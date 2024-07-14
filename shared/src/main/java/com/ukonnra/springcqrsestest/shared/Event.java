package com.ukonnra.springcqrsestest.shared;

import java.util.UUID;

public interface Event {
  UUID id();

  int version();
}
