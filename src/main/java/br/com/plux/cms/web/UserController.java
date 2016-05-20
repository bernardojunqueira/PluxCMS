package br.com.plux.cms.web;


import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.plux.cms.model.Role;
import br.com.plux.cms.model.User;
import br.com.plux.cms.repository.RoleRepository;
import br.com.plux.cms.repository.UserRepository;
import br.com.plux.cms.service.UserDto;
import br.com.plux.cms.service.UserService;

@Controller
@RequestMapping(value = "/users")
public class UserController {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserService service;
	
	@Autowired
    private MessageSource messages;

	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@RequestMapping(value = {"", "/list"}, method = RequestMethod.GET)
	public String list(Model model) {
		model.addAttribute("users", userRepository.findAllWithRoles());
		return "/users/index";
	}
	
	// new User form
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newUser(Model model) {
		model.addAttribute("userDto", new UserDto());
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("actionType", "NEW");
		return "/users/userForm";
	}
	
	// save new User
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String saveUser(@Valid UserDto userDto, Model model, Errors errors) {
		if (errors.hasErrors()) {
			return "/users/userForm";
	    }
		
		final User registered = service.newUser(userDto);

		model.addAttribute("message", "Usuário: " + registered.getFirstName() + " adicionado com sucesso");
		return "redirect:/users";
	}
	
	// edit User form
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String showUser (@PathVariable("id") Long id, Model model) {
		User user = userRepository.findByIdWithRoles(id);
		List<Long> userRoles = new ArrayList<Long>();
		
		for (Role role : user.getRoles()) {
			userRoles.add(role.getId());
		}
		System.out.println(userRoles);
		model.addAttribute("user", user);
		model.addAttribute("currentRoles", userRoles);
		model.addAttribute("roles", roleRepository.findAll());
		model.addAttribute("actionType", "EDIT");
		return "/users/userForm";
	}

	// update User
		@RequestMapping(value = "/edit", method = RequestMethod.POST)
		@Transactional
		public String updateUser(@Valid User user, Model model, Errors errors) {
			if (errors.hasErrors()) {
				return "/users/userForm";
		    }
			
			user.setRoles(roleRepository.findAll());
			
			userRepository.saveAndFlush(user);
			return "redirect:/users";
		}
		
//	@RequestMapping(value = "/register", method = RequestMethod.GET)
//	public String showRegistrationForm(WebRequest request, Model model) {
//	    User user = new User();
//	    model.addAttribute("user", user);
//	    return "registerForm";   
//	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerUserAccount(@Valid User user, Errors errors) {
	    if (errors.hasErrors()) {
	    	return "registerForm";
	    }
	    
	    if (emailExist(user.getEmail())) {
	    	errors.rejectValue("email", "validation.exists", "exists");
	    	return "registerForm";
	    }
	    
	    user.setPassword(passwordEncoder.encode(user.getPassword()));
	    
		userRepository.save(user);
	    return "redirect:/minor";
	}
	
	private boolean emailExist(final String email) {
		final User user = userRepository.findByEmail(email);
		if (user != null) {
			return true;
		}
		return false;
	}
}
