package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskWithSubtasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskWithUserTasks;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskRepositoryTest {

    private final TaskRepository taskRepository;
    private final TestDataLoader testDataLoader;

    @Autowired
    public TaskRepositoryTest(TaskRepository taskRepository, TestDataLoader testDataLoader) {
        this.taskRepository = taskRepository;
        this.testDataLoader = testDataLoader;
    }

    @BeforeAll
    void initialize(){
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }


    /// findTaskByIDAndAssignedTeamID

    @Test
    void shouldFindTaskByIDAndAssignedTeamID(){
        Optional<TaskIdentity> task = taskRepository.findTaskByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), testDataLoader.teamRead.getID());

        assertTrue(task.isPresent());
        assertEquals(testDataLoader.taskMultiUserRead.getID(), task.get().getID());
        assertEquals(testDataLoader.taskMultiUserRead.getName(), task.get().getName());
        assertEquals(testDataLoader.taskMultiUserRead.getDescription(), task.get().getDescription());
        assertEquals(testDataLoader.taskMultiUserRead.getTaskStatus(), task.get().getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserRead.getCreationDate(), task.get().getCreationDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUpdateDate(), task.get().getUpdateDate());
        assertEquals(testDataLoader.taskMultiUserRead.getAssignedTeam(), task.get().getAssignedTeam());
    }

    @Test
    void shouldReturnEmptyTaskForNonExistentIDAndAssignedTeamID(){
        Optional<TaskIdentity> task = taskRepository.findTaskByIDAndAssignedTeamID(999L, testDataLoader.teamRead.getID());

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskForIDAndNonExistentTeamID(){
        Optional<TaskIdentity> task = taskRepository.findTaskByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), 999L);

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskForInvalidIDAndAssignedTeamID(){
        Optional<TaskIdentity> task = taskRepository.findTaskByIDAndAssignedTeamID(-1, testDataLoader.teamRead.getID());

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskForIDAndInvalidTeamID(){
        Optional<TaskIdentity> task = taskRepository.findTaskByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), -1);

        assertFalse(task.isPresent());
    }

    /// findTaskWithUserTasksByIDAndAssignedTeamID

    @Test
    void shouldFindTaskWithUserTasksByIDAndAssignedTeamID(){
        Optional<TaskWithUserTasks> task = taskRepository.findTaskWithUserTasksByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), testDataLoader.teamRead.getID());

        assertTrue(task.isPresent());
        assertEquals(testDataLoader.taskMultiUserRead.getID(), task.get().getID());
        assertEquals(testDataLoader.taskMultiUserRead.getName(), task.get().getName());
        assertEquals(testDataLoader.taskMultiUserRead.getDescription(), task.get().getDescription());
        assertEquals(testDataLoader.taskMultiUserRead.getTaskStatus(), task.get().getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserRead.getCreationDate(), task.get().getCreationDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUpdateDate(), task.get().getUpdateDate());
        assertEquals(testDataLoader.taskMultiUserRead.getAssignedTeam(), task.get().getAssignedTeam());
        assertEquals(testDataLoader.taskMultiUserRead.getUserTasks(), task.get().getUserTasks());
    }

    @Test
    void shouldReturnEmptyTaskWithUserTasksForNonExistentIDAndAssignedTeamID(){
        Optional<TaskWithUserTasks> task = taskRepository.findTaskWithUserTasksByIDAndAssignedTeamID(999L, testDataLoader.teamRead.getID());

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskWithUserTasksForIDAndNonExistentTeamID(){
        Optional<TaskWithUserTasks> task = taskRepository.findTaskWithUserTasksByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), 999L);

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskWithUserTasksForInvalidIDAndAssignedTeamID(){
        Optional<TaskWithUserTasks> task = taskRepository.findTaskWithUserTasksByIDAndAssignedTeamID(-1, testDataLoader.teamRead.getID());

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskWithUserTasksForIDAndInvalidTeamID(){
        Optional<TaskWithUserTasks> task = taskRepository.findTaskWithUserTasksByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), -1);

        assertFalse(task.isPresent());
    }

    /// findTaskWithSubtasksByIDAndAssignedTeamID

    @Test
    void shouldFindTaskWithSubtasksByIDAndAssignedTeamID(){
        Optional<TaskWithSubtasks> task = taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), testDataLoader.teamRead.getID());

        assertTrue(task.isPresent());
        assertEquals(testDataLoader.taskMultiUserRead.getID(), task.get().getID());
        assertEquals(testDataLoader.taskMultiUserRead.getName(), task.get().getName());
        assertEquals(testDataLoader.taskMultiUserRead.getDescription(), task.get().getDescription());
        assertEquals(testDataLoader.taskMultiUserRead.getTaskStatus(), task.get().getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserRead.getCreationDate(), task.get().getCreationDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUpdateDate(), task.get().getUpdateDate());
        assertEquals(testDataLoader.taskMultiUserRead.getAssignedTeam(), task.get().getAssignedTeam());
        assertEquals(testDataLoader.taskMultiUserRead.getSubtasks(), task.get().getSubtasks());
    }

    @Test
    void shouldReturnEmptyTaskWithSubtasksForNonExistentIDAndAssignedTeamID(){
        Optional<TaskWithSubtasks> task = taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(999L, testDataLoader.teamRead.getID());

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskWithSubtasksForIDAndNonExistentTeamID(){
        Optional<TaskWithSubtasks> task = taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), 999L);

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskWithSubtasksForInvalidIDAndAssignedTeamID(){
        Optional<TaskWithSubtasks> task = taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(-1, testDataLoader.teamRead.getID());

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskWithSubtasksForIDAndInvalidTeamID(){
        Optional<TaskWithSubtasks> task = taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), -1);

        assertFalse(task.isPresent());
    }

    /// findTaskFullByIDAndAssignedTeamID

    @Test
    void shouldFindTaskFullByIDAndAssignedTeamID(){
        Optional<TaskFull> task = taskRepository.findTaskFullByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), testDataLoader.teamRead.getID());

        assertTrue(task.isPresent());
        assertEquals(testDataLoader.taskMultiUserRead.getID(), task.get().getID());
        assertEquals(testDataLoader.taskMultiUserRead.getName(), task.get().getName());
        assertEquals(testDataLoader.taskMultiUserRead.getDescription(), task.get().getDescription());
        assertEquals(testDataLoader.taskMultiUserRead.getTaskStatus(), task.get().getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserRead.getCreationDate(), task.get().getCreationDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUpdateDate(), task.get().getUpdateDate());
        assertEquals(testDataLoader.taskMultiUserRead.getAssignedTeam(), task.get().getAssignedTeam());
        assertEquals(testDataLoader.taskMultiUserRead.getUserTasks(), task.get().getUserTasks());
        assertEquals(testDataLoader.taskMultiUserRead.getSubtasks(), task.get().getSubtasks());
    }

    @Test
    void shouldReturnEmptyTaskFullForNonExistentIDAndAssignedTeamID(){
        Optional<TaskWithSubtasks> task = taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(999L, testDataLoader.teamRead.getID());

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskFullForIDAndNonExistentTeamID(){
        Optional<TaskWithSubtasks> task = taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), 999L);

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskFullForInvalidIDAndAssignedTeamID(){
        Optional<TaskWithSubtasks> task = taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(-1, testDataLoader.teamRead.getID());

        assertFalse(task.isPresent());
    }

    @Test
    void shouldReturnEmptyTaskFullForIDAndInvalidTeamID(){
        Optional<TaskWithSubtasks> task = taskRepository.findTaskWithSubtasksByIDAndAssignedTeamID(testDataLoader.taskMultiUserRead.getID(), -1);

        assertFalse(task.isPresent());
    }

    /// findAllTaskFullByAssignedTeamID

    @Test
    void shouldFindSingleTaskFullForAssignedTeamID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamID(testDataLoader.teamDelete.getID());

        assertFalse(taskSet.isEmpty());
        assertEquals(testDataLoader.teamDelete.getTeamTasks(), taskSet);
    }

    @Test
    void shouldFindMultipleTaskFullForAssignedTeamID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamID(testDataLoader.teamRead.getID());

        assertFalse(taskSet.isEmpty());
        assertEquals(testDataLoader.teamRead.getTeamTasks(), taskSet);
    }

    @Test
    void shouldFindNoneTaskFullForNonExistentTeamID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamID(999L);

        assertTrue(taskSet.isEmpty());
    }

    @Test
    void shouldFindNoneTaskFullForInvalidTeamID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamID(-1);

        assertTrue(taskSet.isEmpty());
    }

    /// findAllTaskFullByAssignedTeamIDAndAssignedUserID

    @Test
    void shouldFindSingleTaskFullByAssignedTeamIDAndAssignedUserID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamIDAndAssignedUserID(testDataLoader.teamRead.getID(), testDataLoader.userMember.getID());
        Set<TaskFull> expected = testDataLoader.allTasks.stream()
                .filter(task -> task.getAssignedTeam().equals(testDataLoader.teamRead))
                .filter(task -> task.hasUser(testDataLoader.userMember.getID()))
                .collect(Collectors.toUnmodifiableSet());

        assertFalse(taskSet.isEmpty());
        assertEquals(expected, taskSet);
    }

    @Test
    void shouldFindMultipleTaskFullByAssignedTeamIDAndAssignedUserID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamIDAndAssignedUserID(testDataLoader.teamRead.getID(), testDataLoader.userReadOwner.getID());
        Set<TaskFull> expected = testDataLoader.allTasks.stream()
                .filter(task -> task.getAssignedTeam().equals(testDataLoader.teamRead))
                .filter(task -> task.hasUser(testDataLoader.userReadOwner.getID()))
                .collect(Collectors.toUnmodifiableSet());

        assertFalse(taskSet.isEmpty());
        assertEquals(expected, taskSet);
    }

    @Test
    void shouldFindNoneTaskFullByNonExistentTeamIDAndAssignedUserID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamIDAndAssignedUserID(999L, testDataLoader.userMember.getID());

        assertTrue(taskSet.isEmpty());
    }

    @Test
    void shouldFindNoneTaskFullByAssignedTeamIDAndNonExistentUserID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamIDAndAssignedUserID(testDataLoader.teamRead.getID(), 999L);

        assertTrue(taskSet.isEmpty());
    }

    @Test
    void shouldFindNoneTaskFullByInvalidTeamIDAndAssignedUserID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamIDAndAssignedUserID(-1, testDataLoader.userMember.getID());

        assertTrue(taskSet.isEmpty());
    }

    @Test
    void shouldFindNoneTaskFullByAssignedTeamIDAndInvalidUserID(){
        Set<TaskFull> taskSet = taskRepository.findAllTaskFullByAssignedTeamIDAndAssignedUserID(testDataLoader.teamRead.getID(), -1);

        assertTrue(taskSet.isEmpty());
    }


    /// Schema tests

    @Test
    void shouldThrowForNullName(){
        Task task = new Task();
        task.setName(null);
        task.setDescription("desc");
        task.setTaskStatus(TaskStatus.NEW);
        task.setCreationDate(OffsetDateTime.now());
        task.setAssignedTeam(testDataLoader.teamWrite);

        assertThrows(DataIntegrityViolationException.class, () -> taskRepository.save(task));
    }

    @Test
    void shouldThrowForNullTaskStatus(){
        Task task = new Task();
        task.setName("name");
        task.setDescription("desc");
        task.setTaskStatus(null);
        task.setCreationDate(OffsetDateTime.now());
        task.setAssignedTeam(testDataLoader.teamWrite);

        assertThrows(DataIntegrityViolationException.class, () -> taskRepository.save(task));
    }

    @Test
    void shouldThrowForNullCreationDate(){
        Task task = new Task();
        task.setName("name");
        task.setDescription("desc");
        task.setTaskStatus(TaskStatus.NEW);
        task.setCreationDate(null);
        task.setAssignedTeam(testDataLoader.teamWrite);

        assertThrows(DataIntegrityViolationException.class, () -> taskRepository.save(task));
    }

    @Test
    void shouldThrowForNullTeam(){
        Task task = new Task();
        task.setName("name");
        task.setDescription("desc");
        task.setTaskStatus(TaskStatus.NEW);
        task.setCreationDate(OffsetDateTime.now());
        task.setAssignedTeam(null);

        assertThrows(DataIntegrityViolationException.class, () -> taskRepository.save(task));
    }
}
