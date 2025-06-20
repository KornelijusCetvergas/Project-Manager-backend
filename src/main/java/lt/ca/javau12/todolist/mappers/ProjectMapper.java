package lt.ca.javau12.todolist.mappers;

import org.springframework.stereotype.Component;

import lt.ca.javau12.todolist.dto.ProjectDTO;
import lt.ca.javau12.todolist.entities.Project;

@Component
public class ProjectMapper {
	
	private final TaskMapper taskMapper;
	private final ProjectUserMapper projectUserMapper;
	
	public ProjectMapper(TaskMapper taskMapper, ProjectUserMapper projectUserMapper) {
		this.taskMapper = taskMapper;
		this.projectUserMapper = projectUserMapper;
	}
	
    public ProjectDTO toDTO(Project project) {
    	return new ProjectDTO(
    			project.getId(),
    			project.getUser().getUsername(),
    			project.getTitle(),
    			project.getTasks().stream().map(taskMapper::toDTO).toList(),
    			project.getProjectUserList().stream().map(projectUserMapper::toDTO).toList()
    			);

    }

    public static Project toEntity(ProjectDTO dto) {
        Project project = new Project();
        project.setId(dto.id());
        project.setTitle(dto.title());
        // User and relationships should be fetched/set in service
        return project;
    }
}
