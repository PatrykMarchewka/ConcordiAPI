package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithInvitations;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithTasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRoles;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TeamServiceTest implements TeamRequestBodyHelper, UserRequestBodyHelper {

    private final TeamService teamService;
    private final UserService userService;

    private User user;
    private Team team;

    public TeamServiceTest(TeamService teamService, UserService userService) {
        this.teamService = teamService;
        this.userService = userService;
    }

    @BeforeEach
    void initialize() {
        TeamRequestBody body = createTeamRequestBody("TEST");
        UserRequestBody userRequestBody = createUserRequestBody("JaneD");
        user = userService.createUser(userRequestBody);
        team = teamService.createTeam(body, user);
    }

    @AfterEach
    void cleanUp(){
        teamService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveTeamCorrectly(){
        Team found = teamService.getTeamEntityByID(team.getID());

        assertEquals(team.getID(), found.getID());
        assertEquals("TEST", found.getName());
        assertThrows(LazyInitializationException.class, () -> found.getUserRoles().isEmpty());
        assertThrows(LazyInitializationException.class, () -> found.getTeammates().isEmpty());
        assertThrows(LazyInitializationException.class, () -> found.getTeamTasks().isEmpty());
        assertThrows(LazyInitializationException.class, () -> found.getInvitations().isEmpty());
    }

    @Test
    void shouldThrowForNonExistingTeamID(){
        assertThrows(NotFoundException.class, () -> teamService.getTeamEntityByID(0));
    }

    @Test
    void shouldPutTeam(){
        TeamRequestBody newBody = createTeamRequestBody("NEWTEST");

        Team found = teamService.getTeamEntityByID(team.getID());
        teamService.putTeam(found,newBody);
        Team newFound = teamService.getTeamEntityByID(team.getID());

        assertEquals(team.getID(), newFound.getID());
        assertEquals("NEWTEST", newFound.getName());
    }

    @Test
    void shouldPatchTeam(){
        TeamRequestBody newBody = createTeamRequestBody("NEWTEST");

        Team found = teamService.getTeamEntityByID(team.getID());
        teamService.patchTeam(found,newBody);
        Team newFound = teamService.getTeamEntityByID(team.getID());

        assertEquals(team.getID(), newFound.getID());
        assertEquals("NEWTEST", newFound.getName());
    }

    @Test
    void shouldDeleteTeam(){
        Team found = teamService.getTeamEntityByID(team.getID());
        teamService.deleteTeam(found);

        assertThrows(NotFoundException.class, () -> teamService.getTeamEntityByID(team.getID()));
    }

    @Test
    void shouldDeleteTeamByUserLeaving(){
        teamService.removeUser(team.getID(), user);

        assertThrows(NotFoundException.class, () -> teamService.getTeamEntityByID(team.getID()));
    }

    @Test
    void shouldRemoveUserFromTeam(){
        UserRequestBody userRequestBody1 = createUserRequestBody("JohnD");
        User user1 = userService.createUser(userRequestBody1);
        teamService.addUser(team, user1, UserRole.ADMIN);
        Map<User, UserRole> expected = new HashMap<>(Map.of(
                user, UserRole.OWNER,
                user1, UserRole.ADMIN
        ));

        Team found = teamService.getTeamWithUserRoles(team);
        Map<User, UserRole> actual = found.getUserRoles().stream().collect(Collectors.toMap(TeamUserRole::getUser, TeamUserRole::getUserRole));


        assertEquals(2, found.getTeammates().size());
        assertEquals(expected, actual);


        found = teamService.removeUser(found.getID(),user1);
        expected.remove(user1);
        actual = found.getUserRoles().stream().collect(Collectors.toMap(TeamUserRole::getUser, TeamUserRole::getUserRole));

        assertEquals(1, found.getUserRoles().size());
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeleteTeamByEveryoneLeaving(){
        UserRequestBody userRequestBody1 = createUserRequestBody("JohnD");
        User user1 = userService.createUser(userRequestBody1);
        teamService.addUser(team, user1, UserRole.ADMIN);

        Team found = teamService.getTeamWithUserRoles(team);

        teamService.removeAllUsers(found);

        assertThrows(NotFoundException.class, () -> teamService.getTeamEntityByID(found.getID()));
    }

    @Test
    void shouldReturnTeamWithUserRoles(){
        TeamWithUserRoles found = teamService.getTeamWithUserRoles(team.getID());

        assertEquals(1, found.getUserRoles().size());
        assertEquals(1, found.getTeammates().size());
    }

    @Test
    void shouldReturnTeamWithInvitations(){
        TeamWithInvitations found = teamService.getTeamWithInvitations(team.getID());

        assertTrue(found.getInvitations().isEmpty());
    }

    @Test
    void shouldReturnTeamWithTeamTasks(){
        TeamWithTasks found = teamService.getTeamWithTeamTasks(team.getID());

        assertTrue(found.getTeamTasks().isEmpty());
    }

    @Test
    void shouldGetTeamsDTO(){
        TeamRequestBody body1 = createTeamRequestBody("Nowy");
        Team team1 = teamService.createTeam(body1, user);

        Set<TeamDTO> found = teamService.getTeamsDTO(user);

        assertTrue(found.stream().anyMatch(t -> t.equalsTeam(team)));
        assertTrue(found.stream().anyMatch(t -> t.equalsTeam(team1)));
    }

    @Test
    void shouldCreateTeamDTO(){
        TeamDTO teamDTO = teamService.getTeamDTOByRole(user.getID(), team.getID());

        assertInstanceOf(TeamAdminDTO.class, teamDTO);
        assertTrue(teamDTO.equalsTeam(team));
    }
}
