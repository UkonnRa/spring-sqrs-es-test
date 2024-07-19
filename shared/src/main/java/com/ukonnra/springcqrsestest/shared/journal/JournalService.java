package com.ukonnra.springcqrsestest.shared.journal;

import com.ukonnra.springcqrsestest.shared.Event;
import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.WriteService;
import com.ukonnra.springcqrsestest.shared.errors.NoPermissionError;
import com.ukonnra.springcqrsestest.shared.user.User;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

public interface JournalService
    extends WriteService<
        Journal,
        JournalCommand,
        JournalEvent,
        JournalQuery,
        JournalPresentation,
        JournalRepository> {
  @Override
  default Set<JournalPresentation> convert(
      @Nullable final User operator, final Collection<Journal> entities) {
    return entities.stream()
        .map(entity -> JournalPresentation.of(operator, entity))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  @Override
  default Set<Event> doHandleCommand(@Nullable final User operator, final JournalCommand command) {
    return switch (command) {
      case JournalCommand.Batch batch -> this.batch(operator, batch);
      case JournalCommand.Create create -> this.create(operator, Set.of(create));
      case JournalCommand.Delete delete -> this.delete(operator, Set.of(delete.id()));
      case JournalCommand.Update update -> this.update(operator, Set.of(update));
    };
  }

  private Set<Event> create(
      @Nullable final User operator, final Set<JournalCommand.Create> commands) {

    final var events = new HashSet<Event>();
    for (final var command : commands) {
      if (operator == null) {
        throw new NoPermissionError(Journal.TYPE, Map.of("name", command.name()));
      }

      events.add(new JournalEvent.Created(command));
    }
    return events;
  }

  private Set<Event> update(
      @Nullable final User operator, final Set<JournalCommand.Update> commands) {
    return Set.of();
  }

  private Set<Event> delete(@Nullable final User operator, final Set<UUID> ids) {
    return Set.of();
  }

  private Set<Event> deleteByIds(@Nullable final User operator, final Collection<UUID> ids) {
    final var models = this.getRepository().findAllByIds(ids);
    final var now = Instant.now();
    return models.stream()
        .map(
            model -> {
              final var permission = model.getPermission(operator);
              if (permission == null || !permission.isWriteable()) {
                throw new NoPermissionError(Journal.TYPE, model.getId());
              }

              return new JournalEvent.Deleted(model.getId(), model.getVersion() + 1, now);
            })
        .collect(Collectors.toSet());
  }

  private Set<Event> batch(@Nullable final User operator, final JournalCommand.Batch command) {
    final var events = new HashSet<Event>();

    events.addAll(this.create(operator, command.create()));
    events.addAll(this.deleteByIds(operator, command.delete()));
    events.addAll(
        this.update(
            operator,
            command.update().stream()
                .filter(c -> !command.delete().contains(c.id()))
                .collect(Collectors.toSet())));

    return events;
  }

  @Service
  @AllArgsConstructor
  @Getter
  class Impl implements JournalService {
    private final EventRepository eventRepository;
    private final JournalRepository repository;
  }
}
