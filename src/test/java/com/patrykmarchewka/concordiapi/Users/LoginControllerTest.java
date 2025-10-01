package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMeDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
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
import org.springframework.web.client.RestClient;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginControllerTest {

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
    void shouldCheckHealthStatus(){
        var response = restClient.get().uri("/health").retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Service is up!", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldLoginUserCorrectly(){
        String json = """
                {
                   "login": "string",
                   "password": "string"
                }
                """;
        var response = restClient.post().uri("/login").contentType(MediaType.APPLICATION_JSON).body(json).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForNonExistingUser(){
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

    @Test
    void shouldCreateNewUser(){
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

    @Test
    void shouldGetMeDTO(){
        var response = restClient.get().uri("/me").header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<UserMeDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Data related to my account", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsUser(testDataLoader.user1));
        assertEquals(2, response.getBody().getData().getTeams().size());
    }

    @Test
    void shouldPatchMe(){
        String json = """
                {
                "name":"patched"
                }
                """;
        User dummy = testDataLoader.user1;
        dummy.setName("patched");


        var response = restClient.patch().uri("/me").contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + testDataLoader.jwt1).body(json).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<UserMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Data changed!", response.getBody().getMessage());
        assertEquals(new UserMemberDTO(dummy), response.getBody().getData());
    }

    @Test
    void shouldRefreshToken(){
        var response = restClient.post().uri("/me/refresh").header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(APIResponse.class);

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

    @Test
    void shouldCheckInvitation(){
        var response = restClient.get().uri("/invitations/" + testDataLoader.invitation.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The provided invitation information", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsInvitation(testDataLoader.invitation));
    }

    @Test
    void shouldUseInvitation(){
        TeamMemberDTO old = new TeamMemberDTO(testDataLoader.team1, null);

        var response = restClient.post().uri("/invitations/" + testDataLoader.invitation.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwt2).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TeamMemberDTO>>() {});

        assertNotNull(response.getBody());
        assertEquals("Joined the following team:", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsTeam(testDataLoader.team1));
        assertEquals(old.getID(), response.getBody().getData().getID());
        assertEquals(old.getName(), response.getBody().getData().getName());
        assertEquals(old.getOwners().size(), response.getBody().getData().getOwners().size());
        assertEquals(old.getTeammateCount() + 1, response.getBody().getData().getTeammateCount());
        assertEquals(old.getTasks().size(), response.getBody().getData().getTasks().size());
    }
}
