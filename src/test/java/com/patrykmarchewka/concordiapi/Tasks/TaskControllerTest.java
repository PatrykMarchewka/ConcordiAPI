package com.patrykmarchewka.concordiapi.Tasks;

import com.patrykmarchewka.concordiapi.APIResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient.Builder builder;
    private final TestDataLoader testDataLoader;
    private RestClient restClient;

    @Autowired
    public TaskControllerTest(final RestClient.Builder builder, final TestDataLoader testDataLoader, final TaskService taskService) {
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



    /// getAllTasks

    /// 200
    @Test
    void shouldGetAllTasks(){
        var response = restClient.get().uri("/api/teams/{teamID}/tasks", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<TaskMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All tasks available", response.getBody().getMessage());
        assertEquals(4, response.getBody().getData().size());
    }

    @Test
    void shouldGetAllTasksWithParam(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks?inactiveDays=5", testDataLoader.teamRead.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<APIResponse<Set<TaskMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All inactive tasks available", response.getBody().getMessage());
        assertEquals(0, response.getBody().getData().size());
    }

    /// 400
    @Test
    void shouldThrowForInvalidInactiveDaysGetAllTasks(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks?inactiveDays=0", testDataLoader.teamRead.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Number of days cannot be zero or negative!", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetAllTasks(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks", testDataLoader.teamRead.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesGetAllTasks(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks", testDataLoader.teamRead.getID())
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
    void shouldThrowForInvalidTeamIDGetAllTasks(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks", -1)
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

    /// createTask

    /// 201
    @Test
    void shouldCreateTask(){
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

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyCreateTask(){
        String json = """
                {
                "description":"new description",
                "taskStatus":"NEW"
                }
                """;
        var response = restClient.post().uri("/api/teams/{teamID}/tasks", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
    void shouldThrowForNoAuthenticationCreateTask(){
        String json = """
                {
                "name":"new task",
                "description":"new description",
                "taskStatus":"NEW"
                }
                """;

        var response = restClient.post().uri("/api/teams/{teamID}/tasks", testDataLoader.teamWrite.getID())
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
    void shouldThrowForNoPrivilegesCreateTask(){
        String json = """
                {
                "name":"new task",
                "description":"new description",
                "taskStatus":"NEW"
                }
                """;

        var response = restClient.post().uri("/api/teams/{teamID}/tasks", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
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
    void shouldThrowForInvalidTeamIDCreateTask(){
        String json = """
                {
                "name":"new task",
                "description":"new description",
                "taskStatus":"NEW"
                }
                """;

        var response = restClient.post().uri("/api/teams/{teamID}/tasks", -1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// getAllTasksAssignedToMe

    /// 200
    @Test
    void shouldGetAllTasksAssignedToMe(){
        var response = restClient.get().uri("/api/teams/{teamID}/tasks/me", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<TaskMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tasks assigned to me", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetAllTasksAssignedToMe(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/me", testDataLoader.teamRead.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesGetAllTasksAssignedToMe(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/me", testDataLoader.teamRead.getID())
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
    void shouldThrowForInvalidTeamIDGetAllTasksAssignedToMe(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/me", -1)
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

    /// getTaskByID

    /// 200
    @Test
    void shouldGetTaskByID(){
        var response = restClient.get().uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamRead.getID(), testDataLoader.taskNoUsersRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task details", response.getBody().getMessage());
        assertEquals(new TaskMemberDTO(testDataLoader.taskNoUsersRead), response.getBody().getData());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetTaskByID(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamRead.getID(), testDataLoader.taskNoUsersRead.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesGetTaskByID(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamRead.getID(), testDataLoader.taskNoUsersRead.getID())
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
    void shouldThrowForInvalidTeamIDGetTaskByID(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{ID}", -1, testDataLoader.taskNoUsersRead.getID())
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
    void shouldThrowForInvalidTaskIDGetTaskByID(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamRead.getID(), -1)
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

    /// putTask

    /// 200
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
        var response = restClient.put().uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task fully changed", response.getBody().getMessage());
        assertEquals(new TaskMemberDTO(testDataLoader.refreshTask(testDataLoader.taskMultiUserWrite)), response.getBody().getData());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyPutTask(){
        String json = String.format("""
                {
                "description":"newest description",
                "users": [%d , %d],
                "taskStatus":"INPROGRESS"
                }
                """, testDataLoader.userWriteOwner.getID(), testDataLoader.userManager.getID());

        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
    void shouldThrowForNoAuthenticationPutTask(){
        String json = String.format("""
                {
                "name":"newest task",
                "description":"newest description",
                "users": [%d , %d],
                "taskStatus":"INPROGRESS"
                }
                """, testDataLoader.userWriteOwner.getID(), testDataLoader.userManager.getID());

        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
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
    void shouldThrowForNoPrivilegesPutTask(){
        String json = String.format("""
                {
                "name":"newest task",
                "description":"newest description",
                "users": [%d , %d],
                "taskStatus":"INPROGRESS"
                }
                """, testDataLoader.userWriteOwner.getID(), testDataLoader.userManager.getID());

        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
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
    void shouldThrowForInvalidTeamIDPuTask(){
        String json = String.format("""
                {
                "name":"newest task",
                "description":"newest description",
                "users": [%d , %d],
                "taskStatus":"INPROGRESS"
                }
                """, testDataLoader.userWriteOwner.getID(), testDataLoader.userManager.getID());

        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{ID}", -1, testDataLoader.taskMultiUserWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
    void shouldThrowForInvalidTaskIDPutTask(){
        String json = String.format("""
                {
                "name":"newest task",
                "description":"newest description",
                "users": [%d , %d],
                "taskStatus":"INPROGRESS"
                }
                """, testDataLoader.userWriteOwner.getID(), testDataLoader.userManager.getID());

        var response = restClient.put()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(),-1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// patchTask

    /// 200
    @Test
    void shouldPatchTask(){
        String json = """
                {
                "name": "newer task"
                }
                """;
        var response = restClient.patch().uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task updated", response.getBody().getMessage());
        assertEquals("newer task", response.getBody().getData().getName());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyPatchTask(){
        String json = """
                {
                    "name": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut facilisis blandit est. Nam vel ultricies erat. Sed odio eros, auctor nec ante ac, commodo gravida eros. Fusce nisi elit, commodo ut orci quis, rhoncus ultricies nisi. Sed fringilla tortor magna..."
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
    void shouldThrowForNoAuthenticationPatchTask(){
        String json = """
                {
                "name": "newer task"
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
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
    void shouldThrowForNoPrivilegesPatchTask(){
        String json = """
                {
                "name": "newer task"
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
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
    void shouldThrowForInvalidTeamIDPatchTask(){
        String json = """
                {
                "name": "newer task"
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{ID}", -1, testDataLoader.taskMultiUserWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
    void shouldThrowForInvalidTaskIDPatchTask(){
        String json = """
                {
                "name": "newer task"
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), -1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// deleteTask

    /// 200
    @Test
    void shouldDeleteTask(){
        var response = restClient.delete().uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamDelete.getID(), testDataLoader.taskMultiUserDelete.getID()).header("Authorization", "Bearer " + testDataLoader.jwtDelete).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task has been deleted", response.getBody().getMessage());
        assertThrows(NotFoundException.class, () -> testDataLoader.refreshTask(testDataLoader.taskMultiUserDelete));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationDeleteTask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamDelete.getID(), testDataLoader.taskMultiUserDelete.getID())
                .exchange((req, res) -> ResponseEntity
                .status(res.getStatusCode())
                .headers(res.getHeaders())
                .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesDeleteTask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID())
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
    void shouldThrowForInvalidTeamIDDeleteTask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{ID}", -1, testDataLoader.taskMultiUserDelete.getID())
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
    void shouldThrowForInvalidTaskIDDeleteTask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{ID}", testDataLoader.teamDelete.getID(), -1)
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

    /// addOneUserToTask

    /// 200
    @Test
    void shouldAddOneUserToTask(){
        var response = restClient.post().uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userAdmin.getID()).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User added to task", response.getBody().getMessage());
        assertTrue(response.getBody().getData().getUsers().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userAdmin)));
    }

    /// 400
    @Test
    void shouldThrowForAddingUserInTaskAddOneUserToTask(){
        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userWriteOwner.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(String.format("User with ID - %d was already attached to the task", testDataLoader.userWriteOwner.getID()), response.getBody().getMessage());
        assertNull(response.getBody().getData());

    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationAddOneUserToTask(){
        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userAdmin.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesAddOneUserToTask(){
        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userAdmin.getID())
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
    void shouldThrowForInvalidTeamIDAddOneUserToTask(){
        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", -1, testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userAdmin.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
    void shouldThrowForInvalidTaskIDAddOneUserToTask(){
        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), -1, testDataLoader.userAdmin.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
    void shouldThrowForInvalidUserIDAddOneUserToTask(){
        var response = restClient.post()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// deleteOneUserFromTask

    /// 200
    @Test
    void shouldDeleteOneUserFromTask(){
        var response = restClient.delete().uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskOwnerUserWrite.getID(), testDataLoader.userWriteOwner.getID()).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TaskMemberDTO>>() {});


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User removed from task", response.getBody().getMessage());
        assertFalse(response.getBody().getData().getUsers().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userWriteOwner)));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationDeleteOneUserFromTask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userWriteOwner.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesDeleteOneUserFromTask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userWriteOwner.getID())
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
    void shouldThrowForInvalidTeamIDDeleteOneUserFromTask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", -1, testDataLoader.taskMultiUserWrite.getID(), testDataLoader.userWriteOwner.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
    void shouldThrowForInvalidTaskIDDeleteOneUserFromTask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), -1, testDataLoader.userWriteOwner.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
    void shouldThrowForInvalidUserIDDeleteOneUserFromTask(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/tasks/{ID}/users/{userID}", testDataLoader.teamWrite.getID(), testDataLoader.taskMultiUserWrite.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
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
