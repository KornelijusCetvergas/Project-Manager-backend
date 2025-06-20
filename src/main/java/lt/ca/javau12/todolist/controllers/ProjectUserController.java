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

import lt.ca.javau12.todolist.dto.ProjectUserDTO;
import lt.ca.javau12.todolist.services.ProjectUserService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/projectuser")
public class ProjectUserController {
	
	private final ProjectUserService projectUserService;
	
	public ProjectUserController(ProjectUserService projectUserService) {
		this.projectUserService = projectUserService;
	}
	
	@PostMapping
	public ResponseEntity<ProjectUserDTO> shareProject(@RequestBody ProjectUserDTO dto) {
		return ResponseEntity.ok( projectUserService.shareProject(dto.projectId(), dto.userId()) );
	}
	
	@PatchMapping()
	public ResponseEntity<ProjectUserDTO> updateAccessLevel(@RequestBody ProjectUserDTO dto) {
		return ResponseEntity.ok( projectUserService.updateAccessLevel(dto.projectUserId(), dto.accessLevel()) );
	}
	
	@GetMapping("/{projectId}")
	public ResponseEntity<List<ProjectUserDTO>> getList(@PathVariable long projectId) {
		return ResponseEntity.ok( projectUserService.getList(projectId) );
		
	}
	
	@DeleteMapping("/{projectUserId}")
	public ResponseEntity<String> deleteProjectUser(@PathVariable long projectUserId) {
		projectUserService.deleteProjectUser(projectUserId);
		return ResponseEntity.ok( "ProjectUser deleted successfully!" );
	}
}
