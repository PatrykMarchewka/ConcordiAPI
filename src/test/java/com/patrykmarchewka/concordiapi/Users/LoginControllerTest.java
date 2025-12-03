package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMeDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.JSONWebToken;
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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient.Builder builder;
    private final TestDataLoader testDataLoader;
    private RestClient restClient;

    @Autowired
    public LoginControllerTest(final RestClient.Builder builder, final TestDataLoader testDataLoader){
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



    /// healthCheck

    ///  200
    @Test
    void shouldHealthCheck(){
        var response = restClient.get().uri("/health").retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service is up!", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }


    /// login

    /// 200
    @Test
    void shouldLogin(){
        String json = """
                {
                   "login": "READ",
                   "password": "READ"
                }
                """;
        var response = restClient.post().uri("/login").contentType(MediaType.APPLICATION_JSON).body(json).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        try {
            JSONWebToken.VerifyJWT(response.getBody().getData().toString());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    ///400
    @Test
    void shouldThrowForInvalidRequestBodyLogin(){
        String json = """
                {
                    "login": "MEMBER"
                }
                """;

        var response = restClient.post()
                .uri("/login")
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
        assertEquals("Field cannot be empty", errors.get("password"));
    }

    ///401
    @Test
    void shouldThrowForNonExistingUserLogin(){
        String json = """
                {
                   "login": "fake",
                   "password": "user"
                }
                """;

        var response = restClient.post().uri("/login").contentType(MediaType.APPLICATION_JSON).body(json).retrieve().onStatus(HttpStatus.UNAUTHORIZED::equals, (req, res) -> {}).toEntity(APIResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid credentials", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }


    /// create

    /// 201
    @Test
    void shouldCreate(){
        String json = """
                {
                   "login": "new",
                   "password": "user",
                   "name":"John",
                   "lastName":"Doe"
                }
                """;

        var response = restClient.post().uri("/signup").contentType(MediaType.APPLICATION_JSON).body(json).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<UserMemberDTO>>() {});

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User created", response.getBody().getMessage());
        assertEquals("John", response.getBody().getData().getName());
        assertEquals("Doe", response.getBody().getData().getLastName());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyCreate(){
        String json = """
                {
                    "login": "wrong",
                    "password": "wrong",
                    "name": "wrong"
                }
                """;

        var response = restClient.post()
                .uri("/signup")
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
        assertEquals("Field cannot be empty", errors.get("lastName"));
    }

    /// 409
    @Test
    void shouldThrowForExistingLoginCreate(){
        String json = """
                {
                  "login": "MEMBER",
                  "password": "MEMBER",
                  "name": "MEMBER",
                  "lastName": "MEMBER"
                }
                """;

        var response  = restClient.post()
                .uri("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conflict occurred", response.getBody().getMessage());
        assertEquals("Login currently in use", response.getBody().getData());
    }

    /// getMyData

    /// 200
    @Test
    void shouldGetMyData(){
        var response = restClient.get().uri("/me").header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<UserMeDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Data related to my account", response.getBody().getMessage());
        assertEquals(testDataLoader.userReadOwner.getID(), response.getBody().getData().getID());
        assertEquals(testDataLoader.userReadOwner.getName(), response.getBody().getData().getName());
        assertEquals(testDataLoader.userReadOwner.getLastName(), response.getBody().getData().getLastName());
        assertEquals(testDataLoader.userReadOwner.getTeamRoles().size(), response.getBody().getData().getTeams().size());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetMyData(){
        var response = restClient.get()
                .uri("/me")
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// patchUser

    /// 200
    @Test
    void shouldPatchUser(){
        String json = """
                {
                "name":"patched"
                }
                """;
        var response = restClient.patch().uri("/me").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + testDataLoader.jwtWrite).body(json).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<UserMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Data changed!", response.getBody().getMessage());
        assertEquals("patched", response.getBody().getData().getName());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyPatchUser(){
        String json = """
                {
                    "login": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut facilisis blandit est. Nam vel ultricies erat. Sed odio eros, auctor nec ante ac, commodo gravida eros. Fusce nisi elit, commodo ut orci quis, rhoncus ultricies nisi. Sed fringilla tortor magna..."
                }
                """;
        var response = restClient.patch()
                .uri("/me")
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
        assertEquals("Value must be between 1 and 255 characters", errors.get("login"));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationPatchUser(){
        String json = """
                {
                "name":"MEMBER"
                }
                """;
        var response = restClient.patch()
                .uri("/me")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 409
    @Test
    void shouldThrowForExistingLoginPatchUser(){
        String json = """
                {
                  "login": "MEMBER",
                  "password": "MEMBER",
                  "name": "MEMBER",
                  "lastName": "MEMBER"
                }
                """;
        var response = restClient.patch()
                .uri("/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conflict occurred", response.getBody().getMessage());
        assertEquals("Login currently in use", response.getBody().getData());
    }

    /// refreshToken

    /// 200
    @Test
    void shouldRefreshToken(){
        var response = restClient.post().uri("/me/refresh").header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Your new token", response.getBody().getMessage());
        String jwt = response.getBody().getData().toString();
        try {
            assertTrue(JSONWebToken.VerifyJWT(jwt));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationRefreshToken(){
        var response = restClient.post()
                .uri("/me/refresh")
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }


    /// getInfoAboutInvitation

    /// 200
    @Test
    void shouldGetInfoAboutInvitation(){
        var response = restClient.get().uri("/invitations/" + testDataLoader.invitationRead.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The provided invitation information", response.getBody().getMessage());
        assertEquals(testDataLoader.invitationRead.getUUID(), response.getBody().getData().getUUID());
        assertEquals(testDataLoader.invitationRead.getInvitingTeam().getID(), response.getBody().getData().getTeam().getID());
        assertEquals(testDataLoader.invitationRead.getRole(), response.getBody().getData().getRole());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetInfoAboutInvitation(){
        var response = restClient.get()
                .uri("/invitations/" + testDataLoader.invitationRead.getUUID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 404
    @Test
    void shouldThrowForInvalidInvitationUUIDGetInfoAboutInvitation(){
        var response = restClient.get()
                .uri("/invitations/" + "WRONG-UUID")
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// joinTeam

    /// 200
    @Test
    void shouldJoinTeam(){
        var response = restClient.post().uri("/invitations/" + testDataLoader.invitationWrite.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwtNoTeam).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TeamMemberDTO>>() {});

        assertNotNull(response.getBody());
        assertEquals("Joined the following team:", response.getBody().getMessage());
        assertEquals(testDataLoader.teamWrite.getID(), response.getBody().getData().getID());
        assertEquals(testDataLoader.teamWrite.getName(), response.getBody().getData().getName());
        assertEquals(testDataLoader.teamWrite.getUserRoles().size() + 1, response.getBody().getData().getTeammateCount());
        assertEquals(testDataLoader.teamWrite.getUserRoles().stream().filter(ur -> ur.getUserRole().isOwner()).collect(Collectors.toUnmodifiableSet()).size() + 1, response.getBody().getData().getOwners().size());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationJoinTeam(){
        var response = restClient.post()
                .uri("/invitations/" + testDataLoader.invitationRead.getUUID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 404
    @Test
    void shouldThrowForInvalidInvitationUUIDJoinTeam(){
        var response = restClient.post()
                .uri("/invitations/" + "WRONG-UUID")
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 409
    @Test
    void shouldThrowForAlreadyInTeamJoinTeam(){
        var response = restClient.post()
                .uri("/invitations/" + testDataLoader.invitationWrite.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Conflict occurred", response.getBody().getMessage());
        assertEquals("User is already part of that team", response.getBody().getData());
    }
}
