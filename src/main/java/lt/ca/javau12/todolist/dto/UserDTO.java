package lt.ca.javau12.todolist.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDTO {
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	
	@NotEmpty
	@Size(min=2, message="The username is too short")
	private String username;
	
	@NotEmpty
	@Size(min=2, message="The password is too short")
	private String password;
	
}
