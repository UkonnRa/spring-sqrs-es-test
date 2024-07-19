package com.ukonnra.springcqrsestest.shared;

import java.util.Set;
import org.springframework.lang.Nullable;

/**
 * @param fields {@code null} means no write permission; {@link Set#isEmpty()} means write
 *     permission on all fields; otherwise, means write permission on the listed fields
 */
public record Permission(@Nullable Set<String> fields) {
  public static final Permission EMPTY = new Permission(null);
  public static final Permission ALL = new Permission(Set.of());

  public <E extends AbstractEntity<?>> Permission(
      final E entity, final @Nullable Set<String> fields) {
    this(entity.getDeletedDate() != null ? null : fields);
  }

  public boolean isWriteable(String field) {
    if (fields == null) return false;
    else if (fields.isEmpty()) return true;
    else return fields.contains(field);
  }

  public boolean isWriteable() {
    return fields != null;
  }
}
