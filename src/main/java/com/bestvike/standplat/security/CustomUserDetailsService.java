
package com.bestvike.standplat.security;

import com.bestvike.standplat.dao.SysUserDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	protected Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private HttpSession httpSession;
	@Autowired
	private HttpServletRequest httpServletRequest;
	@Autowired
	private SysUserDao sysUserDao;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return new User(username, username, AuthorityUtils.createAuthorityList("ROLE_USER"));
	}

	public class UsernameEmptyException extends UsernameNotFoundException {
		private static final long serialVersionUID = 1L;

		public UsernameEmptyException(String msg) {
			super(msg);
		}
	}
	public class ValidateCodeInvalidException extends UsernameNotFoundException {
		private static final long serialVersionUID = 1L;

		public ValidateCodeInvalidException(String msg) {
			super(msg);
		}
	}
}
