package com.ukonnra.springcqrsestest.shared.errors;

import com.ukonnra.springcqrsestest.shared.AbstractEntity;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpStatus;

public class NoPermissionError extends AbstractError {
  public static final String TITLE = "No Permission";
  public static final URI TYPE = URI.create("urn:com.ukonnra:white-rabbit:errors:no-permission");

  public NoPermissionError(
      final String type, final Map<String, Object> values, final Set<String> fields) {
    super(HttpStatus.BAD_REQUEST);
    this.setTitle(TITLE);
    this.setType(TYPE);
    this.setDetail(
        String.format(
            "You don't has Permissions when editing Entity[type = %s, %s]%s",
            type,
            AbstractError.convertValueMapToString(values),
            fields.isEmpty() ? "" : String.format("on Field[%s]", String.join(", ", fields))));

    this.getBody().setProperties(Map.of("type", type, "values", values, "fields", fields));
  }

  public NoPermissionError(final String type, final Map<String, Object> values) {
    this(type, values, Set.of());
  }

  public NoPermissionError(final String type, final UUID id) {
    this(type, Map.of(AbstractEntity.FIELD_ID, id));
  }

  public NoPermissionError(final String type, final UUID id, final Set<String> fields) {
    this(type, Map.of(AbstractEntity.FIELD_ID, id), fields);
  }
}
