package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
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
public class UserControllerTest {

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
    void shouldGetAllUsersInATeam(){
        var response = restClient.get().uri("/api/teams/{teamID}/users", testDataLoader.team1.getID()).header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<UserMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All users in the team", response.getBody().getMessage());
        assertEquals(testDataLoader.team1.getUserRoles().size(), response.getBody().getData().size());
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.user1)));
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userAdminA)));
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userAdminA2)));
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userManagerA)));
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userMemberA)));
    }

    @Test
    void shouldGetAllUsersInATeamWithRole(){
        var response = restClient.get().uri("/api/teams/{teamID}/users?role=OWNER", testDataLoader.team1.getID()).header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<UserMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All users in the team with that role", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.user1)));
    }

    @Test
    void shouldGetUserInTeam(){
        var response = restClient.get().uri("/api/teams/{teamID}/users/{ID}", testDataLoader.team1.getID(), testDataLoader.userAdminA.getID()).header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<UserMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User with the provided ID", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsUser(testDataLoader.userAdminA));
    }

    @Test
    void shouldRemoveUserFromTeam(){
        var response = restClient.delete().uri("/api/teams/{teamID}/users/{ID}", testDataLoader.team2.getID(), testDataLoader.userAdminB2.getID()).header("Authorization", "Bearer " + testDataLoader.jwtAdminB).retrieve().toEntity(APIResponse.class);
        testDataLoader.refreshTeam(testDataLoader.team2);
        testDataLoader.refreshUser(testDataLoader.userAdminB2);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User removed from team", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertFalse(testDataLoader.refreshedTeam.checkUser(testDataLoader.userAdminB2.getID()));
        assertFalse(testDataLoader.refreshedUser.getTeams().contains(testDataLoader.team2));
    }

    @Test
    void shouldLeaveTeam(){
        var response = restClient.delete().uri("/api/teams/{teamID}/users/me", testDataLoader.team2.getID()).header("Authorization", "Bearer " + testDataLoader.jwtMemberB).retrieve().toEntity(APIResponse.class);
        testDataLoader.refreshTeam(testDataLoader.team2);
        testDataLoader.refreshUser(testDataLoader.userMemberB);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Left the team", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertFalse(testDataLoader.refreshedTeam.checkUser(testDataLoader.userMemberB.getID()));
        assertFalse(testDataLoader.refreshedUser.getTeams().contains(testDataLoader.team2));
    }

    @Test
    void shouldChangeUserRole(){
        String json = """
                "MEMBER"
                """;
        var response = restClient.patch().uri("/api/teams/{teamID}/users/{ID}/role", testDataLoader.team2.getID(), testDataLoader.userManagerB.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtAdminB).retrieve().toEntity(APIResponse.class);
        testDataLoader.refreshUser(testDataLoader.userManagerB);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Role changed", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertTrue(testDataLoader.refreshedUser.getTeamRoles().stream().anyMatch(teamUserRole -> (teamUserRole.getTeam().equals(testDataLoader.team2) && teamUserRole.getUser().equals(testDataLoader.userManagerB) && teamUserRole.getUserRole().isMember())));

    }


}
