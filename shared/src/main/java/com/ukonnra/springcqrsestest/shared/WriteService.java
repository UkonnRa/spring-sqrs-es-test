package com.ukonnra.springcqrsestest.shared;

import com.ukonnra.springcqrsestest.shared.user.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.lang.Nullable;

public interface WriteService<
        E extends AbstractEntity<V>,
        C extends Command,
        V extends Event,
        Q extends Query,
        P extends Presentation,
        R extends EntityRepository<E, Q, V>>
    extends ReadService<E, Q, P> {
  EventRepository getEventRepository();

  R getRepository();

  Set<Event> doHandleCommand(@Nullable final User user, final C command);

  default void handleCommand(@Nullable final User user, final C command) {
    final var events = this.doHandleCommand(user, command);
    this.getEventRepository().saveAll(events);

    final var ids = events.stream().map(Event::id).collect(Collectors.toSet());
    this.getRepository().refreshSnapshots(ids);
  }

  @Override
  default Set<P> findAll(@Nullable final User operator, final Q query, @Nullable Integer size) {
    final var entities = this.getRepository().findAll(query, size);
    return this.convert(entities);
  }
}
