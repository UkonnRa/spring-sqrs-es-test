package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.Command;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

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

  record Batch(Set<Create> create, Set<Update> update, Set<UUID> delete) implements UserCommand {}
}
