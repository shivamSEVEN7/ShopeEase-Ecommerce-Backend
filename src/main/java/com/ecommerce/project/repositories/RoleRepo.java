package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByRoleName(RoleName roleName);
}
