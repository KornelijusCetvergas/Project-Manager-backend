package lt.ca.javau12.todolist.dto;

import java.util.List;

public record ProjectDTO(
	    Long id,
	    String username,
	    String title,
	    List<TaskDTO> tasks,
	    List<ProjectUserDTO> projectUserList
	) {}
