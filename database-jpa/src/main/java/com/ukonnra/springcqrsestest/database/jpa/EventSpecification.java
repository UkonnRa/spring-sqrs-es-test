package com.ukonnra.springcqrsestest.database.jpa;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public record EventSpecification(
    String aggregateType, Collection<UUID> id, @Nullable Integer startVersion)
    implements Specification<EventPO> {
  public EventSpecification(
      String aggregateType, @Nullable Collection<UUID> id, @Nullable Integer startVersion) {
    this.aggregateType = aggregateType;
    this.id = id == null ? Set.of() : id;
    this.startVersion = startVersion;
  }

  @Override
  public Predicate toPredicate(
      Root<EventPO> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    query.distinct(true);
    final var predicates = new ArrayList<Predicate>();

    predicates.add(builder.equal(root.get(EventPO_.id).get("aggregateType"), this.aggregateType));

    if (!this.id.isEmpty()) {
      predicates.add(root.get(EventPO_.id).get("id").in(this.id));
    }

    if (startVersion != null) {
      predicates.add(builder.ge(root.get(EventPO_.id).get("version"), this.startVersion));
    }

    return builder.and(predicates.toArray(new Predicate[] {}));
  }
}
