package com.patrykmarchewka.concordiapi.Subtasks;


import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
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
public class SubtaskControllerTest {

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
    void shouldGetSubtasks(){
        var response = restClient.get().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamRead.getID(), testDataLoader.taskRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<SubtaskMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtasks attached to this task", response.getBody().getMessage());
        assertEquals(new TaskMemberDTO(testDataLoader.taskRead).getSubtasks(), response.getBody().getData());
    }

    @Test
    void shouldCreateNewSubtask(){
        String json = """
                {
                "name":"name",
                "description":"description",
                "taskStatus":"HALTED"
                }
                """;
        var response = restClient.post().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamWrite.getID(), testDataLoader.taskWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<SubtaskMemberDTO>>() {});
        var refreshedTask = testDataLoader.refreshTask(testDataLoader.taskWrite);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtask created", response.getBody().getMessage());
        assertEquals("name", response.getBody().getData().getName());
        assertEquals("description", response.getBody().getData().getDescription());
        assertEquals(TaskStatus.HALTED, response.getBody().getData().getTaskStatus());
        assertTrue(new TaskMemberDTO(refreshedTask).getSubtasks().contains(response.getBody().getData()));
    }

    @Test
    void shouldGetSpecificSubtask(){
        var response = restClient.get().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamRead.getID(), testDataLoader.taskRead.getID(), testDataLoader.subtaskRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<SubtaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtask details", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsSubtask(testDataLoader.subtaskRead));
    }

    @Test
    void shouldPutSubtask(){
        String json = """
                {
                "name":"newerSubtask",
                "description":"newerDescription",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.put().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskWrite.getID(), testDataLoader.subtaskWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<SubtaskMemberDTO>>() {});
        var refreshedSubtask = testDataLoader.refreshSubtask(testDataLoader.subtaskWrite);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtask fully changed", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsSubtask(refreshedSubtask));
    }

    @Test
    void shouldPatchSubtask(){
        String json = """
                {
                "name":"newestSubtask"
                }
                """;
        var response = restClient.patch().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskWrite.getID(), testDataLoader.subtaskWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<SubtaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtask updated", response.getBody().getMessage());
        assertEquals("newestSubtask", response.getBody().getData().getName());
    }

    @Test
    void shouldDeleteSubtask(){
        var response = restClient.delete().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamDelete.getID(), testDataLoader.taskDelete.getID(), testDataLoader.subtaskDelete.getID()).header("Authorization", "Bearer " + testDataLoader.jwtDelete).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtask has been deleted", response.getBody().getMessage());
        assertThrows(NotFoundException.class, () -> testDataLoader.refreshSubtask(testDataLoader.subtaskDelete));
    }
}
