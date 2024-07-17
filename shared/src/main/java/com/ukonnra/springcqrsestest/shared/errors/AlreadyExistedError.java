package com.ukonnra.springcqrsestest.shared.errors;

import java.net.URI;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class AlreadyExistedError extends AbstractError {
  public static final String TITLE = "Entity Already Existed";
  public static final URI TYPE =
      URI.create("urn:com.ukonnra:white-rabbit:errors:entity-already-existed");

  public AlreadyExistedError(final String type, final Map<String, Object> values) {
    super(HttpStatus.BAD_REQUEST);
    this.setTitle(TITLE);
    this.setType(TYPE);
    this.setDetail(
        String.format(
            "Entity[type = %s, %s] is already existed",
            type, AbstractError.convertValueMapToString(values)));
    this.getBody().setProperties(Map.of("type", type, "values", values));
  }
}
