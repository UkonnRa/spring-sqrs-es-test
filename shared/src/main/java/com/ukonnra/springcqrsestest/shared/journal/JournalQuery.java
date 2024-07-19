package com.ukonnra.springcqrsestest.shared.journal;

import com.ukonnra.springcqrsestest.shared.Query;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import org.springframework.lang.Nullable;

@Builder
public record JournalQuery(Set<UUID> id, Set<UUID> adminId, Set<UUID> memberId, String fullText)
    implements Query {
  public JournalQuery(
      @Nullable Set<UUID> id,
      @Nullable Set<UUID> adminId,
      @Nullable Set<UUID> memberId,
      @Nullable String fullText) {
    this.id = id == null ? Set.of() : id;
    this.adminId = adminId == null ? Set.of() : adminId;
    this.memberId = memberId == null ? Set.of() : memberId;
    this.fullText = fullText == null ? "" : fullText.trim().toLowerCase();
  }
}
