package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestLogin;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserRepository;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Passwords;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
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
public class UserServiceTest implements UserRequestBodyHelper, UserRequestLoginHelper{

    private final UserService userService;
    private final UserRepository userRepository;

    public UserServiceTest(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @AfterEach
    void cleanUp(){
        userRepository.deleteAll();
        userRepository.flush();
    }


    @Test
    void shouldSaveAndRetrieveUserCorrectlyBasic(){
        UserRequestBody body = createUserRequestBody("JaneD");
        long id = userService.createUser(body).getID();

        User found = userService.getUserByID(id);

        assertNotNull(found);
        assertEquals(id, found.getID());
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
    void shouldThrowForNonExistingUserID(){
        assertThrows(NotFoundException.class, () -> userService.getUserByID(0L));
    }

    @Test
    void shouldSaveAndRetrieveUserByLoginAndPassword(){
        UserRequestBody body = createUserRequestBody("JaneD");
        long id = userService.createUser(body).getID();
        UserRequestLogin loginBody = createUserRequestLogin(body.getLogin(), body.getPassword());

        User found = userService.getUserByLoginAndPassword(loginBody);

        assertNotNull(found);
        assertEquals(id, found.getID());
        assertEquals("Jane", found.getName());
        assertEquals("Doe", found.getLastName());
        assertEquals("JaneD", found.getLogin());
        assertNotEquals("d", found.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("d",found.getPassword()));
    }

    @Test
    void shouldReturnTrueForExistingUser(){
        UserRequestBody body = createUserRequestBody("JaneD");
        userService.createUser(body);

        boolean found = userService.checkIfUserExistsByLogin("JaneD");

        assertTrue(found);
    }

    @Test
    void shouldPutUser(){
        UserRequestBody body = createUserRequestBody("JaneD");
        UserRequestBody newBody = createUserRequestBody("NotJane", "NotDoe", "NotJaneD", "Notd");
        long id = userService.createUser(body).getID();

        User found = userService.getUserByID(id);
        userService.putUser(found,newBody);
        User newFound = userService.getUserByID(id);


        assertNotNull(newFound);
        assertEquals(id, newFound.getID());
        assertEquals("NotJane", newFound.getName());
        assertEquals("NotDoe", newFound.getLastName());
        assertEquals("NotJaneD", newFound.getLogin());
        assertNotEquals("Notd", newFound.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("Notd",newFound.getPassword()));
    }

    @Test
    void shouldPatchUser(){
        UserRequestBody body = createUserRequestBody("JaneD");
        UserRequestBody newBody = createUserRequestBody("NotJane", "NotDoe", "NotJaneD", "Notd");
        long id = userService.createUser(body).getID();

        User found = userService.getUserByID(id);
        userService.patchUser(found, newBody);
        User newFound = userService.getUserByID(id);

        assertNotNull(newFound);
        assertEquals(id, newFound.getID());
        assertEquals("NotJane", newFound.getName());
        assertEquals("NotDoe", newFound.getLastName());
        assertEquals("NotJaneD", newFound.getLogin());
        assertNotEquals("Notd", newFound.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("Notd",newFound.getPassword()));
    }

    @Test
    void shouldDeleteUser(){
        UserRequestBody body = createUserRequestBody("JaneD");
        long id = userService.createUser(body).getID();

        User found = userService.getUserByID(id);
        userService.deleteUser(found);

        assertThrows(NotFoundException.class, () -> userService.getUserByID(id));
    }

    @Test
    void shouldReturnSetOfUserMemberDTO(){
        UserRequestBody body = createUserRequestBody("JaneD");
        UserRequestBody otherBody = createUserRequestBody("John", "Doe", "JohnD", "dd");
        User user = userService.createUser(body);
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
    void shouldReturnUsersWithIDs(){
        UserRequestBody body = createUserRequestBody("JaneD");
        UserRequestBody otherBody = createUserRequestBody("JohnD");
        User user = userService.createUser(body);
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
        UserRequestBody body = createUserRequestBody("JaneD");
        User user = userService.createUser(body);

        User found = userService.getUserWithTeams(user);

        assertTrue(found.getTeams().isEmpty());
        assertTrue(found.getTeamRoles().isEmpty());
    }

    @Test
    void shouldReturnUserWithUserTasks(){
        UserRequestBody body = createUserRequestBody("JaneD");
        User user = userService.createUser(body);

        User found = userService.getUserWithUserTasks(user);

        assertTrue(found.getUserTasks().isEmpty());
    }

    @Test
    void shouldSaveAndRetrieveUserCorrectlyFull(){
        UserRequestBody body = createUserRequestBody("JaneD");
        User user = userService.createUser(body);

        User found = userService.getUserFull(user);

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
