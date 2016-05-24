package br.com.plux.cms.service;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.plux.cms.validation.PasswordMatches;
import br.com.plux.cms.validation.ValidEmail;

@PasswordMatches
public class UserDto {
	
	@NotNull
	@Size(min = 5)
	private String firstName;
	
	@NotNull
	@Size(min = 1)
	private String lastName;
	
	@NotNull
	@Size(min = 1)
	private String password;
	
	@NotNull
	@Size(min = 1)
	private String matchingPassword;
	
	@ValidEmail
	@NotNull
	@Size(min = 1)
	private String email;
	
	@NotNull
	private List<Long> roles;
	
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMatchingPassword() {
		return matchingPassword;
	}
	public void setMatchingPassword(String matchingPassword) {
		this.matchingPassword = matchingPassword;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<Long> getRoles() {
		return roles;
	}
	public void setRoles(List<Long> roles) {
		this.roles = roles;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [firstName=").append(firstName).append(", lastName=").append(lastName).append(", email=").append(email).append(", password=").append(password).append("]");
		return builder.toString();
	}
	
	
	
	

}
