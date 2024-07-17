package com.ukonnra.springcqrsestest.testsuite;

import com.ukonnra.springcqrsestest.shared.Command;
import com.ukonnra.springcqrsestest.shared.Presentation;
import com.ukonnra.springcqrsestest.shared.Query;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;

public interface TestWriteClient<C extends Command, Q extends Query, P extends Presentation>
    extends TestReadClient<Q, P> {
  Set<P> findAllByIds(@Nullable final UUID operatorId, final Collection<UUID> ids);

  void handleCommand(@Nullable final UUID operatorId, final C command);
}
