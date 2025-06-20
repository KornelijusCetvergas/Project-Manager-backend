package lt.ca.javau12.todolist.services;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lt.ca.javau12.todolist.dto.ProjectDTO;
import lt.ca.javau12.todolist.entities.Project;
import lt.ca.javau12.todolist.entities.ProjectUser;
import lt.ca.javau12.todolist.entities.User;
import lt.ca.javau12.todolist.mappers.ProjectMapper;
import lt.ca.javau12.todolist.repositories.ProjectRepository;
import lt.ca.javau12.todolist.enums.AccessLevel;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MyUserDetailsService userDetailsService;
    private final ProjectMapper projectMapper;
    private final ProjectUserService projectUserService;

    public ProjectService(
    		ProjectRepository projectRepository, 
    		MyUserDetailsService userDetailsService,
    		ProjectMapper projectMapper,
    		ProjectUserService projectUserService
    		) {
        this.projectRepository = projectRepository;
        this.userDetailsService = userDetailsService;
        this.projectMapper = projectMapper;
        this.projectUserService = projectUserService;
    }
    
    public ProjectDTO createProject(String title) {
        User currentUser = userDetailsService.getCurrentUser();

        Project newProject = new Project(title, currentUser);
        
        projectRepository.save(newProject);
        
        ProjectUser projectUser = projectUserService.createProjectUser(newProject, currentUser, AccessLevel.OWNER);
        
        newProject.setProjectUserList( List.of(projectUser) ); 
        
        return projectMapper.toDTO( newProject );
    }

    public List<ProjectDTO> getCurrentUserProjects() {
    	Long userId = userDetailsService.getCurrentUser().getId();
        return projectRepository
        		.findByUserId(userId)
        		.stream()
        		.map(entity -> projectMapper.toDTO(entity))
        		.toList();
    }
    
    public List<ProjectDTO> getReadableProjects() {
    	Long userId = userDetailsService.getCurrentUser().getId();
        return projectRepository
        		.findAll()
        		.stream()
        		.filter(t -> projectUserService.canUserRead(t.getId(), userId))
        		.map(projectMapper::toDTO)
        		.toList();
    }

    public void deleteProject(Long id) {
    	checkIfUserIsOwner(id);
    	
        projectRepository.deleteById(id);
    }
    
    public ProjectDTO updateTitle(Long id, String title) {
    	Project existing = checkIfUserIsOwner(id);
    	
    	existing.setTitle(title);
    	
    	return projectMapper.toDTO(projectRepository.save(existing));
    }
    
    public Project checkIfUserIsOwner(Long id) {
        User currentUser = userDetailsService.getCurrentUser();

        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not the owner of this project.");
        }
        return project;
    }
}