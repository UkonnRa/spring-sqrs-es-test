package com.ukonnra.springcqrsestest.shared.journal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ukonnra.springcqrsestest.shared.Event;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = JournalEvent.Created.class, name = "journals:created"),
  @JsonSubTypes.Type(value = JournalEvent.Updated.class, name = "journals:updated"),
  @JsonSubTypes.Type(value = JournalEvent.Deleted.class, name = "journals:deleted"),
})
public sealed interface JournalEvent extends Event {
  @Override
  default String aggregateType() {
    return Journal.TYPE;
  }

  record Created(
      UUID id, int version, Instant createdDate, String name, Set<UUID> admins, Set<UUID> members)
      implements JournalEvent {
    public Created(final JournalCommand.Create command) {
      this(
          UUID.randomUUID(), 0, Instant.now(), command.name(), command.admins(), command.members());
    }
  }

  record Updated(UUID id, int version, String name, Set<UUID> admins, @Nullable Set<UUID> members)
      implements JournalEvent {}

  record Deleted(UUID id, int version, Instant deletedDate) implements JournalEvent {}
}
