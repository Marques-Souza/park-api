package com.marquesdev.park_api.repository;

import com.marquesdev.park_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.marquesdev.park_api.enums.Role;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query("select u.role from User u where u.username like :username")
    Role findRoleByUsername(String username);
}
