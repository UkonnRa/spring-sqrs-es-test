package com.ukonnra.springcqrsestest.shared;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AggregateRepository<A extends AbstractAggregate<?>, Q extends Query> {
  List<A> findAll(final Q query, @Nullable final Integer size);

  List<A> findAllByIds(final Collection<UUID> ids);

  default Optional<A> findOne(final Q query) {
    return this.findAll(query, 1).stream().findFirst();
  }

  default Optional<A> findById(final UUID id) {
    return this.findAllByIds(Set.of(id)).stream().findFirst();
  }

  default List<A> findAll(final Q query) {
    return this.findAll(query, null);
  }
}
