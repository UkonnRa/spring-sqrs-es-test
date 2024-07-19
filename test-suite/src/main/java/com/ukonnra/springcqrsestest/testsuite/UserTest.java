package com.ukonnra.springcqrsestest.testsuite;

import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.user.User;
import com.ukonnra.springcqrsestest.shared.user.UserCommand;
import com.ukonnra.springcqrsestest.shared.user.UserEvent;
import com.ukonnra.springcqrsestest.shared.user.UserPresentation;
import com.ukonnra.springcqrsestest.shared.user.UserQuery;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public interface UserTest {
  EventRepository getEventRepository();

  UserTestClient getUserTestClient();

  UserRepository getUserRepository();

  private User createAdmin() {
    // Setup
    final var operatorId = UUID.randomUUID();
    final var operatorEvent =
        new UserEvent.Created(
            operatorId, 0, Instant.now(), "admin-" + operatorId, "This is an Admin", true);
    this.getEventRepository().saveAll(Set.of(operatorEvent));
    this.getUserRepository().refreshSnapshots(Set.of(operatorId));

    return this.getUserRepository().findById(operatorId).orElseThrow();
  }

  @Test
  default void testCreateUser() {
    // Setup
    final var operator = this.createAdmin();

    final var command =
        new UserCommand.Create("new login: " + UUID.randomUUID(), "new display", false);
    this.getUserTestClient().handleCommand(operator.getId(), command);

    final var query = new UserQuery(null, Set.of(command.loginName()), null);

    final var result =
        this.getUserTestClient().findAll(operator.getId(), query, null).stream()
            .findFirst()
            .orElseThrow();
    {
      Assertions.assertEquals(command.loginName(), result.loginName());
      Assertions.assertEquals(command.displayName(), result.displayName());
      Assertions.assertTrue(result.permission().isWriteable(User.FIELD_SYSTEM_ADMIN));
    }

    {
      final var selfResult =
          this.getUserTestClient().findAllByIds(result.id(), Set.of(result.id())).stream()
              .findFirst()
              .orElseThrow();
      Assertions.assertTrue(selfResult.permission().isWriteable(User.FIELD_DISPLAY_NAME));
      Assertions.assertFalse(selfResult.permission().isWriteable(User.FIELD_SYSTEM_ADMIN));
    }

    {
      final var anonymousResult =
          this.getUserTestClient().findAllByIds(null, Set.of(result.id())).stream()
              .findFirst()
              .orElseThrow();
      Assertions.assertFalse(anonymousResult.permission().isWriteable(User.FIELD_DISPLAY_NAME));
    }
  }

  @Test
  default void testBatch() {
    final var operator = this.createAdmin();

    final var scope = UUID.randomUUID();

    final var command =
        new UserCommand.Batch(
            IntStream.range(0, 100)
                .mapToObj(
                    i ->
                        new UserCommand.Create(
                            String.format("Login - %s - %d", scope, i),
                            String.format("Display - %s - %d", scope, i),
                            false))
                .collect(Collectors.toSet()),
            Set.of(),
            Set.of());
    this.getUserTestClient().handleCommand(null, command);

    final var results =
        this.getUserTestClient().findAll(null, new UserQuery(null, null, scope.toString()), null);
    final var ids1 = results.stream().map(UserPresentation::id).toList();
    {
      Assertions.assertEquals(command.create().size(), results.size());

      final var events = this.getEventRepository().findAll(User.TYPE, ids1, null, UserEvent.class);
      Assertions.assertEquals(command.create().size(), events.size());
    }

    final var updateCommand =
        new UserCommand.Batch(
            IntStream.range(100, 200)
                .mapToObj(
                    i ->
                        new UserCommand.Create(
                            String.format("Login - %s - %d", scope, i),
                            String.format("Display - %s - %d", scope, i),
                            false))
                .collect(Collectors.toSet()),
            results.stream()
                .map(
                    result ->
                        new UserCommand.Update(result.id(), "New " + result.loginName(), "", null))
                .collect(Collectors.toSet()),
            Set.of());
    this.getUserTestClient().handleCommand(operator.getId(), updateCommand);

    final var updatedResults =
        this.getUserTestClient().findAll(null, new UserQuery(null, null, scope.toString()), null);
    final var ids2 = updatedResults.stream().map(UserPresentation::id).toList();
    {
      Assertions.assertEquals(
          updateCommand.create().size() + updateCommand.update().size(), updatedResults.size());
      for (final var entity : updatedResults) {
        if (ids1.contains(entity.id())) {
          Assertions.assertTrue(entity.loginName().startsWith("New"));
        } else {
          Assertions.assertTrue(entity.loginName().startsWith("Login"));
        }
      }

      final var events = this.getEventRepository().findAll(User.TYPE, ids2, null, UserEvent.class);
      Assertions.assertEquals(
          command.create().size() + updateCommand.create().size() + updateCommand.update().size(),
          events.size());
    }

    {
      final var idsResults = this.getUserTestClient().findAllByIds(null, ids1);
      Assertions.assertEquals(updateCommand.update().size(), idsResults.size());
      for (final var entity : idsResults) {
        Assertions.assertTrue(entity.loginName().startsWith("New"));
      }
    }

    final var deletedCommand =
        new UserCommand.Batch(
            IntStream.range(200, 300)
                .mapToObj(
                    i ->
                        new UserCommand.Create(
                            String.format("Login - %s - %d", scope, i),
                            String.format("Display - %s - %d", scope, i),
                            false))
                .collect(Collectors.toSet()),
            updatedResults.stream()
                .map(
                    result ->
                        new UserCommand.Update(
                            result.id(), "", "3RD " + result.displayName(), null))
                .collect(Collectors.toSet()),
            new HashSet<>(ids1));
    this.getUserTestClient().handleCommand(operator.getId(), deletedCommand);

    final var deletedResults =
        this.getUserTestClient().findAll(null, new UserQuery(null, null, scope.toString()), null);
    {
      Assertions.assertEquals(
          deletedCommand.create().size()
              + deletedCommand.update().size()
              - deletedCommand.delete().size(),
          deletedResults.size());

      for (final var result : deletedResults) {
        Assertions.assertTrue(result.loginName().startsWith("Login"));
        if (ids2.contains(result.id())) {
          Assertions.assertTrue(result.displayName().startsWith("3RD"));
        } else {
          Assertions.assertTrue(result.displayName().startsWith("Display"));
        }
      }
    }
  }
}
