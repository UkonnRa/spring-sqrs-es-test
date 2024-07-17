package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.EntityRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface UserRepository extends EntityRepository<User, UserQuery, UserEvent> {
  @Override
  default String getAggregateType() {
    return User.TYPE;
  }

  @Override
  default Class<UserEvent> getEventClass() {
    return UserEvent.class;
  }

  @Override
  default User getDefaultEntity(UUID id) {
    final var user = new User();
    user.setId(id);
    return user;
  }
}
