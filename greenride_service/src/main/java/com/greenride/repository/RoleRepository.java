// In: com.greenride.repository.RoleRepository.java
package com.greenride.repository;

import com.greenride.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}