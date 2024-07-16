package com.ukonnra.springcqrsestest.shared;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

public interface EventRepository {
  <E extends Event> List<E> findAll(
      final String aggregateType, final Set<UUID> id, @Nullable final Integer startVersion);

  void saveAll(final Collection<Event> events);
}
