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
            new UserEvent.Updated(id, 1, "", "Display 1"),
            new UserEvent.Updated(id, 2, "login 2", ""),
            new UserEvent.Deleted(id, 3, now.plus(1, ChronoUnit.DAYS)));

    final var user = new User("new login", "new display");
    user.setVersion(1);

    final var event0 = (UserEvent.Updated) events.get(0);
    user.handleEvent(event0);
    Assertions.assertEquals("new login", user.getLoginName());
    Assertions.assertEquals(event0.displayName(), user.getDisplayName());
    Assertions.assertNull(user.getDeactivatedDate());

    final var event1 = (UserEvent.Updated) events.get(1);
    user.handleEvent(event1);
    Assertions.assertEquals(event1.loginName(), user.getLoginName());
    Assertions.assertEquals(event0.displayName(), user.getDisplayName());
    Assertions.assertNull(user.getDeactivatedDate());

    final var event2 = (UserEvent.Deleted) events.get(2);
    user.handleEvent(event2);
    Assertions.assertEquals(event1.loginName(), user.getLoginName());
    Assertions.assertEquals(event0.displayName(), user.getDisplayName());
    Assertions.assertNotNull(user.getDeactivatedDate());

    final var user1 = new User("new login", "new display");
    user1.setVersion(1);

    user1.handleEvents(events);
    Assertions.assertEquals(user.getLoginName(), user1.getLoginName());
    Assertions.assertEquals(user.getDisplayName(), user1.getDisplayName());
    Assertions.assertNotNull(user1.getDeactivatedDate());
  }
}
