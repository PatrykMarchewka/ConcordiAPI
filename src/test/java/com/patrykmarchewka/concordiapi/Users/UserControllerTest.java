package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import com.patrykmarchewka.concordiapi.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.Set;
import java.util.stream.Collectors;

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




    /// getAllUsers

    /// 200
    @Test
    void shouldGetAllUsers(){
        var response = restClient.get().uri("/api/teams/{teamID}/users", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<UserMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All users in the team", response.getBody().getMessage());
        assertEquals(testDataLoader.teamRead.getUserRoles().size(), response.getBody().getData().size());
        assertEquals(Set.of(testDataLoader.userReadOwner.getID(), testDataLoader.userSecondOwner.getID(), testDataLoader.userAdmin.getID(), testDataLoader.userManager.getID(), testDataLoader.userMember.getID(), testDataLoader.userBanned.getID()), response.getBody().getData().stream().map(UserMemberDTO::getID).collect(Collectors.toUnmodifiableSet()));
    }

    @Test
    void shouldGetAllUsersWithParam(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/users?role={role}", testDataLoader.teamRead.getID(), UserRole.OWNER)
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<APIResponse<Set<UserMemberDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All users in the team with that role", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(Set.of(testDataLoader.userReadOwner.getID(), testDataLoader.userSecondOwner.getID()), response.getBody().getData().stream().map(UserMemberDTO::getID).collect(Collectors.toUnmodifiableSet()));
    }


    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetAllUsers(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/users", testDataLoader.teamRead.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesGetAllUsers(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/users", testDataLoader.teamRead.getID())
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
    void shouldThrowForInvalidTeamIDGetAllUsers(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/users", -1)
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

    /// getUser

    /// 200
    @Test
    void shouldGetUser(){
        var response = restClient.get().uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamRead.getID(), testDataLoader.userAdmin.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<UserMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User with the provided ID", response.getBody().getMessage());
        assertEquals(testDataLoader.userAdmin.getID(), response.getBody().getData().getID());
        assertEquals(testDataLoader.userAdmin.getName(), response.getBody().getData().getName());
        assertEquals(testDataLoader.userAdmin.getLastName(), response.getBody().getData().getLastName());
    }

    @Test
    void shouldGetUserMyself(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamRead.getID(), testDataLoader.userReadOwner.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<APIResponse<UserMemberDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User with the provided ID", response.getBody().getMessage());
        assertEquals(testDataLoader.userReadOwner.getID(), response.getBody().getData().getID());
        assertEquals(testDataLoader.userReadOwner.getName(), response.getBody().getData().getName());
        assertEquals(testDataLoader.userReadOwner.getLastName(), response.getBody().getData().getLastName());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetUser(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamRead.getID(), testDataLoader.userMember.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }


    /// 403
    @Test
    void shouldThrowForNoPrivilegesGetUser(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamRead.getID(), testDataLoader.userMember.getID())
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
    void shouldThrowForInvalidTeamIDGetUser(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/users/{ID}", -1, testDataLoader.userMember.getID())
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
    void shouldThrowForInvalidUserIDGetUser(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamRead.getID(), -1)
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

    /// changeUserRole

    /// 200
    @Test
    void shouldChangeUserRole(){
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/users/{ID}?newRole={role}", testDataLoader.teamWrite.getID(), testDataLoader.userManager.getID(), UserRole.MEMBER)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .retrieve()
                .toEntity(APIResponse.class);
        var refreshedUser = testDataLoader.refreshUser(testDataLoader.userManager);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Role changed", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertTrue(refreshedUser.getTeamRoles().stream().anyMatch(teamUserRole -> (teamUserRole.getTeam().equals(testDataLoader.teamWrite) && teamUserRole.getUser().equals(testDataLoader.userManager) && teamUserRole.getUserRole().isMember())));
    }

    /// 400
    @Test
    void shouldThrowForNoUserRoleChangeUserRole(){
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.userManager.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unexpected error!", response.getBody().getMessage());
        assertEquals("Required request parameter 'newRole' for method parameter type UserRole is not present", response.getBody().getData());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationChangeUserRole(){
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/users/{ID}?newRole={role}", testDataLoader.teamWrite.getID(), testDataLoader.userManager.getID(), UserRole.MEMBER)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }


    /// 403
    @Test
    void shouldThrowForNoPrivilegesChangeUserRole(){
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/users/{ID}?newRole={role}", testDataLoader.teamWrite.getID(), testDataLoader.userManager.getID(), UserRole.MEMBER)
                .header("Authorization", "Bearer " + testDataLoader.jwtManager)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForTooHighNewRoleChangeUserRole(){
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/users/{ID}?newRole={role}", testDataLoader.teamWrite.getID(), testDataLoader.userManager.getID(), UserRole.OWNER)
                .header("Authorization", "Bearer " + testDataLoader.jwtManager)
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
    void shouldThrowForInvalidTeamIDChangeUserRole(){
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/users/{ID}?newRole={role}", -1, testDataLoader.userManager.getID(), UserRole.MEMBER)
                .header("Authorization", "Bearer " + testDataLoader.jwtOwner)
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
    void shouldThrowForInvalidUserIDChangeUserRole(){
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/users/{ID}?newRole={role}", testDataLoader.teamWrite.getID(), -1, UserRole.MEMBER)
                .header("Authorization", "Bearer " + testDataLoader.jwtOwner)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// removeUser


    /// 200
    @Test
    void shouldRemoveUser(){
        var response = restClient.delete().uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.userAdmin.getID()).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(APIResponse.class);
        var refreshedTeam = testDataLoader.refreshTeam(testDataLoader.teamWrite);
        var refreshedUser = testDataLoader.refreshUser(testDataLoader.userAdmin);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User removed from team", response.getBody().getMessage());
        assertNull(response.getBody().getData());
        assertFalse(refreshedTeam.checkUser(testDataLoader.userAdmin.getID()));
        assertFalse(refreshedUser.getTeams().contains(testDataLoader.teamWrite));
    }

    @Test
    void shouldRemoveUserMyself(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.userSecondOwner.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtOwner)
                .retrieve()
                .toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User removed from team", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationRemoveUser(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.userWriteOwner.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesRemoveUser(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamWrite.getID(), testDataLoader.userWriteOwner.getID())
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
    void shouldThrowForInvalidTeamIDRemoveUser(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/users/{ID}", -1, testDataLoader.userWriteOwner.getID())
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
    void shouldThrowForInvalidUserIDRemoveUser(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/users/{ID}", testDataLoader.teamWrite.getID(), -1)
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

    /// leaveTeam

    /// 200
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

    /// 401
    @Test
    void shouldThrowForNoAuthenticationLeaveTeam(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/users/me", testDataLoader.teamWrite.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }


    /// 403
    @Test
    void shouldThrowForOnlyOwnerCantLeaveLeaveTeam(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/users/me", testDataLoader.teamDelete.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtDelete)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Can't leave team as the only owner, disband team instead or add new owners", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDLeaveTeam(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/users/me", -1)
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
