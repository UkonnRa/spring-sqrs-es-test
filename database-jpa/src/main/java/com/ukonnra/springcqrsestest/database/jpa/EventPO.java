package com.ukonnra.springcqrsestest.database.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukonnra.springcqrsestest.shared.Event;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity(name = EventPO.TYPE)
@Table(name = EventPO.TYPE)
public class EventPO {
  private static final List<String> ID_FIELDS = List.of("aggregateType", "id", "version");

  public static final String TYPE = "events";

  @EmbeddedId private Id id;

  @Column(nullable = false)
  private String blobValue;

  public EventPO(final ObjectMapper objectMapper, final Event event)
      throws JsonProcessingException {
    final var serde = objectMapper.convertValue(event, new TypeReference<Map<String, Object>>() {});
    for (final var key : ID_FIELDS) {
      serde.remove(key);
    }

    this.id = new Id(event.aggregateType(), event.id(), event.version());
    this.blobValue = objectMapper.writeValueAsString(serde);
  }

  public <E extends Event> E convert(final ObjectMapper objectMapper, final Class<E> clazz)
      throws JsonProcessingException {
    final var map =
        objectMapper.readValue(this.blobValue, new TypeReference<Map<String, Object>>() {});
    map.putAll(
        Map.of(
            "aggregateType", this.id.aggregateType, "id", this.id.id, "version", this.id.version));
    return objectMapper.convertValue(map, clazz);
  }

  @Embeddable
  public record Id(String aggregateType, UUID id, int version) {}
}
