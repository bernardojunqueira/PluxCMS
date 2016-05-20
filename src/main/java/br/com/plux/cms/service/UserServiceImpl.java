package br.com.plux.cms.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.plux.cms.erro.UserAlreadyExistException;
import br.com.plux.cms.model.Role;
import br.com.plux.cms.model.User;
import br.com.plux.cms.repository.RoleRepository;
import br.com.plux.cms.repository.UserRepository;

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
	public User newUser(final UserDto userDto) {
		if (emailExists(userDto.getEmail())) {
			throw new UserAlreadyExistException("There is an account with that email adress: " + userDto.getEmail());
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
	
	private boolean emailExists(final String email) {
		final User user = repository.findByEmail(email);
		if (user != null) {
			return true;
		}
		return false;
	}

}
