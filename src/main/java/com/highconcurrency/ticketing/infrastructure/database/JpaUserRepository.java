package com.highconcurrency.ticketing.infrastructure.database;

import com.highconcurrency.ticketing.domain.user.User;
import com.highconcurrency.ticketing.domain.user.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long>, UserRepository {
}
