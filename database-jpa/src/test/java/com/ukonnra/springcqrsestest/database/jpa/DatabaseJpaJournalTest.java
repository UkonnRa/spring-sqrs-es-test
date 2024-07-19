package com.ukonnra.springcqrsestest.database.jpa;

import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.journal.JournalRepository;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import com.ukonnra.springcqrsestest.testsuite.JournalTest;
import com.ukonnra.springcqrsestest.testsuite.JournalTestClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@Getter
@AllArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
@ContextConfiguration(classes = DatabaseJpaTestConfiguration.class)
public class DatabaseJpaJournalTest implements JournalTest {
  private final EventRepository eventRepository;
  private final JournalTestClient.Impl journalTestClient;
  private final UserRepository userRepository;
  private final JournalRepository journalRepository;
}
