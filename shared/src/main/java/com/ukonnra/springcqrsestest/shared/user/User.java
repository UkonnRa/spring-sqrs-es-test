package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.AbstractEntity;
import com.ukonnra.springcqrsestest.shared.Presentation;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractEntity<UserEvent> implements Presentation {
  public static final String TYPE = "users";

  @Size(min = MIN_NAMELY, max = MAX_NAMELY)
  private String loginName;

  @Size(min = MIN_NAMELY, max = MAX_NAMELY)
  private String displayName;

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
      }
      case UserEvent.Updated updated -> {
        if (!updated.loginName().isEmpty()) {
          this.setLoginName(updated.loginName());
        }
        if (!updated.displayName().isEmpty()) {
          this.setDisplayName(updated.displayName());
        }
      }
      case UserEvent.Deleted deleted -> {
        this.delete(deleted.deletedDate());
      }
    }
  }
}
