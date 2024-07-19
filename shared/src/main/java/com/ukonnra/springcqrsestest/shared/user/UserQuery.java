package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.Query;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record UserQuery(Set<UUID> id, Set<String> loginName, String fullText) implements Query {
  public UserQuery(
      @Nullable final Set<UUID> id,
      @Nullable final Set<String> loginName,
      @Nullable final String fullText) {
    this.id = id == null ? Set.of() : id;
    this.loginName =
        loginName == null
            ? Set.of()
            : loginName.stream().map(String::trim).collect(Collectors.toSet());
    this.fullText = fullText == null ? "" : fullText.trim().toLowerCase();
  }
}
