package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Task;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserTask;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskServiceTest {

    private final TaskService taskService;
    private final TestDataLoader testDataLoader;

    @Autowired
    public TaskServiceTest(TaskService taskService, TestDataLoader testDataLoader) {
        this.taskService = taskService;
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


    /// getTasksWithoutUsers

    @Test
    void shouldGetTasksWithoutUsers(){
        Set<TaskFull> set = taskService.getTasksWithoutUsers(testDataLoader.teamRead.getID(), testDataLoader.userReadOwner.getID());

        assertEquals(1, set.size());
        assertTrue(set.contains(testDataLoader.taskNoUsersRead));
    }

    /**
     * Member role users can only see tasks assigned to them, not all
     */
    @Test
    void shouldReturnEmptyForMemberRoleGetTasksWithoutUsers(){
        Set<TaskFull> set = taskService.getTasksWithoutUsers(testDataLoader.teamRead.getID(), testDataLoader.userMember.getID());

        assertTrue(set.isEmpty());
    }

    @Test
    void shouldThrowForBannedRoleGetTasksWithoutUsers(){
        assertThrows(NoPrivilegesException.class, () -> taskService.getTasksWithoutUsers(testDataLoader.teamRead.getID(), testDataLoader.userBanned.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetTasksWithoutUsers(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTasksWithoutUsers(ID, testDataLoader.userReadOwner.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDGetTasksWithoutUsers(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTasksWithoutUsers(testDataLoader.teamRead.getID(), ID));
    }

    /// getTasksByStatus

    @Test
    void shouldGetTasksByStatus(){
        Set<TaskFull> set = taskService.getTasksByStatus(TaskStatus.NEW, testDataLoader.teamRead.getID(), testDataLoader.userReadOwner.getID());

        assertEquals(2, set.size());
        assertTrue(set.contains(testDataLoader.taskMultiUserRead));
        assertTrue(set.contains(testDataLoader.taskNoUsersRead));
    }

    /**
     * Member role users can only see tasks assigned to them, not all
     */
    @Test
    void shouldReturnPartialGetTasksByStatus(){
        Set<TaskFull> set = taskService.getTasksByStatus(TaskStatus.NEW, testDataLoader.teamRead.getID(), testDataLoader.userMember.getID());

        assertEquals(1, set.size());
        assertTrue(set.contains(testDataLoader.taskMultiUserRead));
    }

    @Test
    void shouldThrowForBannedRoleGetTasksByStatus(){
        assertThrows(NoPrivilegesException.class, () -> taskService.getTasksByStatus(TaskStatus.NEW, testDataLoader.teamRead.getID(), testDataLoader.userBanned.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetTasksByStatus(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTasksByStatus(TaskStatus.NEW, ID, testDataLoader.userReadOwner.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDGetTasksByStatus(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTasksByStatus(TaskStatus.NEW, testDataLoader.teamRead.getID(), ID));
    }

    /// createTask

    @Test
    void shouldCreateTask(){
        TaskRequestBody body = new TaskRequestBody("NEWTask", "NEWTaskDesc", Set.of(), TaskStatus.NEW);
        Task task = taskService.createTask(body, testDataLoader.teamWrite);

        assertDoesNotThrow(task::getID);
        assertEquals(body.getName(), task.getName());
    }

    @Test
    void shouldCreateTaskDBCheck(){
        TaskRequestBody body = new TaskRequestBody("DBNEWTask", "DBNEWTaskDesc", Set.of(), TaskStatus.NEW);
        Task task = taskService.createTask(body, testDataLoader.teamWrite);

        TaskIdentity actual = taskService.getTaskByIDAndTeamID(task.getID(), testDataLoader.teamWrite.getID());

        assertEquals(task.getID(), actual.getID());
        assertEquals(task.getName(), actual.getName());
    }

    @Test
    void shouldThrowForUserOutsideTeamCreateTask(){
        TaskRequestBody body = new TaskRequestBody("NEWTask", "NEWTaskDesc", Set.of((int)testDataLoader.userReadOwner.getID()), TaskStatus.NEW);
        assertThrows(BadRequestException.class, () -> taskService.createTask(body, testDataLoader.teamWrite));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDCreateTask(long ID){
        TaskRequestBody body = new TaskRequestBody("NEWTask", "NEWTaskDesc", Set.of((int)ID), TaskStatus.NEW);
        assertThrows(BadRequestException.class, () -> taskService.createTask(body, testDataLoader.teamWrite));
    }

    /// putTask

    @Test
    void shouldPutTask(){
        TaskRequestBody body = new TaskRequestBody("PUTTask", "PUTTaskDesc", Set.of(), TaskStatus.FINISHED);
        TaskFull task = taskService.putTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, testDataLoader.taskNoUsersWrite.getID());

        assertDoesNotThrow(task::getID);
        assertEquals(body.getName(), task.getName());
        assertEquals(body.getDescription(), task.getDescription());
        assertEquals(body.getUsers().stream().map(Integer::longValue).collect(Collectors.toUnmodifiableSet()), task.getUserTasks().stream().map(UserTask::getID).collect(Collectors.toUnmodifiableSet()));
        assertEquals(body.getTaskStatus(), task.getTaskStatus());
        assertEquals(testDataLoader.teamWrite, task.getAssignedTeam());
        assertEquals(testDataLoader.taskNoUsersWrite.getCreationDate(), task.getCreationDate());
    }

    @Test
    void shouldPutTaskDBCheck(){
        TaskRequestBody body = new TaskRequestBody("DBPUTTask", "DBPUTTaskDesc", Set.of(), TaskStatus.FINISHED);
        TaskFull task = taskService.putTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, testDataLoader.taskNoUsersWrite.getID());

        TaskFull actual = taskService.getTaskFullByIDAndTeamID(task.getID(), testDataLoader.teamWrite.getID());

        assertEquals(task.getID(), actual.getID());
        assertEquals(task.getName(), actual.getName());
        assertEquals(task.getDescription(), actual.getDescription());
        assertEquals(task.getUserTasks(), actual.getUserTasks());
        assertEquals(task.getTaskStatus(), actual.getTaskStatus());
        assertEquals(task.getAssignedTeam(), actual.getAssignedTeam());
        assertEquals(task.getCreationDate(), actual.getCreationDate());
        assertEquals(task.getUpdateDate(), actual.getUpdateDate());
    }

    @Test
    void shouldThrowForUserOutsideTeamPutTask(){
        TaskRequestBody body = new TaskRequestBody("PUTTask", null, Set.of((int)testDataLoader.userReadOwner.getID()), null);
        assertThrows(BadRequestException.class, () -> taskService.putTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, testDataLoader.taskNoUsersWrite.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDPutTask(long ID){
        TaskRequestBody body = new TaskRequestBody("PUTTask", "PUTTaskDesc", Set.of(), TaskStatus.FINISHED);
        assertThrows(NotFoundException.class, () -> taskService.putTask(body, ID, testDataLoader.teamWrite, testDataLoader.taskNoUsersWrite.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDPutTask(long ID){
        TaskRequestBody body = new TaskRequestBody("PUTTask", "PUTTaskDesc", Set.of(), TaskStatus.FINISHED);
        assertThrows(NotFoundException.class, () -> taskService.putTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, ID));
    }

    /**
     * Member role users can only edit tasks assigned to them, not all
     */
    @Test
    void shouldThrowForMemberRolePutTask(){
        TaskRequestBody body = new TaskRequestBody("PUTTask", "PUTTaskDesc", Set.of(), TaskStatus.FINISHED);
        assertThrows(NoPrivilegesException.class, () -> taskService.putTask(body, testDataLoader.userMember.getID(), testDataLoader.teamWrite, testDataLoader.taskNoUsersWrite.getID()));
    }

    /// patchTask

    @Test
    void shouldPatchTask(){
        TaskRequestBody body = new TaskRequestBody("PATCHTask", null, null, null);
        TaskFull task = taskService.patchTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, testDataLoader.taskOwnerUserWrite.getID());

        assertEquals(testDataLoader.taskOwnerUserWrite.getID(), task.getID());
        assertEquals(body.getName(), task.getName());
        assertEquals(testDataLoader.taskOwnerUserWrite.getDescription(), task.getDescription());
        assertEquals(testDataLoader.taskOwnerUserWrite.getUserTasks(), task.getUserTasks());
        assertEquals(testDataLoader.taskOwnerUserWrite.getTaskStatus(), task.getTaskStatus());
        assertEquals(testDataLoader.taskOwnerUserWrite.getAssignedTeam(), task.getAssignedTeam());
        assertEquals(testDataLoader.taskOwnerUserWrite.getCreationDate(), task.getCreationDate());
    }

    @Test
    void shouldPatchTaskDBCheck(){
        TaskRequestBody body = new TaskRequestBody("DBPATCHTask", null, null, null);
        TaskFull task = taskService.patchTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, testDataLoader.taskOwnerUserWrite.getID());

        TaskFull actual = taskService.getTaskFullByIDAndTeamID(task.getID(), testDataLoader.teamWrite.getID());

        assertEquals(task.getID(), actual.getID());
        assertEquals(task.getName(), actual.getName());
        assertEquals(task.getDescription(), actual.getDescription());
        assertEquals(task.getUserTasks(), actual.getUserTasks());
        assertEquals(task.getTaskStatus(), actual.getTaskStatus());
        assertEquals(task.getAssignedTeam(), actual.getAssignedTeam());
        assertEquals(task.getCreationDate(), actual.getCreationDate());
        assertEquals(task.getUpdateDate(), actual.getUpdateDate());
    }

    @Test
    void shouldPatchTaskFully(){
        TaskRequestBody body = new TaskRequestBody("PATCHTask", "PATCHTaskDesc", Set.of((int)testDataLoader.userAdmin.getID()), TaskStatus.FINISHED);
        TaskFull task = taskService.patchTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, testDataLoader.taskOwnerUserWrite.getID());

        assertEquals(testDataLoader.taskOwnerUserWrite.getID(), task.getID());
        assertEquals(body.getName(), task.getName());
        assertEquals(body.getDescription(), task.getDescription());
        assertEquals(body.getUsers().stream().map(Integer::longValue).collect(Collectors.toUnmodifiableSet()), task.getUserTasks().stream().map(UserTask::getAssignedUser).map(User::getID).collect(Collectors.toUnmodifiableSet()));
        assertEquals(body.getTaskStatus(), task.getTaskStatus());
        assertEquals(testDataLoader.taskOwnerUserWrite.getAssignedTeam(), task.getAssignedTeam());
        assertEquals(testDataLoader.taskOwnerUserWrite.getCreationDate(), task.getCreationDate());
    }

    @Test
    void shouldPatchTaskFullyDBCheck(){
        TaskRequestBody body = new TaskRequestBody("DBPATCHTask", "DBPATCHTaskDesc", Set.of((int)testDataLoader.userManager.getID()), TaskStatus.FINISHED);
        TaskFull task = taskService.patchTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, testDataLoader.taskOwnerUserWrite.getID());

        TaskFull actual = taskService.getTaskFullByIDAndTeamID(task.getID(), testDataLoader.teamWrite.getID());

        assertEquals(task.getID(), actual.getID());
        assertEquals(task.getName(), actual.getName());
        assertEquals(task.getDescription(), actual.getDescription());
        assertEquals(task.getUserTasks(), actual.getUserTasks());
        assertEquals(task.getTaskStatus(), actual.getTaskStatus());
        assertEquals(task.getAssignedTeam(), actual.getAssignedTeam());
        assertEquals(task.getCreationDate(), actual.getCreationDate());
        assertEquals(task.getUpdateDate(), actual.getUpdateDate());
    }

    @Test
    void shouldThrowForUserOutsideTeamPatchTask(){
        TaskRequestBody body = new TaskRequestBody("PATCHTask", null, Set.of((int)testDataLoader.userReadOwner.getID()), null);
        assertThrows(BadRequestException.class, () -> taskService.patchTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, testDataLoader.taskNoUsersWrite.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDPatchTask(long ID){
        TaskRequestBody body = new TaskRequestBody("PATCHTask", "PATCHTaskDesc", Set.of(), TaskStatus.FINISHED);
        assertThrows(NotFoundException.class, () -> taskService.patchTask(body, ID, testDataLoader.teamWrite, testDataLoader.taskOwnerUserWrite.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDPatchTask(long ID){
        TaskRequestBody body = new TaskRequestBody("PATCHTask", "PATCHTaskDesc", Set.of(), TaskStatus.FINISHED);
        assertThrows(NotFoundException.class, () -> taskService.patchTask(body, testDataLoader.userWriteOwner.getID(), testDataLoader.teamWrite, ID));
    }

    /**
     * Member role users can only edit tasks assigned to them, not all
     */
    @Test
    void shouldThrowForMemberRolePatchTask(){
        TaskRequestBody body = new TaskRequestBody("PATCHTask", "PATCHTaskDesc", Set.of(), TaskStatus.FINISHED);
        assertThrows(NoPrivilegesException.class, () -> taskService.patchTask(body, testDataLoader.userMember.getID(), testDataLoader.teamWrite, testDataLoader.taskOwnerUserWrite.getID()));
    }

    /// saveTask

    @Test
    void shouldSaveTask(){
        testDataLoader.taskBannedUserWrite.setTaskStatus(TaskStatus.INPROGRESS);
        taskService.saveTask(testDataLoader.taskBannedUserWrite);

        Task actual = testDataLoader.refreshTask(testDataLoader.taskBannedUserWrite);

        assertEquals(TaskStatus.INPROGRESS, actual.getTaskStatus());
    }

    /// deleteTask

    @Test
    void shouldDeleteTask(){
        taskService.deleteTask(testDataLoader.taskMultiUserDelete.getID(), testDataLoader.teamDelete.getID());
        assertThrows(NotFoundException.class, () -> taskService.getTaskByIDAndTeamID(testDataLoader.taskMultiUserDelete.getID(), testDataLoader.teamDelete.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDDeleteTask(long ID){
        assertThrows(NotFoundException.class, () -> taskService.deleteTask(ID, testDataLoader.teamDelete.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDDeleteTask(long ID){
        assertThrows(NotFoundException.class, () -> taskService.deleteTask(testDataLoader.taskOwnerUserRead.getID(), ID));
    }

    /// getAllTasksWithRoleCheck

    //Owner = all, member = just allowed, banned = throw Nopriv, invalid user, invalid team
    @Test
    void shouldGetAllTasksWithRoleCheck(){
        Set<TaskMemberDTO> set = taskService.getAllTasksWithRoleCheck(testDataLoader.userReadOwner.getID(), testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead.getTeamTasks().size(), set.size());
        assertTrue(set.containsAll(testDataLoader.teamRead.getTeamTasks().stream().map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet())));
    }

    @Test
    void shouldReturnPartialGetAllTasksWithRoleCheck(){
        Set<TaskMemberDTO> set = taskService.getAllTasksWithRoleCheck(testDataLoader.userMember.getID(), testDataLoader.teamRead.getID());

        assertEquals(1, set.size());
        assertTrue(set.contains(new TaskMemberDTO(testDataLoader.taskMultiUserRead)));
    }

    @Test
    void shouldThrowForBannedRoleGetAllTasksWithRoleCheck(){
        assertThrows(NoPrivilegesException.class, () -> taskService.getAllTasksWithRoleCheck(testDataLoader.userBanned.getID(), testDataLoader.teamRead.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDGetAllTasksWithRoleCheck(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getAllTasksWithRoleCheck(ID, testDataLoader.teamRead.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetAllTasksWithRoleCheck(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getAllTasksWithRoleCheck(testDataLoader.userReadOwner.getID(), ID));
    }

    /// getAllTasksDTO

    @Test
    void shouldGetAllTasksDTO(){
        Set<TaskMemberDTO> set = taskService.getAllTasksDTO(testDataLoader.teamRead.getID());

        assertTrue(set.containsAll(testDataLoader.teamRead.getTeamTasks().stream().map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet())));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetAllTasksDTO(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getAllTasksDTO(ID));
    }

    /// getAllTasksAssignedToMe

    @Test
    void shouldGetAllTasksAssignedToMe(){
        Set<TaskMemberDTO> set = taskService.getAllTasksAssignedToMe(testDataLoader.teamRead.getID(), testDataLoader.userReadOwner.getID());

        assertEquals(testDataLoader.userReadOwner.getUserTasks().size(), set.size());
        assertTrue(set.containsAll(testDataLoader.userReadOwner.getUserTasks().stream().map(UserTask::getAssignedTask).map(TaskMemberDTO::new).collect(Collectors.toUnmodifiableSet())));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDGetAllTasksAssignedToMe(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getAllTasksAssignedToMe(ID, testDataLoader.teamRead.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetAllTasksAssignedToMe(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getAllTasksAssignedToMe(testDataLoader.userReadOwner.getID(), ID));
    }

    /// getInactiveTasksDTO

    @Test
    void shouldThrowForBannedRoleGetInactiveTasksDTO(){
        assertThrows(NoPrivilegesException.class, () -> taskService.getInactiveTasksDTO(1, testDataLoader.teamRead.getID(), testDataLoader.userBanned.getID()));
    }


    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void shouldThrowForInvalidDaysGetInactiveTasksDTO(int days){
        assertThrows(BadRequestException.class, () -> taskService.getInactiveTasksDTO(days, testDataLoader.teamRead.getID(), testDataLoader.userReadOwner.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetInactiveTasksDTO(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getInactiveTasksDTO(1, ID, testDataLoader.userReadOwner.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDGetInactiveTasksDTO(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getInactiveTasksDTO(1, testDataLoader.teamRead.getID(), ID));
    }

    /// addUserToTask

    @Test
    void shouldAddUserToTask(){
        taskService.addUserToTask(testDataLoader.teamWrite, testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userAdmin.getID());

        assertTrue(testDataLoader.refreshTask(testDataLoader.taskMultiUserWrite).hasUser(testDataLoader.userAdmin.getID()));
    }

    @Test
    void shouldThrowForUserOutsideTeamAddUserToTask(){
        assertThrows(BadRequestException.class, () -> taskService.addUserToTask(testDataLoader.teamWrite, testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userNoTeam.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDAddUserToTask(long ID){
        assertThrows(NotFoundException.class, () -> taskService.addUserToTask(testDataLoader.teamWrite, ID, testDataLoader.userAdmin.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDAddUserToTask(long ID){
        assertThrows(BadRequestException.class, () -> taskService.addUserToTask(testDataLoader.teamWrite, testDataLoader.taskMultiUserWrite.getID(), ID));
    }

    /// removeUserFromTask

    @Test
    void shouldRemoveUserFromTask(){
        taskService.removeUserFromTask(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.teamWrite.getID(), testDataLoader.userMember.getID());

        assertFalse(testDataLoader.refreshTask(testDataLoader.taskMultiUserWrite).hasUser(testDataLoader.userMember.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDRemoveUserFromTask(long ID){
        assertThrows(NotFoundException.class, () -> taskService.removeUserFromTask(ID, testDataLoader.teamWrite.getID(), testDataLoader.userMember.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDRemoveUserFromTask(long ID){
        assertThrows(NotFoundException.class, () -> taskService.removeUserFromTask(testDataLoader.taskMultiUserWrite.getID(), ID, testDataLoader.userMember.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDRemoveUserFromTask(long ID){
        assertThrows(NotFoundException.class, () -> taskService.removeUserFromTask(testDataLoader.taskMultiUserWrite.getID(), testDataLoader.teamWrite.getID(), ID));
    }

    /// getTaskByIDAndTeamID

    @Test
    void shouldGetTaskByIDAndTeamID(){
        TaskIdentity task = taskService.getTaskByIDAndTeamID(testDataLoader.taskMultiUserRead.getID(), testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.taskMultiUserRead.getID(), task.getID());
        assertEquals(testDataLoader.taskMultiUserRead.getName(), task.getName());
        assertEquals(testDataLoader.taskMultiUserRead.getDescription(), task.getDescription());
        assertEquals(testDataLoader.taskMultiUserRead.getTaskStatus(), task.getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserRead.getAssignedTeam(), task.getAssignedTeam());
        assertEquals(testDataLoader.taskMultiUserRead.getCreationDate(), task.getCreationDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUpdateDate(), task.getUpdateDate());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDGetTaskByIDAndTeamID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTaskByIDAndTeamID(ID, testDataLoader.teamRead.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetTaskByIDAndTeamID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTaskByIDAndTeamID(testDataLoader.taskMultiUserRead.getID(), ID));
    }

    /// getTaskWithUserTasksByIDAndTeamID

    @Test
    void shouldGetTaskWithUserTasksByIDAndTeamID(){
        TaskWithUserTasks task = taskService.getTaskWithUserTasksByIDAndTeamID(testDataLoader.taskMultiUserRead.getID(), testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.taskMultiUserRead.getID(), task.getID());
        assertEquals(testDataLoader.taskMultiUserRead.getName(), task.getName());
        assertEquals(testDataLoader.taskMultiUserRead.getDescription(), task.getDescription());
        assertEquals(testDataLoader.taskMultiUserRead.getTaskStatus(), task.getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserRead.getAssignedTeam(), task.getAssignedTeam());
        assertEquals(testDataLoader.taskMultiUserRead.getCreationDate(), task.getCreationDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUpdateDate(), task.getUpdateDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUserTasks(), task.getUserTasks());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDGetTaskWithUserTasksByIDAndTeamID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTaskWithUserTasksByIDAndTeamID(ID, testDataLoader.teamRead.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetTaskWithUserTasksByIDAndTeamID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTaskWithUserTasksByIDAndTeamID(testDataLoader.taskMultiUserRead.getID(), ID));
    }

    /// getTaskWithSubtasksByIDAndTeamID

    @Test
    void shouldGetTaskWithSubtasksByIDAndTeamID(){
        TaskWithSubtasks task = taskService.getTaskWithSubtasksByIDAndTeamID(testDataLoader.taskMultiUserRead.getID(), testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.taskMultiUserRead.getID(), task.getID());
        assertEquals(testDataLoader.taskMultiUserRead.getName(), task.getName());
        assertEquals(testDataLoader.taskMultiUserRead.getDescription(), task.getDescription());
        assertEquals(testDataLoader.taskMultiUserRead.getTaskStatus(), task.getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserRead.getAssignedTeam(), task.getAssignedTeam());
        assertEquals(testDataLoader.taskMultiUserRead.getCreationDate(), task.getCreationDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUpdateDate(), task.getUpdateDate());
        assertEquals(testDataLoader.taskMultiUserRead.getSubtasks(), task.getSubtasks());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDGetTaskWithSubtasksByIDAndTeamID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTaskWithSubtasksByIDAndTeamID(ID, testDataLoader.teamRead.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetTaskWithSubtasksByIDAndTeamID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTaskWithSubtasksByIDAndTeamID(testDataLoader.taskMultiUserRead.getID(), ID));
    }

    /// getTaskFullByIDAndTeamID

    @Test
    void shouldGetTaskFullByIDAndTeamID(){
        TaskFull task = taskService.getTaskFullByIDAndTeamID(testDataLoader.taskMultiUserRead.getID(), testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.taskMultiUserRead.getID(), task.getID());
        assertEquals(testDataLoader.taskMultiUserRead.getName(), task.getName());
        assertEquals(testDataLoader.taskMultiUserRead.getDescription(), task.getDescription());
        assertEquals(testDataLoader.taskMultiUserRead.getTaskStatus(), task.getTaskStatus());
        assertEquals(testDataLoader.taskMultiUserRead.getAssignedTeam(), task.getAssignedTeam());
        assertEquals(testDataLoader.taskMultiUserRead.getCreationDate(), task.getCreationDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUpdateDate(), task.getUpdateDate());
        assertEquals(testDataLoader.taskMultiUserRead.getUserTasks(), task.getUserTasks());
        assertEquals(testDataLoader.taskMultiUserRead.getSubtasks(), task.getSubtasks());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTaskIDGetTaskFullByIDAndTeamID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTaskFullByIDAndTeamID(ID, testDataLoader.teamRead.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetTaskFullByIDAndTeamID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getTaskFullByIDAndTeamID(testDataLoader.taskMultiUserRead.getID(), ID));
    }

    /// getAllTaskFullByTeamID

    @Test
    void shouldGetAllTaskFullByTeamID(){
        Set<TaskFull> set = taskService.getAllTaskFullByTeamID(testDataLoader.teamRead.getID());

        assertTrue(testDataLoader.teamRead.getTeamTasks().containsAll(set));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetAllTaskFullByTeamID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getAllTaskFullByTeamID(ID));
    }

    /// getAllTaskFullByTeamIDAndUserID

    @Test
    void shouldGetAllTaskFullByTeamIDAndUserID(){
        Set<TaskFull> set = taskService.getAllTaskFullByTeamIDAndUserID(testDataLoader.teamRead.getID(), testDataLoader.userReadOwner.getID());

        assertEquals(2, set.size());
        assertTrue(set.contains(testDataLoader.taskMultiUserRead));
        assertTrue(set.contains(testDataLoader.taskOwnerUserRead));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetAllTaskFullByTeamIDAndUserID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getAllTaskFullByTeamIDAndUserID(ID, testDataLoader.userReadOwner.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDGetAllTaskFullByTeamIDAndUserID(long ID){
        assertThrows(NotFoundException.class, () -> taskService.getAllTaskFullByTeamIDAndUserID(testDataLoader.teamRead.getID(), ID));
    }
}
