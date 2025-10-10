package com.patrykmarchewka.concordiapi.Tasks;


import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.Exceptions.ImpossibleStateException;
import com.patrykmarchewka.concordiapi.TaskStatus;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskControllerTest {

    @Autowired
    private RestClient.Builder builder;
    @Autowired
    private TestDataLoader testDataLoader;

    private RestClient restClient;

    @LocalServerPort
    private int port;


    @BeforeAll
    void restInitialize(){
        this.restClient = builder.baseUrl("http://localhost:" + port).build();
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }


    @Test
    void shouldGetAllTasksInATeam(){
        //JSON Deserialization sees APIResponse<Set> so it doesn't add "type" field to TaskDTO even when annotated to do it
        //because of that it has issues with deserializing Set<TaskDTO> into objects
        //In this test responses are TaskManagerDTO, but for mixed DTOs it would need to be Map<String, Object> or something better
        var response = restClient.get().uri("/api/teams/{teamID}/tasks", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<TaskManagerDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All tasks available", response.getBody().getMessage());
        assertEquals(testDataLoader.teamRead.getTeamTasks().size(), response.getBody().getData().size());
        assertTrue(response.getBody().getData().stream().anyMatch(taskManagerDTO -> taskManagerDTO.equalsTask(testDataLoader.taskRead)));
    }

    @Test
    void shouldCreateNewTask(){
        String json = """
                {
                "name":"new task",
                "description":"new description",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.post().uri("/api/teams/{teamID}/tasks", testDataLoader.teamWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Created new task", response.getBody().getMessage());
        assertEquals("new task",response.getBody().getData().getName());
        assertEquals("new description", response.getBody().getData().getDescription());
        assertEquals(TaskStatus.NEW, response.getBody().getData().getTaskStatus());
        assertTrue(response.getBody().getData().getUsers().isEmpty());
        assertTrue(response.getBody().getData().getSubtasks().isEmpty());
    }
    
    @Test
    void shouldGetTasksAssignedToMe(){
        //JSON Deserialization sees APIResponse<Set> so it doesn't add "type" field to TaskDTO even when annotated to do it
        //because of that it has issues with deserializing Set<TaskDTO> into objects
        //In this test responses are TaskManagerDTO, but for mixed DTOs it would need to be Map<String, Object> or something better
        var response = restClient.get().uri("/api/teams/{teamID}/tasks/me", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<TaskManagerDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tasks assigned to me", response.getBody().getMessage());
        assertEquals(testDataLoader.userReadOwner.getUserTasks().size(), response.getBody().getData().size());
        assertTrue(response.getBody().getData().stream().anyMatch(taskManagerDTO -> taskManagerDTO.equalsTask(testDataLoader.taskRead)));
    }

    @Test
    void shouldGetInformationAboutTask(){
        //JSON Deserialization issue, doesn't know to what class deserialize with "type" field, field can be added like this to TaskDTO, it will not fix the Set<TaskDTO> issue
        //@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
        //@JsonSubTypes({
        //    @JsonSubTypes.Type(value = TeamAdminDTO.class, name = "TeamAdminDTO"),
        //    @JsonSubTypes.Type(value = TeamManagerDTO.class, name = "TeamManagerDTO"),
        //    @JsonSubTypes.Type(value = TeamMemberDTO.class, name = "TeamMemberDTO")
        //})

        var response = restClient.get().uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamRead.getID(), testDataLoader.taskRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskManagerDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task details", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsTask(testDataLoader.taskRead));
    }

    @Test
    void shouldPutTask(){
        String json = String.format("""
                {
                "name":"newest task",
                "description":"newest description",
                "users": [%d , %d],
                "taskStatus":"INPROGRESS"
                }
                """, testDataLoader.userWriteOwner.getID(), testDataLoader.userManager.getID());
        var response = restClient.put().uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});
        var refreshedTask = testDataLoader.refreshTaskNew(testDataLoader.taskWrite);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task fully changed", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsTask(refreshedTask));
        assertEquals(2, response.getBody().getData().getUsers().size());
        assertTrue(response.getBody().getData().getUsers().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userWriteOwner)));
        assertTrue(response.getBody().getData().getUsers().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userManager)));
    }

    @Test
    void shouldPatchTask(){
        String json = """
                {
                "name": "newer task"
                }
                """;
        var response = restClient.patch().uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task updated", response.getBody().getMessage());
        assertEquals("newer task", response.getBody().getData().getName());
    }

    @Test
    void shouldDeleteTask(){
        var response = restClient.delete().uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamDelete.getID(), testDataLoader.taskDelete.getID()).header("Authorization", "Bearer " + testDataLoader.jwtDelete).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task has been deleted", response.getBody().getMessage());
        assertThrows(ImpossibleStateException.class, () -> testDataLoader.refreshTaskNew(testDataLoader.taskDelete));
    }

    @Test
    void shouldAttachUserToTask(){
        var response = restClient.post().uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskWrite.getID(), testDataLoader.userAdmin.getID()).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User added to task", response.getBody().getMessage());
        assertTrue(response.getBody().getData().getUsers().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userAdmin)));
    }

    @Test
    void shouldRemoveUserFromTask(){
        var response = restClient.delete().uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskWrite.getID(), testDataLoader.userWriteOwner.getID()).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User removed from task", response.getBody().getMessage());
        assertFalse(response.getBody().getData().getUsers().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userWriteOwner)));
    }


}
