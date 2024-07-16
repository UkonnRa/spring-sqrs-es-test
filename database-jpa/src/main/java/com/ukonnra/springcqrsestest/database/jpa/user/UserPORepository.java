package com.ukonnra.springcqrsestest.database.jpa.user;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserPORepository
    extends JpaRepository<UserPO, UUID>, JpaSpecificationExecutor<UserPO> {}
