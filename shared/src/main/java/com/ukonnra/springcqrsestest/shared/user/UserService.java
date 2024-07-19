package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.AbstractEntity;
import com.ukonnra.springcqrsestest.shared.Event;
import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.WriteService;
import com.ukonnra.springcqrsestest.shared.errors.AlreadyExistedError;
import com.ukonnra.springcqrsestest.shared.errors.NoPermissionError;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

public interface UserService
    extends WriteService<
        User, UserCommand, UserEvent, UserQuery, UserPresentation, UserRepository> {

  @Override
  default Set<UserPresentation> convert(
      @Nullable final User operator, final Collection<User> entities) {
    return entities.stream()
        .map(entity -> new UserPresentation(operator, entity))
        .collect(Collectors.toSet());
  }

  @Override
  default Set<Event> doHandleCommand(@Nullable final User operator, final UserCommand command) {
    return switch (command) {
      case UserCommand.Batch batch -> this.batch(operator, batch);
      case UserCommand.Create create -> this.create(operator, Set.of(create));
      case UserCommand.Delete(UUID id) -> this.delete(operator, Set.of(id));
      case UserCommand.Update update -> this.update(operator, Set.of(update));
    };
  }

  private Set<Event> create(@Nullable final User operator, final Set<UserCommand.Create> commands) {
    final var existingLogins =
        commands.stream().map(UserCommand.Create::loginName).collect(Collectors.toSet());
    final var existings =
        this.getRepository().findAll(UserQuery.builder().loginName(existingLogins).build());

    existings.stream()
        .findFirst()
        .ifPresent(
            model -> {
              throw new AlreadyExistedError(
                  User.TYPE, Map.of(User.FIELD_LOGIN_NAME, model.getLoginName()));
            });

    final var events = new HashSet<Event>();
    for (final var command : commands) {
      if (operator != null && !operator.getSystemAdmin() && command.systemAdmin()) {
        throw new NoPermissionError(User.TYPE, Map.of(User.FIELD_SYSTEM_ADMIN, true));
      }

      events.add(new UserEvent.Created(command));
    }

    return events;
  }

  private Set<Event> update(@Nullable final User operator, final Set<UserCommand.Update> commands) {
    final var ids = commands.stream().map(UserCommand.Update::id).collect(Collectors.toSet());
    final var models =
        this.getRepository().findAllByIds(ids).stream()
            .collect(Collectors.toMap(AbstractEntity::getId, Function.identity()));

    final var loginNames =
        commands.stream()
            .map(UserCommand.Update::loginName)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toSet());
    final var idsByLoginName =
        this.getRepository().findAll(UserQuery.builder().loginName(loginNames).build()).stream()
            .collect(Collectors.toMap(User::getLoginName, User::getId));

    return commands.stream()
        .map(
            command -> {
              final var model = models.get(command.id());
              if (model == null || command.empty()) {
                return null;
              }

              final var permission = model.getPermission(operator);

              if (!command.loginName().isEmpty()) {
                if (!permission.isWriteable(User.FIELD_LOGIN_NAME)) {
                  throw new NoPermissionError(
                      User.TYPE, model.getId(), Set.of(User.FIELD_LOGIN_NAME));
                }

                final var existingId = idsByLoginName.get(command.loginName());
                if (existingId != null && !existingId.equals(model.getId())) {
                  throw new AlreadyExistedError(User.TYPE, model.getId());
                }
              }

              if (!command.displayName().isEmpty()
                  && !permission.isWriteable(User.FIELD_DISPLAY_NAME)) {
                throw new NoPermissionError(
                    User.TYPE, model.getId(), Set.of(User.FIELD_DISPLAY_NAME));
              }

              final var event =
                  new UserEvent.Updated(
                      model.getId(),
                      model.getVersion() + 1,
                      command.loginName(),
                      command.displayName(),
                      command.systemAdmin());
              model.handleEvent(event);

              idsByLoginName.put(model.getLoginName(), model.getId());

              return event;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }

  private Set<Event> delete(@Nullable final User operator, final Collection<UUID> ids) {
    final var models = this.getRepository().findAllByIds(ids);
    final var now = Instant.now();
    return models.stream()
        .map(
            model -> {
              if (!model.getPermission(operator).isWriteable()) {
                throw new NoPermissionError(User.TYPE, model.getId());
              }

              return new UserEvent.Deleted(model.getId(), model.getVersion() + 1, now);
            })
        .collect(Collectors.toSet());
  }

  private Set<Event> batch(@Nullable final User user, final UserCommand.Batch command) {
    final var events = new HashSet<Event>();

    events.addAll(this.create(user, command.create()));
    events.addAll(this.delete(user, command.delete()));
    events.addAll(
        this.update(
            user,
            command.update().stream()
                .filter(c -> !command.delete().contains(c.id()))
                .collect(Collectors.toSet())));

    return events;
  }

  @Service
  @AllArgsConstructor
  @Getter
  class Impl implements UserService {
    private final EventRepository eventRepository;
    private final UserRepository repository;
  }
}
