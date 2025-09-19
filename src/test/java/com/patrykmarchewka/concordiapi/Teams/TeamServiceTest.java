package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserRepository;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TeamServiceTest implements TeamRequestBodyHelper, UserRequestBodyHelper {

    private final TeamService teamService;
    private final UserService userService;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamServiceTest(TeamService teamService, UserService userService, TeamRepository teamRepository, UserRepository userRepository) {
        this.teamService = teamService;
        this.userService = userService;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @AfterEach
    void cleanUp(){
        teamService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveTeamCorrectly(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        long id = teamService.createTeam(body, user).getId();

        Team found = teamService.getTeamByID(id);

        assertEquals(id, found.getId());
        assertEquals("TEST", found.getName());
        assertThrows(LazyInitializationException.class, () -> found.getUserRoles().isEmpty());
        assertThrows(LazyInitializationException.class, () -> found.getTeammates().isEmpty());
        assertThrows(LazyInitializationException.class, () -> found.getTeamTasks().isEmpty());
        assertThrows(LazyInitializationException.class, () -> found.getInvitations().isEmpty());
    }

    @Test
    void shouldThrowForNonExistingTeamID(){
        assertThrows(NotFoundException.class, () -> teamService.getTeamByID(0));
    }

    @Test
    void shouldPutTeam(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        TeamRequestBody newBody = createTeamRequestBody("NEWTEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        long id = teamService.createTeam(body, user).getId();

        Team found = teamService.getTeamByID(id);
        teamService.putTeam(found,newBody);
        Team newFound = teamService.getTeamByID(id);

        assertEquals(id, newFound.getId());
        assertEquals("NEWTEST", newFound.getName());
    }

    @Test
    void shouldPatchTeam(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        TeamRequestBody newBody = createTeamRequestBody("NEWTEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        long id = teamService.createTeam(body, user).getId();

        Team found = teamService.getTeamByID(id);
        teamService.patchTeam(found,newBody);
        Team newFound = teamService.getTeamByID(id);

        assertEquals(id, newFound.getId());
        assertEquals("NEWTEST", newFound.getName());
    }

    @Test
    void shouldDeleteTeam(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        long id = teamService.createTeam(body, user).getId();

        Team found = teamService.getTeamByID(id);
        teamService.deleteTeam(found);

        assertThrows(NotFoundException.class, () -> teamService.getTeamByID(id));
    }

    @Test
    void shouldDeleteTeamByUserLeaving(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        Team team = teamService.createTeam(body,user);

        teamService.removeUser(team, user);

        assertThrows(NotFoundException.class, () -> teamService.getTeamByID(team.getId()));
    }

    @Test
    void shouldRemoveUserFromTeam(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        UserRequestBody userRequestBody1 = createUserRequestBody("JohnD");
        User user = userService.createUser(userRequestBody);
        User user1 = userService.createUser(userRequestBody1);
        Team team = teamService.createTeam(body,user);
        team.addUserRole(user1, UserRole.ADMIN);
        teamService.saveTeam(team);
        Map<User, UserRole> expected = new HashMap<>(Map.of(
                user, UserRole.OWNER,
                user1, UserRole.ADMIN
        ));

        Team found = teamService.getTeamWithUserRoles(team);
        Map<User, UserRole> actual = found.getUserRoles().stream().collect(Collectors.toMap(TeamUserRole::getUser, TeamUserRole::getUserRole));


        assertEquals(2, found.getTeammates().size());
        assertEquals(expected, actual);


        found = teamService.removeUser(found,user1);
        expected.remove(user1);
        actual = found.getUserRoles().stream().collect(Collectors.toMap(TeamUserRole::getUser, TeamUserRole::getUserRole));

        assertEquals(1, found.getUserRoles().size());
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeleteTeamByEveryoneLeaving(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        UserRequestBody userRequestBody1 = createUserRequestBody("JohnD");
        User user = userService.createUser(userRequestBody);
        User user1 = userService.createUser(userRequestBody1);
        Team team = teamService.createTeam(body,user);
        team.addUserRole(user1,UserRole.ADMIN);
        teamService.saveTeam(team);

        Team found = teamService.getTeamWithUserRoles(team);

        teamService.removeAllUsers(found);

        assertThrows(NotFoundException.class, () -> teamService.getTeamByID(found.getId()));
    }

    @Test
    void shouldReturnTeamWithUserRoles(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        long id = teamService.createTeam(body, user).getId();

        Team found = teamService.getTeamByID(id);
        found = teamService.getTeamWithUserRoles(found);

        assertEquals(1, found.getUserRoles().size());
        assertEquals(1, found.getTeammates().size());
    }

    @Test
    void shouldReturnTeamWithInvitations(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        long id = teamService.createTeam(body, user).getId();

        Team found = teamService.getTeamByID(id);
        found = teamService.getTeamWithInvitations(found);

        assertTrue(found.getInvitations().isEmpty());
    }

    @Test
    void shouldReturnTeamWithTeamTasks(){
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        User user = userService.createUser(userRequestBody);
        long id = teamService.createTeam(body, user).getId();

        Team found = teamService.getTeamByID(id);
        found = teamService.getTeamWithTeamTasks(found);

        assertTrue(found.getTeamTasks().isEmpty());
    }
}
