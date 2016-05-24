package br.com.plux.cms.web;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

import br.com.plux.cms.model.Role;

public class RoleFormatter implements Formatter<Role> {

	@Override
	public String print(Role role, Locale locale) {
		System.out.println("role -> string");
		return role.getId().toString();
	}

	@Override
	public Role parse(String id, Locale locale) throws ParseException {
		Role role = new Role();
		System.out.println("string -> role");
		role.setId(Long.parseLong(id));
		
		return role;
	}

}
