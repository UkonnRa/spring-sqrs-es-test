package com.ukonnra.springcqrsestest.database.jpa.journal;

import com.ukonnra.springcqrsestest.database.jpa.user.UserPO_;
import com.ukonnra.springcqrsestest.shared.journal.JournalQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.UUID;
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

      if (!value.adminId().isEmpty()) {
        var subquery = query.subquery(UUID.class);
        final var subroot = subquery.from(JournalUserPO.class);
        subquery =
            subquery
                .select(subroot.get(JournalUserPO_.journal).get(JournalPO_.id))
                .distinct(true)
                .where(
                    builder.and(
                        subroot.get(JournalUserPO_.user).get(UserPO_.id).in(value.adminId()),
                        builder.isTrue(subroot.get(JournalUserPO_.admin)),
                        builder.equal(
                            root.get(JournalPO_.id),
                            subroot.get(JournalUserPO_.journal).get(JournalPO_.id))));
        predicates.add(builder.exists(subquery));
      }

      if (!value.memberId().isEmpty()) {
        var subquery = query.subquery(UUID.class);
        final var subroot = subquery.from(JournalUserPO.class);
        subquery =
            subquery
                .select(subroot.get(JournalUserPO_.journal).get(JournalPO_.id))
                .distinct(true)
                .where(
                    builder.and(
                        subroot.get(JournalUserPO_.user).get(UserPO_.id).in(value.memberId()),
                        builder.isFalse(subroot.get(JournalUserPO_.admin)),
                        builder.equal(
                            root.get(JournalPO_.id),
                            subroot.get(JournalUserPO_.journal).get(JournalPO_.id))));
        predicates.add(builder.exists(subquery));
      }

      if (!value.fullText().isEmpty()) {
        final var fullTextValue = String.format("%%%s%%", value.fullText());
        predicates.add(builder.like(builder.lower(root.get(JournalPO_.name)), fullTextValue));
      }
    }

    return builder.and(predicates.toArray(new Predicate[] {}));
  }
}
