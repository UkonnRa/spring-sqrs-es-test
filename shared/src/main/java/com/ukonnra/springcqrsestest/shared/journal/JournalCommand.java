package com.ukonnra.springcqrsestest.shared.journal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ukonnra.springcqrsestest.shared.Command;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = JournalCommand.Create.class, name = "users:create"),
  @JsonSubTypes.Type(value = JournalCommand.Update.class, name = "users:update"),
  @JsonSubTypes.Type(value = JournalCommand.Delete.class, name = "users:delete"),
  @JsonSubTypes.Type(value = JournalCommand.Batch.class, name = "users:batch"),
})
public sealed interface JournalCommand extends Command {
  record Create(String name, Set<UUID> admins, Set<UUID> members, Set<String> tags)
      implements JournalCommand {}

  record Update(
      UUID id,
      String name,
      Set<UUID> admins,
      @Nullable Set<UUID> members,
      @Nullable Set<String> tags)
      implements JournalCommand {
    public Update(
        final UUID id,
        @Nullable String name,
        @Nullable Set<UUID> admins,
        @Nullable Set<UUID> members,
        @Nullable Set<String> tags) {
      this.id = id;
      this.name = Optional.ofNullable(name).map(String::trim).orElse("");
      this.admins = admins == null ? Set.of() : admins;
      this.members = members;
      this.tags = tags;
    }

    public boolean empty() {
      return this.name.isEmpty() && this.admins.isEmpty() && this.members == null;
    }
  }

  record Delete(UUID id) implements JournalCommand {}

  record Batch(Set<Create> create, Set<Update> update, Set<UUID> delete) implements JournalCommand {
    public Batch(
        @Nullable Set<Create> create, @Nullable Set<Update> update, @Nullable Set<UUID> delete) {
      this.create = create == null ? Set.of() : create;
      this.update = update == null ? Set.of() : update;
      this.delete = delete == null ? Set.of() : delete;
    }
  }
}
