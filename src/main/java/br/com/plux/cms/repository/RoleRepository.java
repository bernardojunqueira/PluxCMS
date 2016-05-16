package br.com.plux.cms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.plux.cms.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

    @Override
    void delete(Role role);

}
