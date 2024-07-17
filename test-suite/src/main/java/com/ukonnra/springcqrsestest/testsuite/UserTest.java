package com.ukonnra.springcqrsestest.testsuite;

import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.user.UserCommand;
import com.ukonnra.springcqrsestest.shared.user.UserEvent;
import com.ukonnra.springcqrsestest.shared.user.UserQuery;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public interface UserTest {
  EventRepository getEventRepository();

  UserTestClient getUserTestClient();

  UserRepository getUserRepository();

  @Test
  default void testCreateUser() throws InterruptedException {
    // Setup
    final var operatorId = UUID.fromString("0190be48-0e2e-7bbb-8b0b-a6ebd4ba1c1f");
    final var operatorEvent =
        new UserEvent.Created(operatorId, 0, Instant.now(), "admin-1", "This is an Admin");
    {
      this.getEventRepository().saveAll(Set.of(operatorEvent));
      this.getUserRepository().refreshSnapshots(Set.of(operatorId));
    }

    final var command = new UserCommand.Create("new login", "new display");
    this.getUserTestClient().handleCommand(operatorId, command);

    final var query = new UserQuery(null, Set.of(command.loginName()), null);
    {
      final var results =
          this.getUserTestClient().findAll(operatorId, query, null).stream().toList();
      Assertions.assertEquals(1, results.size());

      final var result = results.getFirst();
      Assertions.assertEquals(command.loginName(), result.getLoginName());
      Assertions.assertEquals(command.displayName(), result.getDisplayName());
    }

    {
      final var results = this.getUserTestClient().findAll(operatorId, null, null);
      Assertions.assertEquals(2, results.size());

      final var loadedOperator =
          results.stream().filter(e -> e.getId().equals(operatorId)).findFirst().get();
      Assertions.assertEquals(operatorEvent.loginName(), loadedOperator.getLoginName());
      Assertions.assertEquals(operatorEvent.displayName(), loadedOperator.getDisplayName());
    }
  }
}
