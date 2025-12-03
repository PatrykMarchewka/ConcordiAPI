package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
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
public class TeamControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient.Builder builder;
    private final TestDataLoader testDataLoader;
    private RestClient restClient;

    @Autowired
    public TeamControllerTest(final RestClient.Builder builder, final TestDataLoader testDataLoader){
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


    /// myTeams

    /// 200
    @Test
    void shouldMyTeams(){
        //JSON Deserialization sees APIResponse<Set> so it doesn't add "type" field to TeamDTO even when annotated to do it
        //because of that it has issues with deserializing Set<TeamDTO> into objects
        //In this test responses are TeamAdminDTO, but for mixed DTOs it would need to be Map<String, Object> or something better
        var response = restClient.get().uri("/api/teams").header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<TeamAdminDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Information about all joined teams", response.getBody().getMessage());
        assertEquals(testDataLoader.userReadOwner.getTeamRoles().size(), response.getBody().getData().size());
        assertEquals(testDataLoader.userReadOwner.getTeamRoles().stream().map(TeamUserRole::getTeam).map(Team::getID).collect(Collectors.toUnmodifiableSet()), response.getBody().getData().stream().map(TeamAdminDTO::getID).collect(Collectors.toUnmodifiableSet()));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationMyTeams(){
        var response = restClient.get()
                .uri("/api/teams")
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// createTeam

    /// 201
    @Test
    void shouldCreateTeam(){
        String json = """
                {
                "name":"NewestTeam"
                }
                """;

        var response = restClient.post()
                .uri("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .retrieve()
                .toEntity(APIResponse.class);


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Team created with ID of "));
        assertNull(response.getBody().getData());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyCreateTeam(){
        String json = """
                {
                
                }
                """;

        var response = restClient.post()
                .uri("/api/teams")
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
    void shouldThrowForNoAuthenticationCreateTeam(){
        String json = """
                {
                "name":"NewestTeam"
                }
                """;

        var response = restClient.post()
                .uri("/api/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// getTeam

    /// 200
    @Test
    void shouldGetTeam(){
        var response = restClient.get().uri("/api/teams/{teamID}", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TeamAdminDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Information about the team", response.getBody().getMessage());
        assertEquals(testDataLoader.teamRead.getID(), response.getBody().getData().getID());
        assertEquals(testDataLoader.teamRead.getName(), response.getBody().getData().getName());
        assertEquals(testDataLoader.teamRead.getTeamTasks().size(), response.getBody().getData().getTasks().size());
        assertEquals(testDataLoader.teamRead.getUserRoles().size(), response.getBody().getData().getUsersByRole().values().stream().mapToInt(Set::size).sum());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetTeam(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}", testDataLoader.teamRead.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }


    /// 404
    @Test
    void shouldThrowForInvalidTeamIDGetTeam(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}", -1)
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


    /// putTeam

    /// 200
    @Test
    void shouldPutTeam(){
        String json = """
                {
                "name":"newer"
                }
                """;

        var response = restClient.put().uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TeamAdminDTO>>() {});
        var refreshedTeam = testDataLoader.refreshTeam(testDataLoader.teamWrite);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Team has been edited", response.getBody().getMessage());
        assertEquals(testDataLoader.teamWrite.getID(), response.getBody().getData().getID());
        assertEquals("newer", response.getBody().getData().getName());
        assertEquals(testDataLoader.teamWrite.getTeamTasks().size(), response.getBody().getData().getTasks().size());
        assertEquals(testDataLoader.teamWrite.getUserRoles().size(), response.getBody().getData().getUsersByRole().values().stream().mapToInt(Set::size).sum());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyPutTeam(){
        String json = """
                {
                
                }
                """;

        var response = restClient.put()
                .uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID())
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
    void shouldThrowForNoAuthenticationPutTeam(){
        String json = """
                {
                "name":"newer"
                }
                """;

        var response = restClient.put()
                .uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON).body(json)
                .exchange((req, res) -> ResponseEntity
                .status(res.getStatusCode())
                .headers(res.getHeaders())
                .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesPutTeam(){
        String json = """
                {
                "name":"newer"
                }
                """;

        var response = restClient.put()
                .uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON).body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
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
    void shouldThrowForInvalidTeamIDPutTeam(){
        String json = """
                {
                "name":"newer"
                }
                """;


        var response = restClient.put()
                .uri("/api/teams/{teamID}", -1)
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


    /// patchTeam

    /// 200
    @Test
    void shouldPatchTeam(){
        String json = """
                {
                "name":"newest"
                }
                """;

        var response = restClient.patch().uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TeamAdminDTO>>() {});
        var refreshedTeam = testDataLoader.refreshTeam(testDataLoader.teamWrite);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Team has been edited", response.getBody().getMessage());
        assertEquals("newest", response.getBody().getData().getName());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyPatchTeam(){
        String json = """
                {
                    "name": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut facilisis blandit est. Nam vel ultricies erat. Sed odio eros, auctor nec ante ac, commodo gravida eros. Fusce nisi elit, commodo ut orci quis, rhoncus ultricies nisi. Sed fringilla tortor magna..."
                }
                """;

        var response = restClient.patch()
                .uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID())
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
    void shouldThrowForNoAuthenticationPatchTeam(){
        String json = """
                {
                "name":"newer"
                }
                """;

        var response = restClient.patch()
                .uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON).body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesPatchTeam(){
        String json = """
                {
                "name":"newer"
                }
                """;

        var response = restClient.patch()
                .uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON).body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
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
    void shouldThrowForInvalidTeamIDPatchTeam(){
        String json = """
                {
                "name":"newer"
                }
                """;


        var response = restClient.patch()
                .uri("/api/teams/{teamID}", -1)
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


    /// disbandTeam

    /// 200
    @Test
    void shouldDeleteTeam(){
        var response = restClient.delete().uri("/api/teams/{teamID}", testDataLoader.teamDelete.getID()).header("Authorization", "Bearer " + testDataLoader.jwtDelete).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The team has been disbanded", response.getBody().getMessage());
        assertThrows(NotFoundException.class, () -> testDataLoader.refreshTeam(testDataLoader.teamDelete));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationDisbandTeam(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}", testDataLoader.teamDelete.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesDisbandTeam(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}", testDataLoader.teamDelete.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
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
    void shouldThrowForInvalidTeamIDDisbandTeam(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}", -1)
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
