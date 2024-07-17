package com.ukonnra.springcqrsestest.database.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukonnra.springcqrsestest.shared.Event;
import com.ukonnra.springcqrsestest.shared.EventRepository;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
public class EventRepositoryImpl implements EventRepository {
  private final EventPORepository eventPORepository;
  private final ObjectMapper objectMapper;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public <E extends Event> List<E> findAll(
      String aggregateType,
      @Nullable Collection<UUID> id,
      @Nullable Integer startVersion,
      Class<E> eventClass) {
    final var pos =
        this.eventPORepository.findAll(new EventSpecification(aggregateType, id, startVersion));
    return pos.stream()
        .map(po -> po.convert(this.objectMapper, eventClass))
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public void doSaveAll(Collection<Event> events) {
    final var pos =
        events.stream()
            .map(
                e -> {
                  try {
                    return new EventPO(this.objectMapper, e);
                  } catch (final JsonProcessingException ex) {
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    this.eventPORepository.saveAllAndFlush(pos);
  }
}
