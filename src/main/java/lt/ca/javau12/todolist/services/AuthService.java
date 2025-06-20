package lt.ca.javau12.todolist.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lt.ca.javau12.todolist.dto.LoginResponse;
import lt.ca.javau12.todolist.entities.User;
import lt.ca.javau12.todolist.enums.Role;
import lt.ca.javau12.todolist.repositories.UserRepository;
import lt.ca.javau12.todolist.security.JwtUtils;



@Service
public class AuthService {
	
	private final UserRepository userRepository;
	
	private final AuthenticationManager authenticationManager;
	
	private final JwtUtils jwtUtils;
	
	private final PasswordEncoder passwordEncoder;
	
	public AuthService(UserRepository userRepository, 
			AuthenticationManager authenticationManager, 
			JwtUtils jwtUtils,
			PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.passwordEncoder = passwordEncoder;
	}
	
	public void createNewUser(String username, String password) {
		if(!isNewUsername(username)) {
			throw new RuntimeException("Username already taken.");
		}
		User user = new User(null, username, passwordEncoder.encode(password), true, Role.USER);
		userRepository.save(user);
	}
	
	public boolean isNewUsername(String username) {
		return userRepository.findByUsername(username).isEmpty();
	}
	
	public LoginResponse getToken(String username, String password) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password)
			);
		return new LoginResponse( jwtUtils.generateToken(username) );
	}
}
