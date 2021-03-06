package br.com.plux.cms.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.plux.cms.model.Privilege;
import br.com.plux.cms.model.Role;
import br.com.plux.cms.model.User;
import br.com.plux.cms.repository.UserRepository;

@Service("userDetailsService")
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private HttpServletRequest request;

	public CustomUserDetailsService() {
		super();
	}

	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		final String ip = getClientIP();
		if (loginAttemptService.isBlocked(ip)) {
			throw new RuntimeException("blocked");
		}
		System.out.println("IP: " + ip);
		try {
			final User user = userRepository.findByEmail(email);
			if (user == null) {
				throw new UsernameNotFoundException("No user found with username: " + email);
			}

			return new br.com.plux.cms.service.CustomUserDetails(user.getEmail(), user.getPassword(),
					user.isEnabled(), true, true, true, getAuthorities(user.getRoles()), user.getFirstName(), user.getLastName());
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// UTIL

    public final Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private final List<String> getPrivileges(final Collection<Role> roles) {
        final List<String> privileges = new ArrayList<String>();
        final List<Privilege> collection = new ArrayList<Privilege>();
        for (final Role role : roles) {
            collection.addAll(role.getPrivileges());
        }
        for (final Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }
	
	private final List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

}
