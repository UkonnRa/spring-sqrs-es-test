package com.ukonnra.springcqrsestest.database.jpa.user;

import com.ukonnra.springcqrsestest.shared.EventRepository;
import com.ukonnra.springcqrsestest.shared.user.User;
import com.ukonnra.springcqrsestest.shared.user.UserQuery;
import com.ukonnra.springcqrsestest.shared.user.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Getter
public class UserRepositoryImpl implements UserRepository {
  private final EventRepository eventRepository;
  private final UserPORepository userPORepository;

  @Override
  public Set<User> findAll(@Nullable UserQuery query, @Nullable Integer size) {
    final List<UserPO> pos;

    final var specification = query == null ? null : new UserSpecification(query);
    final var page = size != null && size >= 0 ? Pageable.ofSize(size) : null;

    if (specification != null && page != null) {
      pos = this.userPORepository.findAll(specification, page).getContent();
    } else if (specification != null) {
      pos = this.userPORepository.findAll(specification);
    } else if (page != null) {
      pos = this.userPORepository.findAll(page).getContent();
    } else {
      pos = this.userPORepository.findAll();
    }
    return pos.stream().map(UserPO::convertToEntity).collect(Collectors.toSet());
  }

  @Override
  public Set<User> findAllByIds(Collection<UUID> ids) {
    return this.getUserPORepository().findAllById(ids).stream()
        .map(UserPO::convertToEntity)
        .collect(Collectors.toSet());
  }

  @Override
  public void saveAll(Collection<User> entities) {
    final var pos = entities.stream().map(UserPO::new).collect(Collectors.toSet());
    this.userPORepository.saveAllAndFlush(pos);
  }

  @Override
  public void deleteAll(@Nullable Collection<UUID> ids) {
    if (ids == null) {
      this.userPORepository.deleteAllInBatch();
    } else {
      this.userPORepository.deleteAllByIdInBatch(ids);
    }
  }
}
