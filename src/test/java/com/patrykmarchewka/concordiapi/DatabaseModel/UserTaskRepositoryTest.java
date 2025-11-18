package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.TestDataLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTaskRepositoryTest {

    private final UserTaskRepository userTaskRepository;
    private final TestDataLoader testDataLoader;


    @Autowired
    public UserTaskRepositoryTest(UserTaskRepository userTaskRepository, TestDataLoader testDataLoader) {
        this.userTaskRepository = userTaskRepository;
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


    /// findUserTaskByAssignedUserIDAndAssignedTaskID

    @Test
    void shouldFindUserTaskByAssignedUserIDAndAssignedTaskID(){
        Optional<UserTask> userTask = userTaskRepository.findUserTaskByAssignedUserIDAndAssignedTaskID(testDataLoader.userMember.getID(), testDataLoader.taskMultiUserRead.getID());

        assertTrue(userTask.isPresent());
        assertEquals(testDataLoader.userMember, userTask.get().getAssignedUser());
        assertEquals(testDataLoader.taskMultiUserRead, userTask.get().getAssignedTask());
        assertTrue(testDataLoader.userMember.getUserTasks().contains(userTask.get()));
        assertTrue(testDataLoader.taskMultiUserRead.getUserTasks().contains(userTask.get()));
    }

    @Test
    void shouldReturnEmptyUserTaskForNonExistentUserIDAndAssignedTaskID(){
        Optional<UserTask> userTask = userTaskRepository.findUserTaskByAssignedUserIDAndAssignedTaskID(999L, testDataLoader.taskMultiUserRead.getID());

        assertFalse(userTask.isPresent());
    }

    @Test
    void shouldReturnEmptyUserTaskForAssignedUserIDAndNonExistentTaskID(){
        Optional<UserTask> userTask = userTaskRepository.findUserTaskByAssignedUserIDAndAssignedTaskID(testDataLoader.userMember.getID(), 999L);

        assertFalse(userTask.isPresent());
    }

    @Test
    void shouldReturnEmptyUserTaskForInvalidUserIDAndAssignedTaskID(){
        Optional<UserTask> userTask = userTaskRepository.findUserTaskByAssignedUserIDAndAssignedTaskID(-1, testDataLoader.taskMultiUserRead.getID());

        assertFalse(userTask.isPresent());
    }

    @Test
    void shouldReturnEmptyUserTaskForAssignedUserIDAndInvalidTaskID(){
        Optional<UserTask> userTask = userTaskRepository.findUserTaskByAssignedUserIDAndAssignedTaskID(testDataLoader.userMember.getID(), -1);

        assertFalse(userTask.isPresent());
    }
}
