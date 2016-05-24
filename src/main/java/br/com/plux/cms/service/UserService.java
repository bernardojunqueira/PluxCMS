package br.com.plux.cms.service;

import java.util.List;

import br.com.plux.cms.model.User;
import br.com.plux.cms.validation.EmailExistsException;

public interface UserService {
	
	User createUser(UserDto userDto) throws EmailExistsException;
	
	User findById(Long id);
	
	List<User> findAllUsers();
	
	void updateUser(User user);
	
	void deleteUser(Long id);

}
