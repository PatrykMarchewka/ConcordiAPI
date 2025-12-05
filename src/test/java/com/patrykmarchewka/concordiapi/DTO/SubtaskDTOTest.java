package com.patrykmarchewka.concordiapi.DTO;


import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
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
public class SubtaskDTOTest {

    private final TestDataLoader testDataLoader;

    @Autowired
    public SubtaskDTOTest(TestDataLoader testDataLoader) {
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

    /// SubtaskMember
    @Test
    void assertSubtaskMemberDTO(){
        SubtaskMemberDTO dto = new SubtaskMemberDTO(testDataLoader.subtaskRead);

        assertEquals(testDataLoader.subtaskRead.getID(), dto.getID());
        assertEquals(testDataLoader.subtaskRead.getName(), dto.getName());
        assertEquals(testDataLoader.subtaskRead.getDescription(), dto.getDescription());
        assertEquals(testDataLoader.subtaskRead.getTask(), dto.getTask());
        assertEquals(testDataLoader.subtaskRead.getTaskStatus(), dto.getTaskStatus());
    }
}
