package com.ukonnra.springcqrsestest.testsuite;

import com.ukonnra.springcqrsestest.shared.user.User;
import com.ukonnra.springcqrsestest.shared.user.UserCommand;
import com.ukonnra.springcqrsestest.shared.user.UserQuery;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import com.ukonnra.springcqrsestest.shared.user.UserService;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

public interface UserTestClient extends TestWriteClient<UserCommand, UserQuery, User> {
  @Service
  @AllArgsConstructor
  class Impl implements UserTestClient {
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public Set<User> findAllByIds(UUID operatorId, Collection<UUID> ids) {
      final var operator = this.userRepository.findById(operatorId).orElse(null);
      final var query = new UserQuery(new HashSet<>(ids), null, null);
      return this.userService.findAll(operator, query, null);
    }

    @Override
    public Set<User> findAll(UUID operatorId, UserQuery query, Integer size) {
      final var operator = this.userRepository.findById(operatorId).orElse(null);
      return this.userService.findAll(operator, query, null);
    }

    @Override
    public void handleCommand(UUID operatorId, UserCommand command) {
      final var operator = this.userRepository.findById(operatorId).orElse(null);
      this.userService.handleCommand(operator, command);
    }
  }
}
