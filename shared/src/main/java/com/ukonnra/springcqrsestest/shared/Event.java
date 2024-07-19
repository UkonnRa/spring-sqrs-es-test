package com.ukonnra.springcqrsestest.shared;

import java.io.Serializable;
import java.util.UUID;

public interface Event extends Serializable {
  String aggregateType();

  UUID id();

  int version();
}
