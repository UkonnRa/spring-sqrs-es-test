package com.ukonnra.springcqrsestest.testsuite;

import com.ukonnra.springcqrsestest.shared.Presentation;
import com.ukonnra.springcqrsestest.shared.Query;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

public interface TestReadClient<Q extends Query, P extends Presentation> {
  Set<P> findAll(@Nullable final UUID operatorId, final Q query, @Nullable Integer size);
}
