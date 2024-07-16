package com.ukonnra.springcqrsestest.database.jpa;

import com.ukonnra.springcqrsestest.shared.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.lang.Nullable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class AbstractEntityPO<E extends AbstractEntity<?>>
    extends AbstractPersistable<UUID> {
  @CreatedDate private Instant createdDate;

  @Column(nullable = false)
  @Version
  private int version;

  private @Nullable Instant deletedDate = null;

  protected AbstractEntityPO(final E entity) {
    this.setId(entity.getId());
    this.createdDate = entity.getCreatedDate();
    this.version = entity.getVersion();
    this.deletedDate = entity.getDeletedDate();
  }

  protected void load(final E entity) {
    entity.setId(this.getId());
    entity.setCreatedDate(this.createdDate);
    entity.setVersion(this.version);
    entity.setDeletedDate(this.deletedDate);
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
