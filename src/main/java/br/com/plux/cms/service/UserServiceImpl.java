package br.com.plux.cms.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.plux.cms.model.Role;
import br.com.plux.cms.model.User;
import br.com.plux.cms.repository.RoleRepository;
import br.com.plux.cms.repository.UserRepository;
import br.com.plux.cms.validation.EmailExistsException;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	

	@Override
	public User createUser(UserDto userDto) throws EmailExistsException {
		if (emailExists(userDto.getEmail())) {
			throw new EmailExistsException("There is an account with that email adress: " + userDto.getEmail());
		}
		final User user = new User();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		user.setEmail(userDto.getEmail());
		
		List<Role> roles = new ArrayList<>();
		
		for (Long roleId : userDto.getRoles()) {
			roles.add(roleRepository.findOne(roleId));
		}
		
		user.setRoles(roles);
		
		return repository.save(user);
	}
	
	@Override
	public User findById(Long id) {
		User user = repository.findOne(id);
		Hibernate.initialize(user.getRoles());
		return user;
	}
	
	@Override
	public List<User> findAllUsers() {
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "firstName"));
		return repository.findAll(sort);
	}
	
	@Override
	public void updateUser(User user) {
		User entity = repository.findOne(user.getId());
		System.out.println(entity.getRoles());
		
		if (entity != null) {
			
			for (Role role : user.getRoles()) {
				role.setId(roleRepository.findByName(role.getName()).getId());
				System.out.println(role);
			}
			
			entity.setFirstName(user.getFirstName());
			entity.setLastName(user.getLastName());
			entity.setEmail(user.getEmail());
			entity.setRoles(user.getRoles());
			entity.setEnabled(user.isEnabled());
			
		}
		
	}
	
	@Override
	public void deleteUser(Long id) {
		repository.delete(id);
		
	}
	
	private boolean emailExists(final String email) {
		final User user = repository.findByEmail(email);
		if (user != null) {
			return true;
		}
		return false;
	}

	

	

	

}
