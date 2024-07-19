package com.ukonnra.springcqrsestest.shared.journal;

import com.ukonnra.springcqrsestest.shared.AbstractEntity;
import com.ukonnra.springcqrsestest.shared.Permission;
import com.ukonnra.springcqrsestest.shared.user.User;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Journal extends AbstractEntity<JournalEvent> {
  public static final String FIELD_NAME = "name";
  public static final String FIELD_ADMINS = "admins";
  public static final String FIELD_MEMBERS = "members";
  public static final String FIELD_TAGS = "tags";

  public static final int MIN_ADMINS = 1;
  public static final int MAX_ADMINS = 7;
  public static final int MAX_MEMBERS = 15;

  public static final String TYPE = "journals";

  @Size(min = MIN_NAMELY, max = MAX_NAMELY)
  private String name;

  @Size(min = MIN_ADMINS, max = MAX_ADMINS)
  private Set<UUID> admins = new HashSet<>();

  @Size(max = MAX_MEMBERS)
  private Set<UUID> members = new HashSet<>();

  @Size(max = MAX_TAGS)
  private Set<@Size(min = MIN_NAMELY, max = MAX_NAMELY) String> tags = new HashSet<>();

  public void setName(final String name) {
    final var trimmed = name.trim();
    if (trimmed.length() > MAX_NAMELY || trimmed.length() < MIN_NAMELY) {
      // todo: standard error system
      throw new UnsupportedOperationException("Invalid name: " + name);
    }

    this.name = trimmed;
  }

  public void setAdmins(final Collection<UUID> admins) {
    final var values = new HashSet<>(admins);
    if (values.size() > MAX_ADMINS || values.size() < MIN_ADMINS) {
      // todo: standard error system
      throw new UnsupportedOperationException("Invalid admins length: " + admins);
    }

    this.admins = values;
    this.setMembers(this.members);
  }

  public void setMembers(final Collection<UUID> members) {
    final var values =
        members.stream().filter(id -> !this.admins.contains(id)).collect(Collectors.toSet());
    if (values.size() > MAX_MEMBERS) {
      // todo: standard error system
      throw new UnsupportedOperationException("Invalid memebers length: " + admins);
    }

    this.members = values;
  }

  public void setTags(final Collection<String> tags) {
    final var values = new HashSet<String>();
    for (final String tag : tags) {
      final var trimmed = tag.trim();
      if (trimmed.isEmpty()) {
        continue;
      }

      if (trimmed.length() > MAX_NAMELY || trimmed.length() < MIN_NAMELY) {
        // todo: standard error system
        throw new UnsupportedOperationException("Invalid tag: " + tag);
      }
      values.add(trimmed);
    }

    if (values.size() > MAX_TAGS) {
      // todo: standard error system
      throw new UnsupportedOperationException("Invalid tags: " + tags);
    }

    this.tags = values;
  }

  @Override
  protected void doHandleEvent(JournalEvent event) {
    switch (event) {
      case JournalEvent.Created created -> {
        this.setId(created.id());
        this.setCreatedDate(created.createdDate());
        this.setName(created.name());
        this.setAdmins(created.admins());
        this.setMembers(created.members());
        this.setTags(created.tags());
      }
      case JournalEvent.Updated updated -> {
        if (!updated.admins().isEmpty()) {
          this.setAdmins(updated.admins());
        }

        if (updated.members() != null) {
          this.setMembers(updated.members());
        }

        if (updated.tags() != null) {
          this.setTags(updated.tags());
        }
      }
      case JournalEvent.Deleted deleted -> this.delete(deleted.deletedDate());
    }
  }

  public @Nullable Permission getPermission(@Nullable User operator) {
    if (operator == null) {
      return null;
    } else if (operator.getSystemAdmin() || this.admins.contains(operator.getId())) {
      return Permission.ALL;
    } else if (this.members.contains(operator.getId())) {
      return Permission.EMPTY;
    } else {
      return null;
    }
  }
}
