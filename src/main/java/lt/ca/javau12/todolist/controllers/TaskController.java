package lt.ca.javau12.todolist.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lt.ca.javau12.todolist.dto.TaskDTO;
import lt.ca.javau12.todolist.services.TaskService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/task")
public class TaskController {
	
	private final TaskService taskService;
	
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}
	
	@GetMapping("/{projectId}")
	public ResponseEntity<List<TaskDTO>> getByTodoListId(@PathVariable long projectId) {
		return ResponseEntity.ok( taskService.getTasksByProjectId(projectId) );
	}
	
	@PostMapping
	public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO dto) {
		return ResponseEntity.ok( taskService.createTask(dto) );
	}
	
	@PatchMapping("/toggle/{taskId}")
	public ResponseEntity<TaskDTO> toggleIsDone(@PathVariable long taskId) {
		return ResponseEntity.ok( taskService.toggleTask(taskId) );
	}
	
	@DeleteMapping("/{taskId}")
	public ResponseEntity<String> deleteTask(@PathVariable long taskId) {
		taskService.deleteTask(taskId);
		return ResponseEntity.ok( "Task deleted successfully!" );
	}
	
	@DeleteMapping("/{projectId}/alldone")
	public ResponseEntity<String> deleteCompletedTasks(@PathVariable long projectId) {
		taskService.deleteTaskByIsDone(projectId);
		return ResponseEntity.ok( "Tasks deleted successfully!" );
	}
	
}
