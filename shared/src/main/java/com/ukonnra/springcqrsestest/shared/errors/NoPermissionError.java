package com.ukonnra.springcqrsestest.shared.errors;

import java.net.URI;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class NoPermissionError extends AbstractError {
  public static final String TITLE = "No Permission";
  public static final URI TYPE = URI.create("urn:com.ukonnra:white-rabbit:errors:no-permission");

  public NoPermissionError(final String type, final Map<String, Object> values) {
    super(HttpStatus.BAD_REQUEST);
    this.setTitle(TITLE);
    this.setType(TYPE);
    this.setDetail(
        String.format(
            "You don't has Permissions when editing Entity[type = %s, %s]",
            type, AbstractError.convertValueMapToString(values)));
    this.getBody().setProperties(Map.of("type", type, "values", values));
  }
}
