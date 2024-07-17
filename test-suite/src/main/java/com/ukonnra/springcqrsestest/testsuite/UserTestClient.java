package com.ukonnra.springcqrsestest.testsuite;

import com.ukonnra.springcqrsestest.shared.user.User;
import com.ukonnra.springcqrsestest.shared.user.UserCommand;
import com.ukonnra.springcqrsestest.shared.user.UserPresentation;
import com.ukonnra.springcqrsestest.shared.user.UserQuery;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import com.ukonnra.springcqrsestest.shared.user.UserService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

public interface UserTestClient extends TestWriteClient<UserCommand, UserQuery, UserPresentation> {
  @Service
  @AllArgsConstructor
  class Impl implements UserTestClient {
    private final UserService userService;
    private final UserRepository userRepository;

    private @Nullable User getOperator(@Nullable UUID operatorId) {
      return Optional.ofNullable(operatorId).flatMap(this.userRepository::findById).orElse(null);
    }

    @Override
    public Set<UserPresentation> findAllByIds(@Nullable UUID operatorId, Collection<UUID> ids) {
      final var operator = this.getOperator(operatorId);
      final var query = new UserQuery(new HashSet<>(ids), null, null);
      return this.userService.findAll(operator, query, null);
    }

    @Override
    public Set<UserPresentation> findAll(@Nullable UUID operatorId, UserQuery query, Integer size) {
      final var operator = this.getOperator(operatorId);
      return this.userService.findAll(operator, query, null);
    }

    @Override
    public void handleCommand(@Nullable UUID operatorId, UserCommand command) {
      final var operator = this.getOperator(operatorId);
      this.userService.handleCommand(operator, command);
    }
  }
}
