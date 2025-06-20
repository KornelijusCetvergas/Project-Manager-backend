package lt.ca.javau12.todolist.dto;

import jakarta.validation.constraints.NotNull;

public record TaskDTO(
		@NotNull
		Long taskId, 
		String text,
		boolean isDone,
		Long projectId,
		Long userId
		) {}
