package lt.ca.javau12.todolist.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lt.ca.javau12.todolist.dto.TaskDTO;
import lt.ca.javau12.todolist.entities.Task;
import lt.ca.javau12.todolist.entities.Project;
import lt.ca.javau12.todolist.entities.User;
import lt.ca.javau12.todolist.mappers.TaskMapper;
import lt.ca.javau12.todolist.repositories.ProjectRepository;
import lt.ca.javau12.todolist.repositories.TaskRepository;

@Service
public class TaskService {
	
	private final TaskRepository taskRepository;
	private final TaskMapper taskMapper;
	private final ProjectUserService projectUserService;
	private final MyUserDetailsService userDetailsService;
	private final ProjectRepository projectRepository;
	
	public TaskService(
			TaskRepository taskRepository, 
			TaskMapper taskMapper, 
			ProjectUserService projectUserService,
			MyUserDetailsService userDetailsService,
			ProjectRepository projectRepository
			) {
		this.taskRepository = taskRepository;
		this.taskMapper = taskMapper;
		this.projectUserService = projectUserService;
		this.userDetailsService = userDetailsService;
		this.projectRepository = projectRepository;
	}
	
	public List<TaskDTO> getAll() {
		return taskRepository
				.findAll()
				.stream()
				.map(taskMapper::toDTO)
				.toList();
	}
	
	public Optional<TaskDTO> getById(Long id) {
		return taskRepository
				.findById(id)
				.map(taskMapper::toDTO);
	}

	public List<TaskDTO> getTasksByProjectId(Long projectId) {
		
		User currentUser = userDetailsService.getCurrentUser();
		
		if( !projectUserService.canUserEdit(projectId, currentUser.getId()) ) {
    		throw new AccessDeniedException("You are not allowed to add tasks to this project.");
	    }
		
		return taskRepository
				.findByProjectId(projectId)
				.stream()
				.map(taskMapper::toDTO)
				.toList();
	}
	
	public Optional<Project> getProjectByTaskId(Long id) {
		return Optional.of(taskRepository
							.findById(id)
							.get()
							.getProject()
						);
	}
	
    public TaskDTO createTask(TaskDTO dto) {
        
        User currentUser = userDetailsService.getCurrentUser();
        
        if( !projectUserService.canUserEdit(dto.projectId(), currentUser.getId()) ) {
    		throw new AccessDeniedException("You are not allowed to add tasks to this project.");
	    }
        
        Project project = projectRepository
        		.findById(dto.projectId())
        		.orElseThrow(() -> new RuntimeException("Project with id: " + dto.projectId() + " not found"));
        
        Task newTask = new Task(
        		dto.text(), 
        		project, 
        		currentUser
        		);
        
        return taskMapper.toDTO(taskRepository.save(newTask));
    }
    
    public TaskDTO toggleTask(Long taskId) {
    	User currentUser = userDetailsService.getCurrentUser();
        
    	Project project = getProjectByTaskId(taskId)
    				.orElseThrow(() -> new RuntimeException("Project with task id: " + taskId + " not found"));
    	
        if( !projectUserService.canUserDo(project.getId(), currentUser.getId()) ) {
    		throw new AccessDeniedException("You are not allowed to toggle tasks in this project.");
	    }
    	
        Task existing = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        existing.setDone(!existing.isDone());
        
        return taskMapper.toDTO(taskRepository.save(existing));
    }
    
    public void deleteTask(Long taskId) {
    	User currentUser = userDetailsService.getCurrentUser();
        
    	Project project = getProjectByTaskId(taskId)
    				.orElseThrow(() -> new RuntimeException("Project with task id: " + taskId + " not found"));
    	
        if( !projectUserService.canUserEdit(project.getId(), currentUser.getId()) ) {
    		throw new AccessDeniedException("You are not allowed to delete tasks in this project.");
	    }
        
        taskRepository.deleteById(taskId);
    }
    
    @Transactional
	public void deleteTaskByIsDone(Long projectId) {
		User currentUser = userDetailsService.getCurrentUser();
		
		if( !projectUserService.canUserEdit(projectId, currentUser.getId()) ) {
    		throw new AccessDeniedException("You are not allowed to add tasks to this project.");
	    }
		
		taskRepository.deleteByProject_IdAndIsDone(projectId, true);
		
	}
    
}

