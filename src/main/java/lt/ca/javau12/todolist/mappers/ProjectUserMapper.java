package lt.ca.javau12.todolist.mappers;

import org.springframework.stereotype.Component;

import lt.ca.javau12.todolist.dto.ProjectUserDTO;
import lt.ca.javau12.todolist.entities.ProjectUser;

@Component
public class ProjectUserMapper {

    public ProjectUserDTO toDTO(ProjectUser projectUser) {
        return new ProjectUserDTO(
        		projectUser.getId(),
        		projectUser.getProject().getId(),
        		projectUser.getUser().getId(),
        		projectUser.getUser().getUsername(),
        		projectUser.getAccessLevel()
        		);
    }

}