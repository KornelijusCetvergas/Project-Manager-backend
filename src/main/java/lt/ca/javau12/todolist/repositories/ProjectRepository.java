package lt.ca.javau12.todolist.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.ca.javau12.todolist.entities.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long>{

	public List<Project> findByUserId(Long userId);
}
