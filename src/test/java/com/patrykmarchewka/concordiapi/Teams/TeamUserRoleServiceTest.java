package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRoleRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserRepository;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.junit.jupiter.api.AfterEach;
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
    private final TeamUserRoleRepository teamUserRoleRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamUserRoleServiceTest(TeamUserRoleService teamUserRoleService, TeamService teamService, UserService userService, TeamUserRoleRepository teamUserRoleRepository, TeamRepository teamRepository, UserRepository userRepository) {
        this.teamUserRoleService = teamUserRoleService;
        this.teamService = teamService;
        this.userService = userService;
        this.teamUserRoleRepository = teamUserRoleRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @AfterEach
    void cleanUp(){
        teamUserRoleRepository.deleteAll();
        teamUserRoleRepository.flush();
        teamRepository.deleteAll();
        teamRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void shouldSaveAndRetrieveTeamUserRoleCorrectly(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(body, user);

        TeamUserRole found = teamUserRoleService.getByUserAndTeam(user, team);

        assertNotNull(found.getID());
        assertEquals(UserRole.OWNER, found.getUserRole());
        assertEquals(user, found.getUser());
        assertEquals(team, found.getTeam());
    }

    @Test
    void shouldGetRoleFromUser(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(body, user);

        UserRole role = teamUserRoleService.getRole(user, team);

        assertEquals(UserRole.OWNER, role);
    }

    @Test
    void shouldSetRoleToUser(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(body, user);

        teamUserRoleService.setRole(user, team, UserRole.ADMIN);
        UserRole role = teamUserRoleService.getRole(user, team);

        assertEquals(UserRole.ADMIN, role);
    }

   @Test
   void shouldGetAllUsersByRoleAndTeam(){
       TeamRequestBody body = createTeamRequestBody("TEST");
       UserRequestBody userRequestBody = createUserRequestBody("JaneD");
       UserRequestBody userRequestBody1 = createUserRequestBody("JohnD");
       User user = userService.createUser(userRequestBody);
       User user1 = userService.createUser(userRequestBody1);
       Team team = teamService.createTeam(body,user);
       team.addUserRole(user1,UserRole.OWNER);
       teamService.saveTeam(team);

       Set<User> found = teamUserRoleService.getAllByTeamAndUserRole(team, UserRole.OWNER);

       assertNotNull(found);
       assertFalse(found.isEmpty());
       assertEquals(2, found.size());
       assertTrue(found.contains(user));
       assertTrue(found.contains(user1));
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
