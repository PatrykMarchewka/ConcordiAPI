package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubtaskRepositoryTest implements SubtaskTestHelper, TaskTestHelper, TeamTestHelper{

    private final SubtaskRepository subtaskRepository;
    private final TestDataLoader testDataLoader;

    public SubtaskRepositoryTest(SubtaskRepository subtaskRepository, TestDataLoader testDataLoader) {
        this.subtaskRepository = subtaskRepository;
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


    /// findSubtaskByIDAndTaskID

    @Test
    void shouldFindSubtaskByIDAndTaskID(){
        Optional<SubtaskIdentity> subtask = subtaskRepository.findSubtaskByIDAndTaskID(testDataLoader.subtaskRead.getID(), testDataLoader.teamRead.getID());

        assertTrue(subtask.isPresent());
        assertEquals(testDataLoader.subtaskRead.getID(), subtask.get().getID());
        assertEquals(testDataLoader.subtaskRead.getName(), subtask.get().getName());
        assertEquals(testDataLoader.subtaskRead.getDescription(), subtask.get().getDescription());
        assertEquals(testDataLoader.subtaskRead.getTask(), subtask.get().getTask());
        assertEquals(testDataLoader.subtaskRead.getTaskStatus(), subtask.get().getTaskStatus());
    }

    @Test
    void shouldReturnEmptySubtaskForNonExistentIDAndTaskID(){
        Optional<SubtaskIdentity> subtask = subtaskRepository.findSubtaskByIDAndTaskID(999L, testDataLoader.teamRead.getID());

        assertFalse(subtask.isPresent());
    }

    @Test
    void shouldReturnEmptySubtaskForIDAndNonExistentTaskID(){
        Optional<SubtaskIdentity> subtask = subtaskRepository.findSubtaskByIDAndTaskID(testDataLoader.subtaskRead.getID(), 999L);

        assertFalse(subtask.isPresent());
    }

    @Test
    void shouldReturnEmptySubtaskForInvalidIDAndTaskID(){
        Optional<SubtaskIdentity> subtask = subtaskRepository.findSubtaskByIDAndTaskID(-1, testDataLoader.teamRead.getID());

        assertFalse(subtask.isPresent());
    }

    @Test
    void shouldReturnEmptySubtaskForIDAndInvalidTaskID(){
        Optional<SubtaskIdentity> subtask = subtaskRepository.findSubtaskByIDAndTaskID(testDataLoader.subtaskRead.getID(), -1);

        assertFalse(subtask.isPresent());
    }
}
