package br.com.plux.cms.web;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import br.com.plux.cms.model.User;
import br.com.plux.cms.repository.UserRepository;

@Controller
@RequestMapping(value = "/user")
public class UserController {
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String showRegistrationForm(WebRequest request, Model model) {
	    User user = new User();
	    model.addAttribute("user", user);
	    return "registerForm";   
	}
	
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
