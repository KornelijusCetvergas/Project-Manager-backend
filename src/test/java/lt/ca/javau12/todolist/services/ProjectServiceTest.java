package lt.ca.javau12.todolist.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lt.ca.javau12.todolist.dto.ProjectDTO;
import lt.ca.javau12.todolist.entities.Project;
import lt.ca.javau12.todolist.entities.User;
import lt.ca.javau12.todolist.enums.AccessLevel;
import lt.ca.javau12.todolist.mappers.ProjectMapper;
import lt.ca.javau12.todolist.mappers.ProjectUserMapper;
import lt.ca.javau12.todolist.mappers.TaskMapper;
import lt.ca.javau12.todolist.repositories.ProjectRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private MyUserDetailsService userDetailsService;
    private ProjectMapper projectMapper;
    private ProjectUserService projectUserService;
    private ProjectService projectService;
    private TaskMapper taskMapper;
    private ProjectUserMapper projectUserMapper;

    private User mockUser;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        userDetailsService = mock(MyUserDetailsService.class);
        projectMapper = mock(ProjectMapper.class);
        projectUserService = mock(ProjectUserService.class);
        taskMapper = mock(TaskMapper.class);
        projectUserMapper = mock(ProjectUserMapper.class);

        projectService = new ProjectService(
            projectRepository, userDetailsService, projectMapper, projectUserService
        );

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");

        when(userDetailsService.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    void createProject_shouldSaveProjectAndReturnDTO() {
        String title = "New Project";
        Project savedProject = new Project(title, mockUser);
        ProjectDTO mockDTO = new ProjectDTO(
        		1L, 
        		mockUser.getUsername(), 
        		title, 
        		savedProject.getTasks().stream().map(taskMapper::toDTO).toList(), 
        		savedProject.getProjectUserList().stream().map(projectUserMapper::toDTO).toList()
        		);

        when(projectMapper.toDTO(any(Project.class))).thenReturn(mockDTO);

        ProjectDTO result = projectService.createProject(title);

        verify(projectRepository).save(any(Project.class));
        verify(projectUserService).createProjectUser(any(Project.class), eq(mockUser), eq(AccessLevel.OWNER));
        assertEquals(title, result.title());
    }

    @Test
    void getCurrentUserProjects_shouldReturnMappedList() {
        Project project = new Project("My Project", mockUser);
        project.setId(1L);
        ProjectDTO dto = new ProjectDTO(
        		1L, 
        		mockUser.getUsername(), 
        		"My Project", 
        		project.getTasks().stream().map(taskMapper::toDTO).toList(), 
        		project.getProjectUserList().stream().map(projectUserMapper::toDTO).toList()
        		);

        when(projectRepository.findByUserId(mockUser.getId())).thenReturn(List.of(project));
        when(projectMapper.toDTO(project)).thenReturn(dto);

        List<ProjectDTO> results = projectService.getCurrentUserProjects();

        assertEquals(1, results.size());
        assertEquals("My Project", results.get(0).title());
    }

    @Test
    void getReadableProjects_shouldReturnProjectsWhereUserCanRead() {
        Project project1 = new Project("Project 1", mockUser);
        project1.setId(1L);
        Project project2 = new Project("Project 2", mockUser);
        project2.setId(2L);

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(projectUserService.canUserRead(eq(1L), eq(mockUser.getId()))).thenReturn(true);
        when(projectUserService.canUserRead(eq(2L), eq(mockUser.getId()))).thenReturn(false);
        when(projectMapper.toDTO(project1))
        	.thenReturn(
        			new ProjectDTO(
        	        		1L, 
        	        		mockUser.getUsername(), 
        	        		"Project 1", 
        	        		project1.getTasks().stream().map(taskMapper::toDTO).toList(), 
        	        		project1.getProjectUserList().stream().map(projectUserMapper::toDTO).toList()
        	        		)
        			);

        List<ProjectDTO> results = projectService.getReadableProjects();

        assertEquals(1, results.size());
        assertEquals("Project 1", results.get(0).title());
    }

    @Test
    void updateTitle_shouldUpdateAndReturnDTO() {
        Project project = new Project("Old Title", mockUser);
        project.setId(1L);
        ProjectDTO updatedDto = new ProjectDTO(
        		1L, 
        		mockUser.getUsername(), 
        		"New Title", 
        		project.getTasks().stream().map(taskMapper::toDTO).toList(), 
        		project.getProjectUserList().stream().map(projectUserMapper::toDTO).toList()
        		);


        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toDTO(project)).thenReturn(updatedDto);

        ProjectDTO result = projectService.updateTitle(1L, "New Title");

        assertEquals("New Title", result.title());
    }

    @Test
    void deleteProject_shouldCallRepositoryDeleteIfOwner() {
        Project project = new Project("Test Project", mockUser);
        project.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProject(1L);

        verify(projectRepository).deleteById(1L);
    }

}
