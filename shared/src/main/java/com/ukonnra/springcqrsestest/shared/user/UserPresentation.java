package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.Permission;
import com.ukonnra.springcqrsestest.shared.Presentation;
import java.time.Instant;
import java.util.UUID;
import org.springframework.lang.Nullable;

public record UserPresentation(
    UUID id,
    Instant createdDate,
    int version,
    @Nullable Instant deletedDate,
    String loginName,
    String displayName,
    boolean systemAdmin,
    Permission permission)
    implements Presentation {
  public UserPresentation(@Nullable final User operator, final User entity) {
    this(
        entity.getId(),
        entity.getCreatedDate(),
        entity.getVersion(),
        entity.getDeletedDate(),
        entity.getLoginName(),
        entity.getDisplayName(),
        entity.getSystemAdmin(),
        entity.getPermission(operator));
  }
}
