package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.Event;
import java.time.Instant;
import java.util.UUID;

public sealed interface UserEvent extends Event {
  record Created(UUID id, int version, Instant createdDate, String loginName, String displayName)
      implements UserEvent {}

  record Updated(UUID id, int version, String loginName, String displayName) implements UserEvent {}

  record Deleted(UUID id, int version, Instant deletedDate) implements UserEvent {}
}
