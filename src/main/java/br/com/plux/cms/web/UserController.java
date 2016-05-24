package br.com.plux.cms.web;

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

import br.com.plux.cms.model.User;
import br.com.plux.cms.repository.RoleRepository;
import br.com.plux.cms.repository.UserRepository;
import br.com.plux.cms.service.UserDto;
import br.com.plux.cms.service.UserService;
import br.com.plux.cms.validation.EmailExistsException;

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
	
	@ModelAttribute
	public void addAttributes(Locale locale, Model model) {
		model.addAttribute("sessionName", messages.getMessage("users.session.name", null, locale));
		model.addAttribute("path", "users");
	}

	@RequestMapping(value = { "", "/list" }, method = RequestMethod.GET)
	public String list(Model model) {
		model.addAttribute("users", service.findAllUsers());
		return "/users/index";
	}

	// new User form
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newUser(WebRequest request, Model model) {
		model.addAttribute("userDto", new UserDto());
		model.addAttribute("allRoles", roleRepository.findAll());
		model.addAttribute("actionType", "NEW");
		return "/users/userForm";
	}

	// save new User
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String saveUser(@Valid @ModelAttribute("userDto") UserDto userDto, BindingResult result, WebRequest request, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("actionType", "NEW");
			model.addAttribute("allRoles", roleRepository.findAll());
			return "/users/userForm";
		}
		try {
			final User registered = service.createUser(userDto);
			//model.addAttribute("message", "Usuário: " + registered.getFirstName() + " adicionado com sucesso");
		} catch (EmailExistsException e){
			model.addAttribute("actionType", "NEW");
			model.addAttribute("allRoles", roleRepository.findAll());
			result.rejectValue("email", "validation.exists", "exists");
			return "/users/userForm";
		}
		
		return "redirect:/users";
	}

	// edit User form
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String showUser(@PathVariable("id") Long id, Model model) {
		model.addAttribute("user", service.findById(id));
		model.addAttribute("allRoles", roleRepository.findAll());
		model.addAttribute("actionType", "EDIT");
		return "/users/userForm";
	}

	// update User
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String updateUser(@Valid User user, Model model, Errors errors) {
		if (errors.hasErrors()) {
			return "/users/userForm";
		}
		service.updateUser(user);
		return "redirect:/users";
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String delteUser(@PathVariable("id") Long id, Model model) {
		service.deleteUser(id);
		return "redirect:/users";
	}

	// @RequestMapping(value = "/register", method = RequestMethod.GET)
	// public String showRegistrationForm(WebRequest request, Model model) {
	// User user = new User();
	// model.addAttribute("user", user);
	// return "registerForm";
	// }

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
