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

import lt.ca.javau12.todolist.dto.ProjectDTO;
import lt.ca.javau12.todolist.services.ProjectService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/project")
public class ProjectController {
	
	private final ProjectService projectService;
	
	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<ProjectDTO>> getReadableProjects() {
		return ResponseEntity.ok( projectService.getReadableProjects() );
	}
	
	@PostMapping
	public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO dto) {
		return ResponseEntity.ok( projectService.createProject(dto.title()) );
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProject(@PathVariable Long id) {
		projectService.deleteProject(id);
		return ResponseEntity.ok( "Project deleted successfully!" );
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<ProjectDTO> updateTitle(@PathVariable Long id, @RequestBody ProjectDTO dto) {
		return ResponseEntity.ok( projectService.updateTitle(id, dto.title()) );
	}
}
