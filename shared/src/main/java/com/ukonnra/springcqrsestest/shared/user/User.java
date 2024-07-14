package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.AbstractAggregate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(
    name = User.TYPE,
    indexes = {
      @Index(columnList = "displayName"),
      @Index(columnList = "active"),
    })
public class User extends AbstractAggregate<UserEvent> {
  public static final String TYPE = "users";

  @Column(nullable = false, unique = true, length = MAX_NAMELY)
  @Size(min = MIN_NAMELY, max = MAX_NAMELY)
  private String loginName;

  @Column(nullable = false, length = MAX_NAMELY)
  @Size(min = MIN_NAMELY, max = MAX_NAMELY)
  private String displayName;

  private @Nullable Instant deactivatedDate = null;

  public User(String loginName, String displayName) {
    this.loginName = loginName;
    this.displayName = displayName;
  }

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

  public void delete(final Instant timestamp) {
    if (this.deactivatedDate == null) {
      this.deactivatedDate = timestamp;
    }
  }

  public void delete() {
    this.delete(Instant.now());
  }

  @Override
  public final void handleEvent(UserEvent event) {
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
        this.delete(deleted.deactivatedDate());
      }
    }
  }
}
