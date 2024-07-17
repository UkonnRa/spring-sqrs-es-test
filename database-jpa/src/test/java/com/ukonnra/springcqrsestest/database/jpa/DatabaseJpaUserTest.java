package com.ukonnra.springcqrsestest.database.jpa;

import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import com.ukonnra.springcqrsestest.testsuite.UserTest;
import com.ukonnra.springcqrsestest.testsuite.UserTestClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@Getter
@AllArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
@ContextConfiguration(classes = DatabaseJpaTestConfiguration.class)
class DatabaseJpaUserTest implements UserTest {
  private final EventRepository eventRepository;
  private final UserTestClient.Impl userTestClient;
  private final UserRepository userRepository;

  @Override
  public UserTestClient getUserTestClient() {
    return this.userTestClient;
  }
}
