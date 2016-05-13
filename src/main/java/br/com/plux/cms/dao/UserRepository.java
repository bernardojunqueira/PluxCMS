package br.com.plux.cms.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.plux.cms.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByEmail(String email);
}