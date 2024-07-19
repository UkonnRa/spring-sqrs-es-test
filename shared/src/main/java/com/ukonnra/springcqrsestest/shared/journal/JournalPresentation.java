package com.ukonnra.springcqrsestest.shared.journal;

import com.ukonnra.springcqrsestest.shared.Permission;
import com.ukonnra.springcqrsestest.shared.Presentation;
import com.ukonnra.springcqrsestest.shared.user.User;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record JournalPresentation(
    UUID id,
    Instant createdDate,
    int version,
    @Nullable Instant deletedDate,
    String name,
    Set<UUID> admins,
    Set<UUID> members,
    Permission permission)
    implements Presentation {
  public static @Nullable JournalPresentation of(
      @Nullable final User operator, final Journal entity) {
    final var permission = entity.getPermission(operator);

    if (permission == null) return null;

    return new JournalPresentation(
        entity.getId(),
        entity.getCreatedDate(),
        entity.getVersion(),
        entity.getDeletedDate(),
        entity.getName(),
        entity.getAdmins(),
        entity.getMembers(),
        permission);
  }
}
