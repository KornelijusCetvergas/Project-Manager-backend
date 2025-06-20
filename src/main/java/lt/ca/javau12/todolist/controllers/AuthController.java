package lt.ca.javau12.todolist.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lt.ca.javau12.todolist.dto.LoginRequest;
import lt.ca.javau12.todolist.dto.LoginResponse;
import lt.ca.javau12.todolist.dto.SignupRequest;
import lt.ca.javau12.todolist.services.AuthService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
		authService.createNewUser(request.username(), request.password());
		return ResponseEntity.ok("Registered succesfully");	
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.getToken(request.username(), request.password()));
	}
}



