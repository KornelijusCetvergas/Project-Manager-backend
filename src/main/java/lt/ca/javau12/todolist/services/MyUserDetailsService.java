package lt.ca.javau12.todolist.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lt.ca.javau12.todolist.entities.User;
import lt.ca.javau12.todolist.repositories.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

	UserRepository userRepository;
	
	public MyUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException("UserNotFound") );
		
		return org.springframework.security.core.userdetails.User.builder()
				.username( user.getUsername())
				.password( user.getPassword())
				.authorities( "ROLE_" + user.getRole().name()) 
				.disabled( !user.isEnabled())
				.build();
	}
	
	public User getCurrentUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
	}
	
	public void delteUser(Long id) {
		userRepository.deleteById(id);
	}

}
