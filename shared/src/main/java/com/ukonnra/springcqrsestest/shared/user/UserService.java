package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.AbstractEntity;
import com.ukonnra.springcqrsestest.shared.Event;
import com.ukonnra.springcqrsestest.shared.WriteService;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface UserService
    extends WriteService<User, UserCommand, UserQuery, User, UserRepository> {
  @Override
  default Set<Event> doHandleCommand(final User user, final UserCommand command) {
    return switch (command) {
      case UserCommand.Batch batch -> this.batch(user, batch);
      case UserCommand.Create create -> this.create(user, Set.of(create));
      case UserCommand.Delete delete -> this.delete(user, Set.of(delete));
      case UserCommand.Update update -> this.update(user, Set.of(update));
    };
  }

  @Override
  default Set<User> convert(final Collection<User> entities) {
    return new HashSet<>(entities);
  }

  private Set<Event> create(final User user, final Set<UserCommand.Create> commands) {
    return commands.stream()
        .map(
            command ->
                new UserEvent.Created(
                    UUID.randomUUID(),
                    0,
                    Instant.now(),
                    command.loginName(),
                    command.displayName()))
        .collect(Collectors.toSet());
  }

  private Set<Event> update(final User user, final Set<UserCommand.Update> commands) {
    final var ids = commands.stream().map(UserCommand.Update::id).collect(Collectors.toSet());
    final var models =
        this.getRepository().findAllByIds(ids).stream()
            .collect(Collectors.toMap(AbstractEntity::getId, Function.identity()));
    return commands.stream()
        .map(
            command -> {
              final var model = models.get(command.id());
              if (model == null) {
                return null;
              }

              model.setVersion(model.getVersion() + 1);
              return new UserEvent.Updated(
                  model.getId(), model.getVersion(), command.loginName(), command.displayName());
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  private Set<Event> delete(final User user, final Collection<UserCommand.Delete> commands) {
    return this.deleteByIds(user, commands.stream().map(c -> c.id()).collect(Collectors.toSet()));
  }

  private Set<Event> deleteByIds(final User user, final Collection<UUID> ids) {
    final var models = this.getRepository().findAllByIds(ids);
    return models.stream()
        .map(model -> new UserEvent.Deleted(model.getId(), model.getVersion() + 1, Instant.now()))
        .collect(Collectors.toSet());
  }

  private Set<Event> batch(final User user, final UserCommand.Batch command) {
    return Stream.of(
            this.create(user, command.create()),
            this.update(user, command.update()),
            this.deleteByIds(user, command.delete()))
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }
}
