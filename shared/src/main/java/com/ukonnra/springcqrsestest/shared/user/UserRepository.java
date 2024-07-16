package com.ukonnra.springcqrsestest.shared.user;

import com.ukonnra.springcqrsestest.shared.AggregateRepository;

public interface UserRepository extends AggregateRepository<User, UserQuery> {}
