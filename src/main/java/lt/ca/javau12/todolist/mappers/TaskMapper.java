package lt.ca.javau12.todolist.mappers;

import org.springframework.stereotype.Component;

import lt.ca.javau12.todolist.dto.TaskDTO;
import lt.ca.javau12.todolist.entities.Task;

@Component
public class TaskMapper {
	
	public TaskDTO toDTO(Task task) {
	    return new TaskDTO(
	    		task.getId(), 
	    		task.getText(), 
	    		task.isDone(), 
	    		task.getProject().getId(),
	    		task.getUser().getId()
	    		);
	}
	
	public Task toEntity(TaskDTO dto) {
		return new Task(
				dto.taskId(),
				dto.text(),
				false
				);
	}	
}
