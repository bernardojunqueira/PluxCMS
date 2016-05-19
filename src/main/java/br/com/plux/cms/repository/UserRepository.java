package br.com.plux.cms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.plux.cms.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByEmail(String email);
	
	@Query("from User u left join fetch u.roles where u.id=?1")
    User findByIdWithRoles(Long id);
	
	@Query("from User u left join fetch u.roles")
    List<User> findAllWithRoles();
}
