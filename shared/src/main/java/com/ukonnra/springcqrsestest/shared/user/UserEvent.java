package com.ukonnra.springcqrsestest.shared.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ukonnra.springcqrsestest.shared.Event;
import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = UserEvent.Created.class, name = "users:created"),
  @JsonSubTypes.Type(value = UserEvent.Updated.class, name = "users:updated"),
  @JsonSubTypes.Type(value = UserEvent.Deleted.class, name = "users:deleted"),
})
public sealed interface UserEvent extends Event {
  @Override
  default String aggregateType() {
    return User.TYPE;
  }

  record Created(UUID id, int version, Instant createdDate, String loginName, String displayName)
      implements UserEvent {}

  record Updated(UUID id, int version, String loginName, String displayName) implements UserEvent {}

  record Deleted(UUID id, int version, Instant deletedDate) implements UserEvent {}
}
