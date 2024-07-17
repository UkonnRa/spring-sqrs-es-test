package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.AbstractEntity;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractEntity<UserEvent> {
  public static final String TYPE = "users";

  public static final String FIELD_LOGIN_NAME = "loginName";
  public static final String FIELD_DISPLAY_NAME = "displayName";
  public static final String FIELD_SYSTEM_ADMIN = "systemAdmin";

  @Size(min = MIN_NAMELY, max = MAX_NAMELY)
  private String loginName;

  @Size(min = MIN_NAMELY, max = MAX_NAMELY)
  private String displayName;

  private boolean systemAdmin = false;

  public void setLoginName(String value) {
    final var trimmed = value.trim();
    if (trimmed.length() > MAX_NAMELY || trimmed.length() < MIN_NAMELY) {
      // todo: standard error system
      throw new UnsupportedOperationException("Invalid login name: " + loginName);
    }

    this.loginName = trimmed;
  }

  public void setDisplayName(String value) {
    final var trimmed = value.trim();
    if (trimmed.length() > MAX_NAMELY || trimmed.length() < MIN_NAMELY) {
      // todo: standard error system
      throw new UnsupportedOperationException("Invalid login name: " + loginName);
    }

    this.displayName = trimmed;
  }

  @Override
  public final void doHandleEvent(UserEvent event) {
    switch (event) {
      case UserEvent.Created created -> {
        this.setId(created.id());
        this.setCreatedDate(created.createdDate());
        this.setLoginName(created.loginName());
        this.setDisplayName(created.displayName());
        this.setSystemAdmin(created.systemAdmin());
      }
      case UserEvent.Updated updated -> {
        if (!updated.loginName().isEmpty()) {
          this.setLoginName(updated.loginName());
        }
        if (!updated.displayName().isEmpty()) {
          this.setDisplayName(updated.displayName());
        }

        Optional.ofNullable(updated.systemAdmin()).ifPresent(value -> this.systemAdmin = value);
      }
      case UserEvent.Deleted deleted -> this.delete(deleted.deletedDate());
    }
  }

  /**
   * @param operator The auth user in this query; can be {@code null}.
   * @return {@code null} means no write permission; {@link Set#isEmpty()} means write permission on
   *     all fields; otherwise, means write permission on the listed fields
   */
  public @Nullable Set<String> calculateWritePermission(@Nullable User operator) {
    if (operator == null) {
      return null;
    } else if (operator.systemAdmin) {
      return Set.of();
    } else if (operator.getId().equals(this.getId())) {
      return Set.of(FIELD_LOGIN_NAME, FIELD_DISPLAY_NAME);
    } else {
      return null;
    }
  }
}
