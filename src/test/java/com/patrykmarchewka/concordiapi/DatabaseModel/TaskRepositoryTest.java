package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TaskRepositoryTest implements TaskTestHelper, TeamTestHelper{

    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;

    public TaskRepositoryTest(TaskRepository taskRepository, TeamRepository teamRepository) {
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
    }

    @AfterEach
    void cleanUp(){
        taskRepository.deleteAll();
        taskRepository.flush();
        teamRepository.deleteAll();
        teamRepository.flush();
    }
    @Test
    void shouldSaveAndRetrieveTaskCorrectly(){
        Team team = createTeam(teamRepository);
        Task task = createTask(team,taskRepository);

        Task found = taskRepository.findByIdAndAssignedTeam(task.getID(),team).orElse(null);

        assertNotNull(found);
        assertEquals(task.getID(), found.getID());
        assertEquals("TEST TASK", found.getName());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, found.getTaskStatus());
        assertEquals(team, task.getAssignedTeam());
        assertTrue(task.getUserTasks().isEmpty());
        assertTrue(task.getUsers().isEmpty());
        assertTrue(task.getSubtasks().isEmpty());
        assertTrue(OffsetDateTime.now().isAfter(found.getCreationDate()));
        assertTrue(OffsetDateTime.now().isAfter(found.getUpdateDate()));
    }

    @Test
    void shouldFindByTeam(){
        Team team = createTeam(teamRepository);
        createTask(team,taskRepository);
        createTask(team,taskRepository);

        Set<Task> found = taskRepository.getByAssignedTeam(team);

        assertNotNull(found);
        assertEquals(2,found.size());
    }
}
