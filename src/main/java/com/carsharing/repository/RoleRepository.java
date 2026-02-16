package com.carsharing.repository;

import com.carsharing.model.Role;
import com.carsharing.model.enums.RoleName;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);

    List<Role> findByNameIn(Set<RoleName> roleNames);
}
