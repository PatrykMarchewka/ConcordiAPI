package com.patrykmarchewka.concordiapi.Subtasks;

import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.Tasks.TaskRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Teams.TeamRequestBodyHelper;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubtaskServiceTest implements SubtaskRequestBodyHelper, TaskRequestBodyHelper, TeamRequestBodyHelper, UserRequestBodyHelper {

    private final SubtaskService subtaskService;
    private final TestDataLoader testDataLoader;

    @Autowired
    public SubtaskServiceTest(SubtaskService subtaskService, TestDataLoader testDataLoader) {
        this.subtaskService = subtaskService;
        this.testDataLoader = testDataLoader;
    }

    @BeforeAll
    void initialize(){
        testDataLoader.loadDataForTests();
    }

    @AfterEach
    void refresh(){
        testDataLoader.subtaskWrite = testDataLoader.refreshSubtask(testDataLoader.subtaskWrite);
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }


    /// getSubtaskByID

    @Test
    void shouldGetSubtaskByID(){
        SubtaskIdentity subtask = subtaskService.getSubtaskByID(testDataLoader.taskMultiUserRead.getID(), testDataLoader.subtaskRead.getID());

        assertEquals(testDataLoader.subtaskRead.getID(), subtask.getID());
        assertEquals(testDataLoader.subtaskRead.getName(), subtask.getName());
        assertEquals(testDataLoader.subtaskRead.getDescription(), subtask.getDescription());
        assertEquals(testDataLoader.subtaskRead.getTaskStatus(), subtask.getTaskStatus());
        assertEquals(testDataLoader.subtaskRead.getTask(), subtask.getTask());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDGetSubtaskByID(long ID){
        assertThrows(NotFoundException.class, () -> subtaskService.getSubtaskByID(ID, testDataLoader.subtaskRead.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidSubtaskIDGetSubtaskByID(long ID){
        assertThrows(NotFoundException.class, () -> subtaskService.getSubtaskByID(testDataLoader.taskMultiUserRead.getID(), ID));
    }

    /// createSubtask

    @Test
    void shouldCreateSubtask(){
        SubtaskRequestBody body = new SubtaskRequestBody("NEWSubtask", null, TaskStatus.NEW);
        Subtask subtask = subtaskService.createSubtask(body, testDataLoader.teamWrite.getID(), testDataLoader.taskNoUsersWrite.getID());

        assertDoesNotThrow(subtask::getID);
        assertEquals(body.getName(), subtask.getName());
        assertEquals(body.getDescription(), subtask.getDescription());
        assertEquals(body.getTaskStatus(), subtask.getTaskStatus());
        assertEquals(testDataLoader.taskNoUsersWrite, subtask.getTask());
    }

    @Test
    void shouldCreateSubtaskDBCheck(){
        SubtaskRequestBody body = new SubtaskRequestBody("DBNEWSubtask", null, TaskStatus.NEW);
        Subtask subtask = subtaskService.createSubtask(body, testDataLoader.teamWrite.getID(), testDataLoader.taskNoUsersWrite.getID());

        SubtaskIdentity actual = subtaskService.getSubtaskByID(testDataLoader.taskNoUsersWrite.getID(), subtask.getID());

        assertEquals(subtask.getID(), actual.getID());
        assertEquals(subtask.getName(), actual.getName());
        assertEquals(subtask.getDescription(), actual.getDescription());
        assertEquals(subtask.getTaskStatus(), actual.getTaskStatus());
        assertEquals(subtask.getTask(), actual.getTask());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDCreateSubtask(long ID){
        SubtaskRequestBody body = new SubtaskRequestBody("NEWSubtask", null, TaskStatus.NEW);
        assertThrows(NotFoundException.class, () -> subtaskService.createSubtask(body, ID, testDataLoader.taskNoUsersWrite.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDCreateSubtask(long ID){
        SubtaskRequestBody body = new SubtaskRequestBody("NEWSubtask", null, TaskStatus.NEW);
        assertThrows(NotFoundException.class, () -> subtaskService.createSubtask(body, testDataLoader.teamWrite.getID(), ID));
    }

    /// putUpdate

    @Test
    void shouldPutUpdate(){
        SubtaskRequestBody body = new SubtaskRequestBody("PUTSubtask", "PUTSubtaskDesc", TaskStatus.NEW);
        SubtaskIdentity subtask = subtaskService.putUpdate(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID(), body);

        assertEquals(testDataLoader.subtaskWrite.getID(), subtask.getID());
        assertEquals(body.getName(), subtask.getName());
        assertEquals(body.getDescription(), subtask.getDescription());
        assertEquals(body.getTaskStatus(), subtask.getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserWrite, subtask.getTask());
    }

    @Test
    void shouldPutUpdateDBCheck(){
        SubtaskRequestBody body = new SubtaskRequestBody("DBPUTSubtask", "DBPUTSubtaskDesc", TaskStatus.NEW);
        SubtaskIdentity subtask = subtaskService.putUpdate(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID(), body);

        SubtaskIdentity actual = subtaskService.getSubtaskByID(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID());

        assertEquals(subtask.getID(), actual.getID());
        assertEquals(subtask.getName(), actual.getName());
        assertEquals(subtask.getDescription(), actual.getDescription());
        assertEquals(subtask.getTaskStatus(), actual.getTaskStatus());
        assertEquals(subtask.getTask(), actual.getTask());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskID(long ID){
        SubtaskRequestBody body = new SubtaskRequestBody("PUTSubtask", "PUTSubtaskDesc", TaskStatus.NEW);
        assertThrows(NotFoundException.class, () -> subtaskService.putUpdate(ID, testDataLoader.subtaskWrite.getID(), body));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidSubtaskID(long ID){
        SubtaskRequestBody body = new SubtaskRequestBody("PUTSubtask", "PUTSubtaskDesc", TaskStatus.NEW);
        assertThrows(NotFoundException.class, () -> subtaskService.putUpdate(testDataLoader.taskMultiUserWrite.getID(), ID, body));
    }

    /// patchUpdate

    @Test
    void shouldPatchUpdate(){
        SubtaskRequestBody body = new SubtaskRequestBody("PATCHSubtask", null, null);
        SubtaskIdentity subtask = subtaskService.patchUpdate(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID(), body);

        assertEquals(testDataLoader.subtaskWrite.getID(), subtask.getID());
        assertEquals(body.getName(), subtask.getName());
        assertEquals(testDataLoader.subtaskWrite.getDescription(), subtask.getDescription());
        assertEquals(testDataLoader.subtaskWrite.getTaskStatus(), subtask.getTaskStatus());
        assertEquals(testDataLoader.subtaskWrite.getTask(), subtask.getTask());
    }

    @Test
    void shouldPatchUpdateDBCheck(){
        SubtaskRequestBody body = new SubtaskRequestBody("DBPATCHSubtask", null, null);
        SubtaskIdentity subtask = subtaskService.patchUpdate(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID(), body);

        SubtaskIdentity actual = subtaskService.getSubtaskByID(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID());

        assertEquals(subtask.getID(), actual.getID());
        assertEquals(body.getName(), actual.getName());
        assertEquals(subtask.getDescription(), actual.getDescription());
        assertEquals(subtask.getTaskStatus(), actual.getTaskStatus());
        assertEquals(subtask.getTask(), actual.getTask());
    }

    @Test
    void shouldPatchUpdateFully(){
        SubtaskRequestBody body = new SubtaskRequestBody("PATCHSubtask", "PATCHSubtaskDesc", TaskStatus.NEW);
        SubtaskIdentity subtask = subtaskService.patchUpdate(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID(), body);

        assertEquals(testDataLoader.subtaskWrite.getID(), subtask.getID());
        assertEquals(body.getName(), subtask.getName());
        assertEquals(body.getDescription(), subtask.getDescription());
        assertEquals(body.getTaskStatus(), subtask.getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserWrite, subtask.getTask());
    }

    @Test
    void shouldPatchUpdateFullyDBCheck(){
        SubtaskRequestBody body = new SubtaskRequestBody("DBPATCHSubtask", "DBPATCHSubtaskDesc", TaskStatus.NEW);
        SubtaskIdentity subtask = subtaskService.patchUpdate(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID(), body);

        SubtaskIdentity actual = subtaskService.getSubtaskByID(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID());

        assertEquals(subtask.getID(), actual.getID());
        assertEquals(subtask.getName(), actual.getName());
        assertEquals(subtask.getDescription(), actual.getDescription());
        assertEquals(subtask.getTaskStatus(), actual.getTaskStatus());
        assertEquals(subtask.getTask(), actual.getTask());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDPatchUpdate(long ID){
        SubtaskRequestBody body = new SubtaskRequestBody("PATCHSubtask", "PATCHSubtaskDesc", TaskStatus.NEW);
        assertThrows(NotFoundException.class, () -> subtaskService.patchUpdate(ID, testDataLoader.subtaskWrite.getID(), body));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidSubtaskIDPatchUpdate(long ID){
        SubtaskRequestBody body = new SubtaskRequestBody("PATCHSubtask", "PATCHSubtaskDesc", TaskStatus.NEW);
        assertThrows(NotFoundException.class, () -> subtaskService.patchUpdate(testDataLoader.taskMultiUserWrite.getID(), ID, body));
    }

    /// saveSubtask

    @Test
    void shouldSaveSubtask(){
        testDataLoader.subtaskWrite.setName("NewName");
        subtaskService.saveSubtask(testDataLoader.subtaskWrite);

        assertEquals("NewName", testDataLoader.refreshSubtask(testDataLoader.subtaskWrite).getName());
    }

    /// deleteSubtask

    @Test
    void shouldDeleteSubtask(){
        subtaskService.deleteSubtask(testDataLoader.taskMultiUserDelete.getID(), testDataLoader.subtaskDelete.getID());

        assertThrows(NotFoundException.class, () -> subtaskService.getSubtaskByID(testDataLoader.taskMultiUserDelete.getID(), testDataLoader.subtaskDelete.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDDeleteSubtask(long ID){
        assertThrows(NotFoundException.class, () -> subtaskService.deleteSubtask(ID, testDataLoader.subtaskWrite.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidSubtaskIDDeleteSubtask(long ID){
        assertThrows(NotFoundException.class, () -> subtaskService.deleteSubtask(testDataLoader.taskMultiUserWrite.getID(), ID));
    }

    /// getSubtasksDTO

    @Test
    void shouldGetSubtasksDTO(){
        Set<SubtaskMemberDTO> set = subtaskService.getSubtasksDTO(testDataLoader.taskMultiUserRead);

        assertEquals(testDataLoader.taskMultiUserRead.getSubtasks().size(), set.size());
        assertEquals(set, testDataLoader.taskMultiUserRead.getSubtasks().stream().map(SubtaskMemberDTO::new).collect(Collectors.toUnmodifiableSet()));
    }
}
