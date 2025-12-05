package com.patrykmarchewka.concordiapi.DTO;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskDTOTest {

    private final TestDataLoader testDataLoader;

    @Autowired
    public TaskDTOTest(final TestDataLoader testDataLoader) {
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

    /// TaskMemberDTO
    @Test
    void assertTaskMemberDTO(){
        TaskMemberDTO dto = new TaskMemberDTO(testDataLoader.taskMultiUserRead);

        assertEquals(testDataLoader.taskMultiUserRead.getID(), dto.getID());
        assertEquals(testDataLoader.taskMultiUserRead.getName(), dto.getName());
        assertEquals(testDataLoader.taskMultiUserRead.getDescription(), dto.getDescription());
        assertEquals(testDataLoader.taskMultiUserRead.getTaskStatus(), dto.getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserRead.getSubtasks().size(), dto.getSubtasks().size());
        assertEquals(testDataLoader.taskMultiUserRead.getUserTasks().size(), dto.getUsers().size());
        assertEquals(testDataLoader.taskMultiUserRead.getCreationDate(), dto.getCreationDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUpdateDate(), dto.getUpdateDate());
        assertEquals(testDataLoader.taskMultiUserRead.getAssignedTeam(), dto.getAssignedTeam());
    }
}
