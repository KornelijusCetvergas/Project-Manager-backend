package lt.ca.javau12.todolist.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lt.ca.javau12.todolist.enums.Role;

@Entity
@Table(name="users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String username;
	private String password;
	private boolean enabled;
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	@OneToMany( mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Project> projects = new ArrayList<>();
	
	@OneToMany(mappedBy = "user")
    private List<ProjectUser> sharedTodoLists = new ArrayList<>();
	
	public User() {}
	
	public User(Long id, String username, String password, boolean enabled, Role role) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<Project> getTodoLists() {
		return projects;
	}

	public void setTodoLists(List<Project> projects) {
		this.projects = projects;
	}

	public List<ProjectUser> getSharedTodoLists() {
		return sharedTodoLists;
	}

	public void setSharedTodoLists(List<ProjectUser> sharedTodoLists) {
		this.sharedTodoLists = sharedTodoLists;
	}

	
	
}
