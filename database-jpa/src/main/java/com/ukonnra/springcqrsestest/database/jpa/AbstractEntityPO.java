package com.ukonnra.springcqrsestest.database.jpa;

import com.ukonnra.springcqrsestest.shared.AbstractEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.lang.Nullable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@MappedSuperclass
public abstract class AbstractEntityPO<E extends AbstractEntity<?>> {
  @Id private UUID id;

  @CreatedDate private Instant createdDate;

  @Column(nullable = false)
  @Version
  private int version;

  private @Nullable Instant deletedDate = null;

  protected AbstractEntityPO(final E entity) {
    this.id = entity.getId();
    this.createdDate = entity.getCreatedDate();
    this.version = entity.getVersion();
    this.deletedDate = entity.getDeletedDate();
  }

  @Transient
  protected abstract Class<E> getEntityClass();

  public @Nullable E convertToEntity() {
    try {
      final var entity = this.getEntityClass().getConstructor().newInstance();
      entity.setId(this.getId());
      entity.setCreatedDate(this.createdDate);
      entity.setVersion(this.version);
      entity.setDeletedDate(this.deletedDate);
      return entity;
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      return null;
    }
  }

  // Provided by Intellij, I believe it is correct
  @Override
  @SuppressFBWarnings("Eq")
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy proxy
            ? proxy.getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy proxy
            ? proxy.getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    AbstractEntityPO<?> that = (AbstractEntityPO<?>) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy proxy
        ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
