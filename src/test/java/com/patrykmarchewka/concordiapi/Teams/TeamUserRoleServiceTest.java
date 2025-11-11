package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TeamUserRoleServiceTest implements TeamRequestBodyHelper, UserRequestBodyHelper {

    private final TeamUserRoleService teamUserRoleService;
    private final TeamService teamService;
    private final UserService userService;

    private User user;
    private Team team;

    public TeamUserRoleServiceTest(TeamUserRoleService teamUserRoleService, TeamService teamService, UserService userService) {
        this.teamUserRoleService = teamUserRoleService;
        this.teamService = teamService;
        this.userService = userService;
    }

    @BeforeEach
    void initialize(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        user = userService.createUser(userRequestBody);
        team = teamService.createTeam(body, user);
    }

    @AfterEach
    void cleanUp(){
        teamUserRoleService.deleteAll();
        teamService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveTeamUserRoleCorrectly(){
        TeamUserRole found = teamUserRoleService.getByUserAndTeam(user.getID(), team.getID());

        assertNotNull(found.getID());
        assertEquals(UserRole.OWNER, found.getUserRole());
        assertEquals(user, found.getUser());
        assertEquals(team, found.getTeam());
    }

    @Test
    void shouldGetRoleFromUser(){
        UserRole role = teamUserRoleService.getRole(user.getID(), team.getID());

        assertEquals(UserRole.OWNER, role);
    }

    @Test
    void shouldSetRoleToUser(){
        teamUserRoleService.setRole(user.getID(), team.getID(), UserRole.ADMIN);
        UserRole role = teamUserRoleService.getRole(user.getID(), team.getID());

        assertEquals(UserRole.ADMIN, role);
    }

   @Test
   void shouldGetAllUsersByRoleAndTeam(){
       UserRequestBody userRequestBody1 = createUserRequestBody("JohnD");
       User user1 = userService.createUser(userRequestBody1);
       teamService.addUser(team, user1, UserRole.OWNER);

       Set<TeamUserRole> found = teamUserRoleService.getAllByTeamAndUserRole(team.getID(), UserRole.OWNER);

       assertNotNull(found);
       assertFalse(found.isEmpty());
       assertEquals(2, found.size());
       assertTrue(found.stream().anyMatch(teamUserRole -> teamUserRole.getUser().equals(user)));
       assertTrue(found.stream().anyMatch(teamUserRole -> teamUserRole.getUser().equals(user1)));
   }

   @Test
    void shouldCheckRolesInTeam(){
       assertTrue(teamUserRoleService.checkRoles(UserRole.OWNER, UserRole.OWNER));
       assertTrue(teamUserRoleService.checkRoles(UserRole.OWNER, UserRole.ADMIN));
       assertTrue(teamUserRoleService.checkRoles(UserRole.OWNER, UserRole.MANAGER));
       assertTrue(teamUserRoleService.checkRoles(UserRole.OWNER, UserRole.MEMBER));

       assertFalse(teamUserRoleService.checkRoles(UserRole.ADMIN, UserRole.OWNER));
       assertTrue(teamUserRoleService.checkRoles(UserRole.ADMIN, UserRole.ADMIN));
       assertTrue(teamUserRoleService.checkRoles(UserRole.ADMIN, UserRole.MANAGER));
       assertTrue(teamUserRoleService.checkRoles(UserRole.ADMIN, UserRole.MEMBER));

       assertFalse(teamUserRoleService.checkRoles(UserRole.MANAGER, UserRole.OWNER));
       assertFalse(teamUserRoleService.checkRoles(UserRole.MANAGER, UserRole.ADMIN));
       assertTrue(teamUserRoleService.checkRoles(UserRole.MANAGER, UserRole.MANAGER));
       assertTrue(teamUserRoleService.checkRoles(UserRole.MANAGER, UserRole.MEMBER));

       assertFalse(teamUserRoleService.checkRoles(UserRole.MEMBER, UserRole.OWNER));
       assertFalse(teamUserRoleService.checkRoles(UserRole.MEMBER, UserRole.ADMIN));
       assertFalse(teamUserRoleService.checkRoles(UserRole.MEMBER, UserRole.MANAGER));
       assertTrue(teamUserRoleService.checkRoles(UserRole.MEMBER, UserRole.MEMBER));
   }
}
