package com.ukonnra.springcqrsestest.shared;

import com.ukonnra.springcqrsestest.shared.user.User;
import java.util.Set;
import org.springframework.lang.Nullable;

public interface WriteService<
        A extends AbstractAggregate<?>,
        C extends Command,
        Q extends Query,
        P extends Presentation,
        R extends AggregateRepository<A, Q>>
    extends ReadService<A, Q, P> {
  EventRepository getEventRepository();

  R getRepository();

  Set<Event> doHandleCommand(final User user, final C command);

  default void handleCommand(final User user, final C command) {
    final var events = this.doHandleCommand(user, command);
    this.getEventRepository().saveAll(events);
  }

  @Override
  default Set<P> findAll(@Nullable final User operator, final Q query, @Nullable Integer size) {
    final var entities = this.getRepository().findAll(query, size);
    return this.convert(entities);
  }
}
