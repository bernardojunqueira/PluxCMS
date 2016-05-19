package br.com.plux.cms.web;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.plux.cms.model.User;
import br.com.plux.cms.repository.RoleRepository;
import br.com.plux.cms.repository.UserRepository;

@Controller
@RequestMapping(value = "/users")
public class UserController {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;

	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@RequestMapping(value = {"", "/list"}, method = RequestMethod.GET)
	public String list(Model model) {
		model.addAttribute("users", userRepository.findAllWithRoles());
		return "/users/index";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newUser(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("roles", roleRepository.findAll());
		return "/users/userForm";
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public String saveUser(@Valid User user, Model model, Errors errors) {
		if (errors.hasErrors()) {
			return "/users/userForm";
	    }
		System.out.println(user.getRoles());
		//userRepository.save(user);
		model.addAttribute("message", "Usuário adicionado com sucesso");
		return "redirect:/users";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String showUser (@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", userRepository.findOne(id));
		return "/users/show";
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
