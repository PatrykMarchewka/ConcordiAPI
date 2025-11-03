package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestLogin;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Exceptions.WrongCredentialsException;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithCredentials;
import com.patrykmarchewka.concordiapi.Passwords;
import com.patrykmarchewka.concordiapi.Teams.TeamRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.UserRole;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class UserServiceTest implements UserRequestBodyHelper, UserRequestLoginHelper, TeamRequestBodyHelper {

    private final UserService userService;
    private final TeamService teamService;

    private User user;

    public UserServiceTest(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
    }

    @BeforeEach
    void initialize(){
        UserRequestBody body = createUserRequestBody("JaneD");
        user = userService.createUser(body);
    }

    @AfterEach
    void cleanUp(){
        teamService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveUserCorrectlyBasic(){
        User found = userService.getUserByID(user.getID());

        assertNotNull(found);
        assertEquals(user.getID(), found.getID());
        assertEquals("Jane", found.getName());
        assertEquals("Doe", found.getLastName());
        assertEquals("JaneD", found.getLogin());
        assertNotEquals("d", found.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("d",found.getPassword()));
        assertThrows(LazyInitializationException.class, () -> found.getUserTasks().isEmpty());
        assertThrows(LazyInitializationException.class, () -> found.getTeamRoles().isEmpty());
        assertThrows(LazyInitializationException.class, () -> found.getTeams().isEmpty());
    }

    @Test
    void shouldThrowForNonUniqueLogin(){
        UserRequestBody body1 = createUserRequestBody("JaneD");

        assertThrows(ConflictException.class, () -> userService.createUser(body1));
    }

    @Test
    void shouldThrowForNonExistingUserID(){
        assertThrows(NotFoundException.class, () -> userService.getUserByID(0L));
    }

    @Test
    void shouldSaveAndRetrieveUserByLoginAndPassword(){
        UserRequestLogin loginBody = createUserRequestLogin(user.getLogin(), "d");

        UserWithCredentials found = userService.getUserWithCredentialsByLoginAndPassword(loginBody);

        assertNotNull(found);
        assertEquals(user.getID(), found.getID());
        assertEquals("Jane", found.getName());
        assertEquals("Doe", found.getLastName());
        assertEquals("JaneD", found.getLogin());
        assertNotEquals("d", found.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("d",found.getPassword()));
    }

    @Test
    void shouldThrowForIncorrectLoginAndPassword(){
        UserRequestLogin loginBody = createUserRequestLogin("JaneD", "doe");

        assertThrows(WrongCredentialsException.class, () -> userService.getUserWithCredentialsByLoginAndPassword(loginBody));
    }

    @Test
    void shouldReturnTrueForExistingUser(){
        boolean found = userService.checkIfUserExistsByLogin("JaneD");

        assertTrue(found);
    }

    @Test
    void shouldPutUser(){
        UserRequestBody newBody = createUserRequestBody("NotJane", "NotDoe", "NotJaneD", "Notd");

        User found = userService.getUserByID(user.getID());
        userService.putUser(found,newBody);
        User newFound = userService.getUserByID(user.getID());


        assertNotNull(newFound);
        assertEquals(user.getID(), newFound.getID());
        assertEquals("NotJane", newFound.getName());
        assertEquals("NotDoe", newFound.getLastName());
        assertEquals("NotJaneD", newFound.getLogin());
        assertNotEquals("Notd", newFound.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("Notd",newFound.getPassword()));
    }

    @Test
    void shouldPatchUser(){
        UserRequestBody newBody = createUserRequestBodyPATCH("NotJane");

        User found = userService.getUserByID(user.getID());
        userService.patchUser(found, newBody);
        User newFound = userService.getUserByID(user.getID());

        assertNotNull(newFound);
        assertEquals(user.getID(), newFound.getID());
        assertEquals("NotJane", found.getName());
        assertEquals("Doe", found.getLastName());
        assertEquals("JaneD", found.getLogin());
        assertNotEquals("d", found.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("d",found.getPassword()));
    }

    @Test
    void shouldPatchUserFull(){
        UserRequestBody newBody = createUserRequestBody("NotJane", "NotDoe", "NotJaneD", "Notd");

        User found = userService.getUserByID(user.getID());
        userService.patchUser(found,newBody);
        User newFound = userService.getUserByID(user.getID());

        assertNotNull(newFound);
        assertEquals(user.getID(), newFound.getID());
        assertEquals("NotJane", newFound.getName());
        assertEquals("NotDoe", newFound.getLastName());
        assertEquals("NotJaneD", newFound.getLogin());
        assertNotEquals("Notd", newFound.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("Notd",newFound.getPassword()));
    }

    @Test
    void shouldDeleteUser(){
        User found = userService.getUserByID(user.getID());
        userService.deleteUser(found);

        assertThrows(NotFoundException.class, () -> userService.getUserByID(user.getID()));
    }

    @Test
    void shouldReturnSetOfUserMemberDTO(){
        UserRequestBody otherBody = createUserRequestBody("John", "Doe", "JohnD", "dd");
        User user1 = userService.createUser(otherBody);
        Set<User> setUsers = Set.of(user,user1);

        Set<UserMemberDTO> set = userService.userMemberDTOSetProcess(setUsers);
        Set<String> expectedNames = Set.of("John","Jane");
        String expectedLastNames = "Doe";

        assertNotNull(set);
        assertFalse(set.isEmpty());
        assertEquals(2, set.size());
        assertEquals(expectedNames, set.stream().map(UserMemberDTO::getName).collect(Collectors.toUnmodifiableSet()));
        assertTrue(set.stream().map(UserMemberDTO::getLastName).allMatch(name -> name.equals(expectedLastNames)));
    }

    @Test
    void shouldReturnUserDTOWithParam(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("Team");
        Team team = teamService.createTeam(teamRequestBody, user);

        Set<UserMemberDTO> found = userService.userMemberDTOSetParam(UserRole.OWNER, UserRole.OWNER, team);

        assertEquals(1, found.size());
        assertTrue(found.contains(new UserMemberDTO(user)));
    }

    @Test
    void shouldReturnEmptySetUserDTOWithParam(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("Team");
        Team team = teamService.createTeam(teamRequestBody, user);

        Set<UserMemberDTO> found = userService.userMemberDTOSetParam(UserRole.OWNER, UserRole.MEMBER, team);

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnUserDTOWithoutParam(){
        TeamRequestBody teamRequestBody = createTeamRequestBody("Team");
        Team team = teamService.createTeam(teamRequestBody, user);

        Set<UserMemberDTO> found = userService.userMemberDTOSetNoParam(UserRole.OWNER, team);

        assertEquals(1, found.size());
        assertTrue(found.contains(new UserMemberDTO(user)));
    }

    @Test
    void shouldReturnUsersWithIDs(){
        UserRequestBody otherBody = createUserRequestBody("JohnD");
        User user1 = userService.createUser(otherBody);
        Set<Integer> setIDs = Set.of((int)user.getID(), (int)user1.getID());

        Set<User> foundUsers = userService.getUsersFromIDs(setIDs);

        assertNotNull(foundUsers);
        assertFalse(foundUsers.isEmpty());
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(user));
        assertTrue(foundUsers.contains(user1));
    }

    @Test
    void shouldReturnUserWithTeams(){
        User found = userService.getUserEntityWithTeams(user);

        assertTrue(found.getTeams().isEmpty());
        assertTrue(found.getTeamRoles().isEmpty());
    }

    @Test
    void shouldReturnUserWithUserTasks(){
        User found = userService.getUserEntityWithUserTasks(user);

        assertTrue(found.getUserTasks().isEmpty());
    }

    @Test
    void shouldSaveAndRetrieveUserCorrectlyFull(){
        User found = userService.getUserEntityFull(user);

        assertEquals(user.getID(), found.getID());
        assertEquals("Jane", found.getName());
        assertEquals("Doe", found.getLastName());
        assertEquals("JaneD", found.getLogin());
        assertNotEquals("d", found.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("d",found.getPassword()));
        assertTrue(found.getTeams().isEmpty());
        assertTrue(found.getTeamRoles().isEmpty());
        assertTrue(found.getUserTasks().isEmpty());
    }


}
