package com.ukonnra.springcqrsestest.shared;

import com.ukonnra.springcqrsestest.shared.user.User;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.springframework.lang.Nullable;

public interface ReadService<E extends Entity, Q extends Query, P extends Presentation> {
  Set<P> convert(final Collection<E> entities);

  Set<P> findAll(@Nullable final User operator, final Q query, @Nullable Integer size);

  default Optional<P> findOne(@Nullable final User operator, final Q query) {
    return this.findAll(operator, query, 1).stream().findFirst();
  }
}
