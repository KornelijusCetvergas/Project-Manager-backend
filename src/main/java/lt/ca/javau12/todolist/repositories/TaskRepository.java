package lt.ca.javau12.todolist.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import lt.ca.javau12.todolist.entities.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long>{
	List<Task> findByProjectId(Long projectId);
	void deleteByProject_IdAndIsDone(Long projectId, boolean isDone);
}
