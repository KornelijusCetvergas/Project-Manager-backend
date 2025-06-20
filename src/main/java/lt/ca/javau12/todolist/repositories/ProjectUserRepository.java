package lt.ca.javau12.todolist.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.ca.javau12.todolist.entities.Project;
import lt.ca.javau12.todolist.entities.ProjectUser;
import lt.ca.javau12.todolist.entities.User;

@Repository
public interface ProjectUserRepository extends JpaRepository<ProjectUser,Long>{
	
	List<ProjectUser> findByUser(User user);
    List<ProjectUser> findByProject(Project project);
    Optional<ProjectUser> findByUserAndProject(User user, Project project);
    List<ProjectUser> findByProjectId(Long projectId);
}