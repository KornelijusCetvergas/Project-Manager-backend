package lt.ca.javau12.todolist.dto;

import jakarta.validation.constraints.NotNull;
import lt.ca.javau12.todolist.enums.AccessLevel;

public record ProjectUserDTO(
		@NotNull
		Long projectUserId, 
		Long projectId,
		Long userId,
		String username,
		AccessLevel accessLevel
		) {

}
