package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.Command;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

public sealed interface UserCommand extends Command {
  record Create(String loginName, String displayName, boolean systemAdmin) implements UserCommand {}

  record Update(UUID id, String loginName, String displayName, @Nullable Boolean systemAdmin)
      implements UserCommand {}

  record Delete(UUID id) implements UserCommand {}

  record Batch(Set<Create> create, Set<Update> update, Set<UUID> delete) implements UserCommand {}
}
