package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
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

    @LocalServerPort
    private int port;

    private final RestClient.Builder builder;
    private final TestDataLoader testDataLoader;
    private RestClient restClient;

    @Autowired
    public UserControllerTest(final RestClient.Builder builder, final TestDataLoader testDataLoader) {
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

    @Test
    void shouldGetAllUsersInATeam(){
        var response = restClient.get().uri("/api/teams/{teamID}/users", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<UserMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All users in the team", response.getBody().getMessage());
        assertEquals(new TeamMemberDTO(testDataLoader.teamRead).getTeammateCount(), response.getBody().getData().size());
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userReadOwner)));
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userAdmin)));
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userManager)));
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userMember)));
    }

    @Test
    void shouldGetAllUsersInATeamWithRole(){
        var response = restClient.get().uri("/api/teams/{teamID}/users?role=OWNER", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<UserMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All users in the team with that role", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());
        assertTrue(response.getBody().getData().stream().anyMatch(userMemberDTO -> userMemberDTO.equalsUser(testDataLoader.userReadOwner)));
    }

    @Test
    void shouldGetUserInTeam(){
        var response = restClient.get().uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamRead.getID(), testDataLoader.userAdmin.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<UserMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User with the provided ID", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsUser(testDataLoader.userAdmin));
    }

    @Test
    void shouldRemoveUserFromTeam(){
        var response = restClient.delete().uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.userAdmin.getID()).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(APIResponse.class);
        var refreshedTeam = testDataLoader.refreshTeam(testDataLoader.teamWrite);
        var refreshedUser = testDataLoader.refreshUser(testDataLoader.userAdmin);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User removed from team", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertFalse(refreshedTeam.checkUser(testDataLoader.userAdmin.getID()));
        assertFalse(refreshedUser.getTeams().contains(testDataLoader.teamWrite));

        //TODO: Fix and change to:
        //TeamWithUserRolesAndTasks team  = teamService.getTeamWithUserRolesAndTasksByID(testDataLoader.teamWrite.getID());
        //assertFalse(new TeamManagerDTO(team).getUsersByRole().containsValue(new UserMemberDTO(testDataLoader.userAdmin)));
        //assertFalse(refreshedUser.getTeams().contains(testDataLoader.teamWrite));
        //assertFalse(new UserMeDTO(refreshedUser).getTeams().contains(new TeamMemberDTO(refreshedTeam, refreshedUser)));
    }

    @Test
    void shouldLeaveTeam(){
        var response = restClient.delete().uri("/api/teams/{teamID}/users/me", testDataLoader.teamWrite.getID()).header("Authorization", "Bearer " + testDataLoader.jwtMember).retrieve().toEntity(APIResponse.class);
        var refreshedTeam = testDataLoader.refreshTeam(testDataLoader.teamWrite);
        var refreshedUser = testDataLoader.refreshUser(testDataLoader.userMember);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Left the team", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertFalse(refreshedTeam.checkUser(testDataLoader.userMember.getID()));
        assertFalse(refreshedUser.getTeams().contains(testDataLoader.teamWrite));
    }

    @Test
    void shouldChangeUserRole(){
        String json = """
                "MEMBER"
                """;
        var response = restClient.patch().uri("/api/teams/{teamID}/users/{ID}/role", testDataLoader.teamWrite.getID(), testDataLoader.userManager.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(APIResponse.class);
        var refreshedUser = testDataLoader.refreshUser(testDataLoader.userManager);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Role changed", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertTrue(refreshedUser.getTeamRoles().stream().anyMatch(teamUserRole -> (teamUserRole.getTeam().equals(testDataLoader.teamWrite) && teamUserRole.getUser().equals(testDataLoader.userManager) && teamUserRole.getUserRole().isMember())));
    }


}
