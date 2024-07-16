package com.ukonnra.springcqrsestest.database.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukonnra.springcqrsestest.shared.user.UserEvent;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = DatabaseJpaTestConfiguration.class)
@Slf4j
class EventPOTest {
  private final ObjectMapper objectMapper;
  private final EventPORepository eventRepository;

  @Autowired
  public EventPOTest(ObjectMapper objectMapper, EventPORepository eventRepository) {
    this.objectMapper = objectMapper;
    this.eventRepository = eventRepository;
  }

  @Test
  void testEventSerde() throws JsonProcessingException {
    final var events =
        List.<UserEvent>of(
            new UserEvent.Created(
                UUID.fromString("0190bbe0-0bfa-7564-83f4-eb73ae463d2d"),
                0,
                Instant.ofEpochSecond(1721059200),
                "New Login",
                "New Display"),
            new UserEvent.Updated(
                UUID.fromString("0190bbe0-0bfa-7564-83f4-eb73ae463d2d"), 1, "Updated Login", ""),
            new UserEvent.Deleted(
                UUID.fromString("0190bbe0-0bfa-7564-83f4-eb73ae463d2d"),
                2,
                Instant.ofEpochSecond(1723737600)));

    for (final var event : events) {
      final var po = new EventPO(this.objectMapper, event);
      log.info("PO: {}", po);
      this.eventRepository.save(po);
    }
    this.eventRepository.flush();

    final var pos = this.eventRepository.findAll();
    Assertions.assertEquals(events.size(), pos.size());

    for (final var po : pos) {
      final var deser = po.convert(this.objectMapper, UserEvent.class);
      Assertions.assertTrue(events.contains(deser));
    }
  }
}
