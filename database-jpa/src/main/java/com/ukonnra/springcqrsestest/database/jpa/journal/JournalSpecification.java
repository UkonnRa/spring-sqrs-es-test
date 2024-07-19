package com.ukonnra.springcqrsestest.database.jpa.journal;

import com.ukonnra.springcqrsestest.database.jpa.user.UserPO_;
import com.ukonnra.springcqrsestest.shared.journal.JournalQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public record JournalSpecification(@Nullable JournalQuery value)
    implements Specification<JournalPO> {
  @Override
  public Predicate toPredicate(
      Root<JournalPO> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    query.distinct(true);
    final var predicates = new ArrayList<Predicate>();

    predicates.add(root.get(JournalPO_.deletedDate).isNull());

    if (value != null) {
      if (!value.id().isEmpty()) {
        predicates.add(root.get(JournalPO_.id).in(value.id()));
      }

      if (!value.adminId().isEmpty() || !value.memberId().isEmpty()) {
        final var userRoot = root.join(JournalPO_.journalUsers);

        if (!value.adminId().isEmpty()) {
          predicates.addAll(
              List.of(
                  builder.isTrue(userRoot.get(JournalUserPO_.admin)),
                  userRoot.get(JournalUserPO_.user).get(UserPO_.id).in(value.adminId())));
        }

        if (!value.memberId().isEmpty()) {
          predicates.addAll(
              List.of(
                  builder.isFalse(userRoot.get(JournalUserPO_.admin)),
                  userRoot.get(JournalUserPO_.user).get(UserPO_.id).in(value.memberId())));
        }
      }

      if (!value.tag().isEmpty() || !value.fullText().isEmpty()) {

        final var tagRoot = builder.lower(root.join(JournalPO_.tags));

        if (!value.tag().isEmpty()) {
          predicates.add(tagRoot.in(value.tag()));
        }

        if (!value.fullText().isEmpty()) {
          final var fullTextValue = String.format("%%%s%%", value.fullText());
          predicates.add(
              builder.or(
                  builder.like(builder.lower(root.get(JournalPO_.name)), fullTextValue),
                  builder.like(tagRoot, fullTextValue)));
        }
      }
    }

    return builder.and(predicates.toArray(new Predicate[] {}));
  }
}
