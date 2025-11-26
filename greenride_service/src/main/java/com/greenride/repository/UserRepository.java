package com.greenride.repository;

import com.greenride.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // Added for security checks
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
