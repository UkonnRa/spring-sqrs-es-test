package com.ukonnra.springcqrsestest.endpoint.grpc;

import com.ukonnra.springcqrsestest.database.jpa.DatabaseJpaConfiguration;
import com.ukonnra.springcqrsestest.shared.SharedConfiguration;
import com.ukonnra.springcqrsestest.shared.journal.JournalCommand;
import com.ukonnra.springcqrsestest.shared.journal.JournalService;
import com.ukonnra.springcqrsestest.shared.user.UserCommand;
import com.ukonnra.springcqrsestest.shared.user.UserQuery;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import com.ukonnra.springcqrsestest.shared.user.UserService;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Configuration
@Import({SharedConfiguration.class, DatabaseJpaConfiguration.class})
@AllArgsConstructor
public class ApplicationConfiguration {
  private final UserRepository userRepository;
  private final UserService userService;
  private final JournalService journalService;

  @EventListener(ApplicationReadyEvent.class)
  @Async
  public void onApplicationReady() {
    System.out.println("Application ready");

    final var scope = UUID.randomUUID();

    this.userService.handleCommand(
        null,
        new UserCommand.Batch(
            IntStream.range(0, 10)
                .mapToObj(
                    i ->
                        new UserCommand.Create(
                            String.format("login-%d-%s", i, scope),
                            "Display Name for #" + i,
                            false))
                .collect(Collectors.toSet()),
            null,
            null));

    final var user =
        this.userRepository.findOne(UserQuery.builder().fullText("login").build()).orElseThrow();

    this.journalService.handleCommand(
        user,
        new JournalCommand.Batch(
            IntStream.range(0, 3)
                .mapToObj(
                    i -> new JournalCommand.Create("Journal " + i, Set.of(user.getId()), Set.of()))
                .collect(Collectors.toSet()),
            null,
            null));
  }
}
