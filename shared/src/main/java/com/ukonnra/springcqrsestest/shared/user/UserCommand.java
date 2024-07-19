package com.ukonnra.springcqrsestest.shared.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ukonnra.springcqrsestest.shared.Command;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = UserCommand.Create.class, name = "users:create"),
  @JsonSubTypes.Type(value = UserCommand.Update.class, name = "users:update"),
  @JsonSubTypes.Type(value = UserCommand.Delete.class, name = "users:delete"),
  @JsonSubTypes.Type(value = UserCommand.Batch.class, name = "users:batch"),
})
public sealed interface UserCommand extends Command {
  record Create(String loginName, String displayName, boolean systemAdmin) implements UserCommand {}

  record Update(UUID id, String loginName, String displayName, @Nullable Boolean systemAdmin)
      implements UserCommand {
    public Update(
        final UUID id,
        @Nullable final String loginName,
        @Nullable final String displayName,
        @Nullable final Boolean systemAdmin) {
      this.id = id;
      this.loginName = Optional.ofNullable(loginName).map(String::trim).orElse("");
      this.displayName = Optional.ofNullable(displayName).map(String::trim).orElse("");
      this.systemAdmin = systemAdmin;
    }

    public boolean empty() {
      return this.loginName.isEmpty() && this.displayName.isEmpty() && this.systemAdmin == null;
    }
  }

  record Delete(UUID id) implements UserCommand {}

  record Batch(Set<Create> create, Set<Update> update, Set<UUID> delete) implements UserCommand {
    public Batch(
        @Nullable final Set<Create> create,
        @Nullable final Set<Update> update,
        @Nullable final Set<UUID> delete) {
      this.create = create == null ? Set.of() : create;
      this.update = update == null ? Set.of() : update;
      this.delete = delete == null ? Set.of() : delete;
    }
  }
}
