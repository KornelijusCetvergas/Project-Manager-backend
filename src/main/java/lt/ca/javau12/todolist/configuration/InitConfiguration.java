package lt.ca.javau12.todolist.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lt.ca.javau12.todolist.entities.User;
import lt.ca.javau12.todolist.enums.Role;
import lt.ca.javau12.todolist.repositories.UserRepository;
import lt.ca.javau12.todolist.security.JwtUtils;


@Configuration
public class InitConfiguration {
	
	private final JwtUtils jwtUtils;
	private final UserRepository userRepository;
	
	private static final Logger log = LoggerFactory.getLogger(InitConfiguration.class);
	
	
	public InitConfiguration(UserRepository userRepository, JwtUtils jwtUtils) {
		this.userRepository = userRepository;
		this.jwtUtils = jwtUtils;
		
	}
	
	
	//TODO: REMOVE BEFORE PRODUCTION !!!!!!!!!
	@Bean
	CommandLineRunner init(PasswordEncoder encoder) {
		return args -> {
			
			Optional<User> userBox = userRepository.findByUsername("alice");
			//(Long id, String username, String password, Role role, boolean enabled)
			if(userBox.isEmpty()) {
				User user = new User(null, "alice", encoder.encode("pass1234"), true, Role.ADMIN);
				userRepository.save(user);
			} else {
				String username = userBox.get().getUsername();
				String token = jwtUtils.generateToken(username);
				log.info("alice token:  " + token);
			}
			
			Optional<User> newUserBox = userRepository.findByUsername("BOB");
			//(Long id, String username, String password, Role role, boolean enabled)
			if(newUserBox.isEmpty()) {
				User user = new User(null, "BOB", encoder.encode("pass12345"), true, Role.USER);
				userRepository.save(user);
			} else {
				String username = newUserBox.get().getUsername();
				String token = jwtUtils.generateToken(username);
				log.info("BOB token:  " + token);
			}
			
			Optional<User> DummyUserBox = userRepository.findByUsername("dummy");
			//(Long id, String username, String password, Role role, boolean enabled)
			if(DummyUserBox.isEmpty()) {
				User user = new User(null, "dummy", encoder.encode("pass123456"), true, Role.USER);
				userRepository.save(user);
			} else {
				String username = DummyUserBox.get().getUsername();
				String token = jwtUtils.generateToken(username);
				log.info("dummy token:  " + token);
			}
		};	
	}

	
}
