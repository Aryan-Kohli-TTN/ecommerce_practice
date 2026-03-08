package com.bootcamp.repository.role;

import com.bootcamp.enums.Authority;
import com.bootcamp.entity.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoleRepository  extends JpaRepository<Role, UUID> {
    public Role findRoleByAuthority(Authority authority);
}
