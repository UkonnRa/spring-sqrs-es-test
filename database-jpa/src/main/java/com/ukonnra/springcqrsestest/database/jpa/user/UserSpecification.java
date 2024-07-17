package com.ukonnra.springcqrsestest.database.jpa.user;

import com.ukonnra.springcqrsestest.shared.user.UserQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public record UserSpecification(@Nullable UserQuery value) implements Specification<UserPO> {
  @Override
  public Predicate toPredicate(Root<UserPO> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    query.distinct(true);
    final var predicates = new ArrayList<Predicate>();

    predicates.add(root.get(UserPO_.deletedDate).isNull());

    if (value != null) {
      if (!value.id().isEmpty()) {
        predicates.add(root.get(UserPO_.id).in(value.id()));
      }

      if (!value.loginName().isEmpty()) {
        predicates.add(root.get(UserPO_.loginName).in(value.loginName()));
      }

      if (!value.fullText().isEmpty()) {
        final var fullTextValue = String.format("%%%s%%", value.fullText());

        predicates.add(
            builder.or(
                builder.like(builder.lower(root.get(UserPO_.loginName)), fullTextValue),
                builder.like(builder.lower(root.get(UserPO_.displayName)), fullTextValue)));
      }
    }

    return builder.and(predicates.toArray(new Predicate[] {}));
  }
}
