package com.ukonnra.springcqrsestest.endpoint.grpc;

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
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

public interface ApplicationListener {
  Logger LOGGER = LoggerFactory.getLogger(ApplicationListener.class);

  UserRepository getUserRepository();

  UserService getUserService();

  JournalService getJournalService();

  @EventListener(ApplicationReadyEvent.class)
  @Async
  default void onApplicationReady() {
    LOGGER.info("Application ready");

    final var scope = UUID.randomUUID();

    this.getUserService()
        .handleCommand(
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
        this.getUserRepository()
            .findOne(UserQuery.builder().fullText("login").build())
            .orElseThrow();

    this.getJournalService()
        .handleCommand(
            user,
            new JournalCommand.Batch(
                IntStream.range(0, 3)
                    .mapToObj(
                        i ->
                            new JournalCommand.Create(
                                "Journal " + i,
                                Set.of(user.getId()),
                                Set.of(),
                                Set.of("Tag 1", "  Tag 2 ", "", "  ")))
                    .collect(Collectors.toSet()),
                null,
                null));
  }

  @Service
  @AllArgsConstructor
  @Getter
  class Impl implements ApplicationListener {
    private final UserRepository userRepository;
    private final UserService userService;
    private final JournalService journalService;
  }
}
