package co.prior.oauth.demo.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.prior.oauth.demo.model.User;
import co.prior.oauth.demo.model.UserAuthority;
import co.prior.oauth.demo.model.dto.CustomGrantedAuthority;
import co.prior.oauth.demo.model.dto.CustomUserDetails;
import co.prior.oauth.demo.persistence.UserDao;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userDao.findByUsername(username);
		if (user != null) {
			CustomUserDetails customUserDetails = new CustomUserDetails();
			customUserDetails.setUserName(user.getUserName());
			customUserDetails.setPassword(user.getPassword());
			Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
			for (UserAuthority authority : user.getUserAuthorities()) {
				authorities.add(new CustomGrantedAuthority(authority.getAuthority().getName()));
			}
			customUserDetails.setGrantedAuthorities(authorities);
			return customUserDetails;
		}
		throw new UsernameNotFoundException(username);
	}

}
