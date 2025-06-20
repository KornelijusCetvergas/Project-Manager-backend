package lt.ca.javau12.todolist.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import lt.ca.javau12.todolist.dto.ProjectUserDTO;
import lt.ca.javau12.todolist.entities.Project;
import lt.ca.javau12.todolist.entities.ProjectUser;
import lt.ca.javau12.todolist.entities.User;
import lt.ca.javau12.todolist.enums.AccessLevel;
import lt.ca.javau12.todolist.mappers.ProjectUserMapper;
import lt.ca.javau12.todolist.repositories.ProjectRepository;
import lt.ca.javau12.todolist.repositories.ProjectUserRepository;
import lt.ca.javau12.todolist.repositories.UserRepository;

@Service
public class ProjectUserService {

    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserMapper projectUserMapper;
    private final MyUserDetailsService userDetailsService;

    public ProjectUserService(
        ProjectUserRepository projectUserRepository,
        UserRepository userRepository,
        ProjectRepository projectRepository,
        ProjectUserMapper projectUserMapper,
        MyUserDetailsService userDetailsService
    ) {
        this.projectUserRepository = projectUserRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectUserMapper = projectUserMapper;
        this.userDetailsService = userDetailsService;
    }

    public ProjectUserDTO shareProject(Long projectId, Long userId) {
    	
    	User currentUser = userDetailsService.getCurrentUser();
    	
    	if( !isOwner(projectId, currentUser.getId()) ) {
    		throw new AccessDeniedException("You are not the owner of this project.");
	    }
    	
		Project project = projectRepository.findById(projectId)
				.orElseThrow( () -> new RuntimeException("Project with id: " + projectId + " not found.") );
    	
        User user = userRepository.findById(userId)
            .orElseThrow( () -> new RuntimeException("User with id: " + userId + " not found.") );

        return projectUserMapper.toDTO( createProjectUser(project, user, AccessLevel.READ) );
    }
    
    public ProjectUser createProjectUser(Project project, User user, AccessLevel accessLevel) {
    	
    	if( isDuplicate(project.getId(), user.getId()) ) {
    		throw new RuntimeException("ProjectUser already exists.");
	    }
    	
    	ProjectUser newVisibility = new ProjectUser(
		        project,
		        user,
		        accessLevel
        		);
    	
    	return projectUserRepository.save(newVisibility);
    }
    
    public ProjectUserDTO updateAccessLevel(Long projectUserId, AccessLevel accessLevel) {
    	
    	if( accessLevel == AccessLevel.OWNER ) {
    		throw new RuntimeException("There can only be one project owner.");
	    }
    	
    	User currentUser = userDetailsService.getCurrentUser();
    	
    	ProjectUser existing = projectUserRepository
    			.findById(projectUserId)
    			.orElseThrow( () -> new RuntimeException("ProjectUser not found.") );
    	
    	if( !isOwner(existing.getProject().getId(), currentUser.getId()) ) {
    		throw new AccessDeniedException("You are not the owner of this project.");
	    }
    	
    	if( existing.getAccessLevel() == AccessLevel.OWNER ) {
    		throw new RuntimeException("Cannot change owners AccessLevel.");
	    }
    	
    	existing.setAccessLevel(accessLevel);
    	
    	projectUserRepository.save(existing);
    	
    	return projectUserMapper.toDTO( existing );
    	
    }
    
    public List<ProjectUserDTO> getList(Long projectId) {
    	
    	User currentUser = userDetailsService.getCurrentUser();
    	
    	if( !isOwner(projectId, currentUser.getId()) ) {
    		throw new AccessDeniedException("You are not the owner of this project.");
	    }
    	
        return projectUserRepository
        		.findByProjectId(projectId)
        		.stream()
        		.map(projectUserMapper::toDTO)
        		.toList();
    }
    
    public void deleteProjectUser(Long projectUserId) {
    	
    	ProjectUser existing = projectUserRepository
    			.findById(projectUserId)
    			.orElseThrow( () -> new RuntimeException("ProjectUser not found.") );
    	
    	User currentUser = userDetailsService.getCurrentUser();
    	
    	if( !isOwner(existing.getProject().getId(), currentUser.getId()) ) {
    		throw new AccessDeniedException("You are not the owner of this project.");
	    }
    	
    	projectUserRepository.deleteById(projectUserId);
    }
    
    public AccessLevel getUserAccessLevel(Long projectId, Long userId) {
    	
    	Optional<ProjectUser> projectUser = projectUserRepository
        		.findByProjectId(projectId)
        		.stream()
        		.filter( v -> v.getUser().getId().equals(userId) )
        		.findFirst();
        
    	if(projectUser.isEmpty()) {
    		return AccessLevel.NOACCESS;
    	}
    	
        return projectUser.get().getAccessLevel();
    }
    
    public boolean canUserRead(Long projectId, Long userId) {
    	AccessLevel level = getUserAccessLevel(projectId, userId);
    	switch (level) {
    		case AccessLevel.OWNER: return true;
    		case AccessLevel.EDIT: 	return true;
    		case AccessLevel.DO: 	return true;
	    	case AccessLevel.READ: 	return true;
	    	default: 				return false;
    	}
    }
    
    public boolean canUserDo(Long projectId, Long userId) {
    	AccessLevel level = getUserAccessLevel(projectId, userId);
    	switch (level) {
	    	case AccessLevel.OWNER: return true;
			case AccessLevel.EDIT: 	return true;
			case AccessLevel.DO: 	return true;
	    	case AccessLevel.READ: 	return false;
	    	default: 				return false;
    	}
    }
    
    public boolean canUserEdit(Long projectId, Long userId) {
    	AccessLevel level = getUserAccessLevel(projectId, userId);
    	switch (level) {
	    	case AccessLevel.OWNER: return true;
			case AccessLevel.EDIT: 	return true;
			case AccessLevel.DO: 	return false;
	    	case AccessLevel.READ: 	return false;
	    	default: 				return false;
    	}
    }
    
    public boolean isOwner(Long projectId, Long userId) {
    	AccessLevel level = getUserAccessLevel(projectId, userId);
    	switch (level) {
	    	case AccessLevel.OWNER: return true;
			case AccessLevel.EDIT: 	return false;
			case AccessLevel.DO: 	return false;
	    	case AccessLevel.READ: 	return false;
	    	default: 				return false;
    	}
    }
    
    public boolean isDuplicate(Long projectId, Long userId) {
    	return projectUserRepository
        	.findByProjectId(projectId)
        	.stream()
        	.filter( v -> v.getUser().getId().equals(userId) )
        	.findFirst()
        	.isPresent();
    }
}
