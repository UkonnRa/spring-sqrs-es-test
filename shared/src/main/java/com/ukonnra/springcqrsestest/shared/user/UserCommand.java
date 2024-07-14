package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.Command;
import java.util.Set;
import java.util.UUID;

public sealed interface UserCommand extends Command {
  record Create(String loginName, String displayName) implements UserCommand {}

  record Update(UUID id, String loginName, String displayName) implements UserCommand {}

  record Delete(UUID id) implements UserCommand {}

  record Batch(Set<Create> create, Set<Update> update, Set<UUID> delete) implements UserCommand {}
}
