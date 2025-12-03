package com.patrykmarchewka.concordiapi.Subtasks;


import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Subtask;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SubtaskControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient.Builder builder;
    private final TestDataLoader testDataLoader;
    private RestClient restClient;

    @Autowired
    public SubtaskControllerTest(final RestClient.Builder builder, final TestDataLoader testDataLoader){
        this.builder = builder;
        this.testDataLoader = testDataLoader;
    }


    @BeforeAll
    void restInitialize(){
        this.restClient = builder.baseUrl("http://localhost:" + port).build();
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }


    /// getSubtasks

    /// 200
    @Test
    void shouldGetSubtasks(){
        var response = restClient.get().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamRead.getID(), testDataLoader.taskMultiUserRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<SubtaskMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtasks attached to this task", response.getBody().getMessage());
        assertEquals(testDataLoader.taskMultiUserRead.getSubtasks().size(), response.getBody().getData().size());
        assertEquals(testDataLoader.taskMultiUserRead.getSubtasks().stream().map(Subtask::getID).collect(Collectors.toUnmodifiableSet()), response.getBody().getData().stream().map(SubtaskMemberDTO::getID).collect(Collectors.toUnmodifiableSet()));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetSubtasks(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamRead.getID(), testDataLoader.taskMultiUserRead.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesGetSubtasks(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamRead.getID(), testDataLoader.taskMultiUserRead.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtBanned)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDGetSubtasks(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", -1, testDataLoader.taskMultiUserRead.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidTaskIDGetSubtasks(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamRead.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// createSubtask

    /// 201
    @Test
    void shouldCreateSubtask(){
        String json = """
                {
                "name":"name",
                "description":"description",
                "taskStatus":"HALTED"
                }
                """;
        var response = restClient.post().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<SubtaskMemberDTO>>() {});

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("name", response.getBody().getData().getName());
        assertEquals("description", response.getBody().getData().getDescription());
        assertEquals(TaskStatus.HALTED, response.getBody().getData().getTaskStatus());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyCreateSubtask(){
        String json = """
                {
                "description":"description",
                "taskStatus":"HALTED"
                }
                """;
        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        LinkedHashMap<String, String> errors = (LinkedHashMap<String, String>) response.getBody().getData();
        assertEquals("Field cannot be empty", errors.get("name"));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationCreateSubtask(){
        String json = """
                {
                "name":"name",
                "description":"description",
                "taskStatus":"HALTED"
                }
                """;
        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesCreateSubtask(){
        String json = """
                {
                "name":"name",
                "description":"description",
                "taskStatus":"HALTED"
                }
                """;
        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtBanned)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDCreateSubtask(){
        String json = """
                {
                "name":"name",
                "description":"description",
                "taskStatus":"HALTED"
                }
                """;

        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", -1, testDataLoader.taskMultiUserWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidTaskIDCreateSubtask(){
        String json = """
                {
                "name":"name",
                "description":"description",
                "taskStatus":"HALTED"
                }
                """;

        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks", testDataLoader.teamWrite.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }


    /// getSubtaskByID

    /// 200
    @Test
    void shouldGetSubtaskByID(){
        var response = restClient.get().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamRead.getID(), testDataLoader.taskMultiUserRead.getID(), testDataLoader.subtaskRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<SubtaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtask details", response.getBody().getMessage());
        assertEquals(testDataLoader.subtaskRead.getID(), response.getBody().getData().getID());
        assertEquals(testDataLoader.subtaskRead.getName(), response.getBody().getData().getName());
        assertEquals(testDataLoader.subtaskRead.getDescription(), response.getBody().getData().getDescription());
        assertEquals(testDataLoader.subtaskRead.getTaskStatus(), response.getBody().getData().getTaskStatus());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetSubtaskByID(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamRead.getID(), testDataLoader.taskMultiUserRead.getID(), testDataLoader.subtaskRead.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesGetSubtaskByID(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamRead.getID(), testDataLoader.taskMultiUserRead.getID(), testDataLoader.subtaskRead.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtBanned)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDGetSubtaskByID(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", -1, testDataLoader.taskMultiUserRead.getID(), testDataLoader.subtaskRead.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidTaskIDGetSubtaskByID(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamRead.getID(), -1, testDataLoader.subtaskRead.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidSubtaskIDGetSubtaskByID(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamRead.getID(), testDataLoader.taskMultiUserRead.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }


    /// putSubtask

    /// 200
    @Test
    void shouldPutSubtask(){
        String json = """
                {
                "name":"newerSubtask",
                "description":"newerDescription",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.put().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<SubtaskMemberDTO>>() {});


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtask fully changed", response.getBody().getMessage());
        assertEquals(testDataLoader.subtaskWrite.getID(), response.getBody().getData().getID());
        assertEquals("newerSubtask", response.getBody().getData().getName());
        assertEquals("newerDescription", response.getBody().getData().getDescription());
        assertEquals(TaskStatus.NEW, response.getBody().getData().getTaskStatus());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyPutSubtask(){
        String json = """
                {
                "description":"newerDescription",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        LinkedHashMap<String, String> errors = (LinkedHashMap<String, String>) response.getBody().getData();
        assertEquals("Field cannot be empty", errors.get("name"));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationPutSubtask(){
        String json = """
                {
                "name":"newerSubtask",
                "description":"newerDescription",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesPutSubtask(){
        String json = """
                {
                "name":"newerSubtask",
                "description":"newerDescription",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtBanned)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDPutSubtask(){
        String json = """
                {
                "name":"newerSubtask",
                "description":"newerDescription",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", -1, testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidTaskIDPutSubtask(){
        String json = """
                {
                "name":"newerSubtask",
                "description":"newerDescription",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), -1, testDataLoader.subtaskWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidSubtaskIDPutSubtask(){
        String json = """
                {
                "name":"newerSubtask",
                "description":"newerDescription",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }


    /// patchSubtask

    /// 200
    @Test
    void shouldPatchSubtask(){
        String json = """
                {
                "name":"newestSubtask"
                }
                """;
        var response = restClient.patch().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<SubtaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtask updated", response.getBody().getMessage());
        assertEquals("newestSubtask", response.getBody().getData().getName());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyPatchSubtask(){
        String json = """
                {
                    "name": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut facilisis blandit est. Nam vel ultricies erat. Sed odio eros, auctor nec ante ac, commodo gravida eros. Fusce nisi elit, commodo ut orci quis, rhoncus ultricies nisi. Sed fringilla tortor magna..."
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        LinkedHashMap<String, String> errors = (LinkedHashMap<String, String>) response.getBody().getData();
        assertEquals("Value must be between 1 and 255 characters", errors.get("name"));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationPatchSubtask(){
        String json = """
                {
                "name":"newestSubtask"
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesPatchSubtask(){
        String json = """
                {
                "name":"newestSubtask"
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtBanned)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDPatchSubtask(){
        String json = """
                {
                "name":"newestSubtask"
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", -1, testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidTaskIDPatchSubtask(){
        String json = """
                {
                "name":"newestSubtask"
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), -1, testDataLoader.subtaskWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidSubtaskIDPatchSubtask(){
        String json = """
                {
                "name":"newestSubtask"
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }


    /// deleteSubtask

    /// 200
    @Test
    void shouldDeleteSubtask(){
        var response = restClient.delete().uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamDelete.getID(), testDataLoader.taskMultiUserDelete.getID(), testDataLoader.subtaskDelete.getID()).header("Authorization", "Bearer " + testDataLoader.jwtDelete).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Subtask has been deleted", response.getBody().getMessage());
        assertThrows(NotFoundException.class, () -> testDataLoader.refreshSubtask(testDataLoader.subtaskDelete));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationDeleteSubtask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamDelete.getID(), testDataLoader.taskMultiUserDelete.getID(), testDataLoader.subtaskDelete.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesDeleteSubtask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.subtaskWrite.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtBanned)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDDeleteSubtask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", -1, testDataLoader.taskMultiUserDelete.getID(), testDataLoader.subtaskDelete.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtDelete)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidTaskIDDeleteSubtask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamDelete.getID(), -1, testDataLoader.subtaskDelete.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtDelete)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidSubtaskIDDeleteSubtask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{taskID}/subtasks/{ID}", testDataLoader.teamDelete.getID(), testDataLoader.taskMultiUserDelete.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtDelete)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
}
