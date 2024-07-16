package com.ukonnra.springcqrsestest.shared;

import java.util.UUID;

public interface Event {
  String aggregateType();

  UUID id();

  int version();
}
