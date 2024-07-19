package com.ukonnra.springcqrsestest.testsuite;

import com.ukonnra.springcqrsestest.shared.journal.JournalCommand;
import com.ukonnra.springcqrsestest.shared.journal.JournalPresentation;
import com.ukonnra.springcqrsestest.shared.journal.JournalQuery;
import com.ukonnra.springcqrsestest.shared.journal.JournalService;
import com.ukonnra.springcqrsestest.shared.user.User;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

public interface JournalTestClient
    extends TestWriteClient<JournalCommand, JournalQuery, JournalPresentation> {
  @Service
  @AllArgsConstructor
  class Impl implements JournalTestClient {
    private final UserRepository userRepository;
    private final JournalService journalService;

    private @Nullable User getOperator(@Nullable UUID operatorId) {
      return Optional.ofNullable(operatorId).flatMap(this.userRepository::findById).orElse(null);
    }

    @Override
    public Set<JournalPresentation> findAllByIds(@Nullable UUID operatorId, Collection<UUID> ids) {
      final var operator = this.getOperator(operatorId);
      final var query = new JournalQuery(new HashSet<>(ids), null, null, null);
      return this.journalService.findAll(operator, query, null);
    }

    @Override
    public Set<JournalPresentation> findAll(
        @Nullable UUID operatorId, JournalQuery query, @Nullable Integer size) {
      final var operator = this.getOperator(operatorId);
      return this.journalService.findAll(operator, query, null);
    }

    @Override
    public void handleCommand(@Nullable UUID operatorId, JournalCommand command) {
      final var operator = this.getOperator(operatorId);
      this.journalService.handleCommand(operator, command);
    }
  }
}
