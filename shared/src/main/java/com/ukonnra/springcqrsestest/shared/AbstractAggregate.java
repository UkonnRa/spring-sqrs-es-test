package com.ukonnra.springcqrsestest.shared;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class AbstractAggregate<E extends Event> extends AbstractPersistable<UUID> {
  public static final int MIN_NAMELY = 2;
  public static final int MAX_NAMELY = 127;
  public static final int MAX_LONG_TEXT = 1023;
  public static final int MAX_TAGS = 15;

  @Column(nullable = false)
  @CreatedDate
  private Instant createdDate = Instant.now();

  @Version private int version;

  @Override
  public final boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public final int hashCode() {
    return super.hashCode();
  }

  public void handleEvents(final Collection<E> events) {
    events.stream()
        .sorted(Comparator.comparing(Event::version))
        .forEachOrdered(
            event -> {
              if (event.version() == this.version) {
                this.handleEvent(event);
                this.version++;
              } else {
                // todo: invalid event version
                throw new UnsupportedOperationException("Invalid event version");
              }
            });
  }

  protected abstract void handleEvent(final E event);
}
