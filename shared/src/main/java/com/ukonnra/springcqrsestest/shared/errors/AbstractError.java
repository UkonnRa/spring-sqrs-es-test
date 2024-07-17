package com.ukonnra.springcqrsestest.shared.errors;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponseException;

public abstract class AbstractError extends ErrorResponseException {
  protected static String convertValueMapToString(final Map<String, Object> map) {
    return map.entrySet().stream()
        .map(e -> String.format("%s = %s", e.getKey(), e.getValue()))
        .collect(Collectors.joining(", "));
  }

  protected AbstractError(final HttpStatusCode status) {
    super(status);
  }
}
