package com.ukonnra.springcqrsestest.shared.journal;

import com.ukonnra.springcqrsestest.shared.EntityRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface JournalRepository extends EntityRepository<Journal, JournalQuery, JournalEvent> {
  @Override
  default String getAggregateType() {
    return Journal.TYPE;
  }

  @Override
  default Class<JournalEvent> getEventClass() {
    return JournalEvent.class;
  }

  @Override
  default Journal getDefaultEntity(UUID id) {
    final var model = new Journal();
    model.setId(id);
    return model;
  }
}
