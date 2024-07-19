package com.ukonnra.springcqrsestest.testsuite;

import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.journal.JournalCommand;
import com.ukonnra.springcqrsestest.shared.journal.JournalQuery;
import com.ukonnra.springcqrsestest.shared.journal.JournalRepository;
import com.ukonnra.springcqrsestest.shared.user.User;
import com.ukonnra.springcqrsestest.shared.user.UserEvent;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public interface JournalTest {
  EventRepository getEventRepository();

  UserRepository getUserRepository();

  JournalRepository getJournalRepository();

  JournalTestClient getJournalTestClient();

  private User createAdmin() {
    // Setup
    final var operatorId = UUID.randomUUID();
    final var operatorEvent =
        new UserEvent.Created(
            operatorId, 0, Instant.now(), "admin-" + operatorId, "This is an Admin", true);
    this.getEventRepository().saveAll(Set.of(operatorEvent));
    this.getUserRepository().refreshSnapshots(Set.of(operatorId));

    return this.getUserRepository().findById(operatorId).orElseThrow();
  }

  @Test
  default void testCreateJournals() {
    final var normalId = UUID.randomUUID();
    final var normalUserEvent =
        new UserEvent.Created(
            normalId, 0, Instant.now(), "normal-" + normalId, "This is an Normal User", false);
    this.getEventRepository().saveAll(Set.of(normalUserEvent));
    this.getUserRepository().refreshSnapshots(Set.of(normalId));

    final var journalCommand =
        new JournalCommand.Create("New Journal with Admin " + normalId, Set.of(normalId), Set.of());
    this.getJournalTestClient().handleCommand(normalId, journalCommand);

    final var journalsWithAdmin =
        this.getJournalTestClient()
            .findAll(normalId, JournalQuery.builder().adminId(Set.of(normalId)).build(), null);
    Assertions.assertEquals(1, journalsWithAdmin.size());

    final var journalsWithMember =
        this.getJournalTestClient()
            .findAll(normalId, JournalQuery.builder().memberId(Set.of(normalId)).build(), null);
    Assertions.assertTrue(journalsWithMember.isEmpty());

    final var normal2Id = UUID.randomUUID();
    final var normal2UserEvent =
        new UserEvent.Created(
            normal2Id, 0, Instant.now(), "normal-" + normal2Id, "This is an Normal 2 User", false);
    this.getEventRepository().saveAll(Set.of(normal2UserEvent));
    this.getUserRepository().refreshSnapshots(Set.of(normal2Id));

    final var journalsFromNormal2 =
        this.getJournalTestClient().findAll(normal2Id, JournalQuery.builder().build(), null);
    Assertions.assertTrue(journalsFromNormal2.isEmpty());
  }
}
