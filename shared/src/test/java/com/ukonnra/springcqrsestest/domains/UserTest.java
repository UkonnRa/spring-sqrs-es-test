package com.ukonnra.springcqrsestest.domains;

import com.ukonnra.springcqrsestest.shared.user.User;
import com.ukonnra.springcqrsestest.shared.user.UserEvent;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTest {
  @Test
  void testHandleEvents() {
    final var id = UUID.randomUUID();
    final var now = Instant.now();

    final var events =
        List.<UserEvent>of(
            new UserEvent.Created(id, 0, Instant.now(), "new login", "new display"),
            new UserEvent.Updated(id, 1, "", "Display 1"),
            new UserEvent.Updated(id, 2, "login 2", ""),
            new UserEvent.Deleted(id, 3, now.plus(1, ChronoUnit.DAYS)));

    final var user = new User();

    final var event0 = (UserEvent.Created) events.get(0);
    user.handleEvent(event0);
    Assertions.assertEquals(event0.loginName(), user.getLoginName());
    Assertions.assertEquals(event0.displayName(), user.getDisplayName());
    Assertions.assertNull(user.getDeletedDate());
    Assertions.assertEquals(0, user.getVersion());

    final var event1 = (UserEvent.Updated) events.get(1);
    user.handleEvent(event1);
    Assertions.assertEquals(event0.loginName(), user.getLoginName());
    Assertions.assertEquals(event1.displayName(), user.getDisplayName());
    Assertions.assertNull(user.getDeletedDate());
    Assertions.assertEquals(1, user.getVersion());

    final var event2 = (UserEvent.Updated) events.get(2);
    user.handleEvent(event2);
    Assertions.assertEquals(event2.loginName(), user.getLoginName());
    Assertions.assertEquals(event1.displayName(), user.getDisplayName());
    Assertions.assertNull(user.getDeletedDate());
    Assertions.assertEquals(2, user.getVersion());

    final var event3 = (UserEvent.Deleted) events.get(3);
    user.handleEvent(event3);
    Assertions.assertEquals(event2.loginName(), user.getLoginName());
    Assertions.assertEquals(event1.displayName(), user.getDisplayName());
    Assertions.assertNotNull(user.getDeletedDate());
    Assertions.assertEquals(3, user.getVersion());

    final var user1 = new User();

    user1.handleEvents(events);
    Assertions.assertEquals(user.getLoginName(), user1.getLoginName());
    Assertions.assertEquals(user.getDisplayName(), user1.getDisplayName());
    Assertions.assertNotNull(user1.getDeletedDate());
  }
}
