package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class SubtaskRepositoryTest implements SubtaskTestHelper, TaskTestHelper, TeamTestHelper{

    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;

    public SubtaskRepositoryTest(SubtaskRepository subtaskRepository, TaskRepository taskRepository, TeamRepository teamRepository) {
        this.subtaskRepository = subtaskRepository;
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
    }

    @AfterEach
    void cleanUp(){
        subtaskRepository.deleteAll();
        subtaskRepository.flush();
        taskRepository.deleteAll();
        taskRepository.flush();
        teamRepository.deleteAll();
        teamRepository.flush();
    }

    @Test
    void shouldSaveAndRetrieveSubtaskCorrectly(){
        Team team = createTeam(teamRepository);
        Task task = createTask(team,taskRepository);
        long id = createSubtask(task,subtaskRepository).getID();

        Subtask found = subtaskRepository.findSubtaskEntityByIDAndTaskID(id, task.getID()).orElse(null);

        assertNotNull(found);
        assertEquals(id,found.getID());
        assertEquals("TESTSub",found.getName());
        assertEquals("Test subtask", found.getDescription());
        assertEquals(task,found.getTask());
        assertEquals(TaskStatus.NEW, found.getTaskStatus());
    }
}
