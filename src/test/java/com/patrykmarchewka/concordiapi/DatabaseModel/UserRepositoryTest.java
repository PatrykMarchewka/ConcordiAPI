package com.patrykmarchewka.concordiapi.DatabaseModel;


import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithCredentials;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithTeamRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithUserTasks;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTest{

    private final UserRepository userRepository;
    private final TestDataLoader testDataLoader;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository, TestDataLoader testDataLoader) {
        this.userRepository = userRepository;
        this.testDataLoader = testDataLoader;
    }

    @BeforeAll
    void initialize(){
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }


    /// existsByLogin

    @Test
    void shouldReturnTrueForExistsByLogin() {
        assertTrue(userRepository.existsByLogin(testDataLoader.userMember.getLogin()));
    }

    @Test
    void shouldReturnFalseForExistsByLogin(){
        assertFalse(userRepository.existsByLogin("TEST"));
    }

    /// findUserByID

    @Test
    void shouldFindUserByID(){
        Optional<UserIdentity> user = userRepository.findUserByID(testDataLoader.userMember.getID());

        assertTrue(user.isPresent());
        assertEquals(testDataLoader.userMember.getID(), user.get().getID());
        assertEquals(testDataLoader.userMember.getName(), user.get().getName());
        assertEquals(testDataLoader.userMember.getLastName(), user.get().getLastName());
    }

    @Test
    void shouldReturnEmptyUserIdentityForNonExistingID(){
        Optional<UserIdentity> user = userRepository.findUserByID(999L);

        assertFalse(user.isPresent());
    }

    @Test
    void shouldReturnEmptyUserIdentityForInvalidID(){
        Optional<UserIdentity> user = userRepository.findUserByID(-1);

        assertFalse(user.isPresent());
    }

    /// findUserWithCredentialsByLogin

    @Test
    void shouldFindUserWithCredentialsByLogin(){
        Optional<UserWithCredentials> user = userRepository.findUserWithCredentialsByLogin(testDataLoader.userMember.getLogin());

        assertTrue(user.isPresent());
        assertEquals(testDataLoader.userMember.getID(), user.get().getID());
        assertEquals(testDataLoader.userMember.getName(), user.get().getName());
        assertEquals(testDataLoader.userMember.getLastName(), user.get().getLastName());
        assertEquals(testDataLoader.userMember.getLogin(), user.get().getLogin());
        assertEquals(testDataLoader.userMember.getPassword(), user.get().getPassword());
    }

    @Test
    void shouldReturnEmptyUserWithCredentialsForNonExistingLogin(){
        Optional<UserWithCredentials> user = userRepository.findUserWithCredentialsByLogin("TEST");

        assertFalse(user.isPresent());
    }

    @Test
    void shouldReturnEmptyUserWithCredentialsForEmptyLogin(){
        Optional<UserWithCredentials> user = userRepository.findUserWithCredentialsByLogin("");

        assertFalse(user.isPresent());
    }

    @Test
    void shouldReturnEmptyUserWithCredentialsForNullLogin(){
        Optional<UserWithCredentials> user = userRepository.findUserWithCredentialsByLogin(null);

        assertFalse(user.isPresent());
    }

    @Test
    void shouldReturnEmptyUserWithCredentialsForCaseSensitivity(){
        Optional<UserWithCredentials> user = userRepository.findUserWithCredentialsByLogin(testDataLoader.userMember.getLogin().toLowerCase());

        assertFalse(user.isPresent());
    }

    /// findUserWithTeamRolesByID

    @Test
    void shouldReturnUserWithTeamRolesByID(){
        Optional<UserWithTeamRoles> user = userRepository.findUserWithTeamRolesAndTeamsByID(testDataLoader.userMember.getID());

        assertTrue(user.isPresent());
        assertEquals(testDataLoader.userMember.getID(), user.get().getID());
        assertEquals(testDataLoader.userMember.getName(), user.get().getName());
        assertEquals(testDataLoader.userMember.getLastName(), user.get().getLastName());
        assertEquals(testDataLoader.userMember.getTeamRoles(), user.get().getTeamRoles());
    }

    @Test
    void shouldReturnEmptyUserWithTeamRolesForNonExistingID(){
        Optional<UserWithTeamRoles> user = userRepository.findUserWithTeamRolesAndTeamsByID(999L);

        assertFalse(user.isPresent());
    }

    @Test
    void shouldReturnEmptyUserWithTeamRolesForInvalidID(){
        Optional<UserWithTeamRoles> user = userRepository.findUserWithTeamRolesAndTeamsByID(-1);

        assertFalse(user.isPresent());
    }

    /// findUserWithTasksByID

    @Test
    void shouldReturnUserWithTasksByID(){
        Optional<UserWithUserTasks> user = userRepository.findUserWithUserTasksByID(testDataLoader.userMember.getID());

        assertTrue(user.isPresent());
        assertEquals(testDataLoader.userMember.getID(), user.get().getID());
        assertEquals(testDataLoader.userMember.getName(), user.get().getName());
        assertEquals(testDataLoader.userMember.getLastName(), user.get().getLastName());
        assertEquals(testDataLoader.userMember.getUserTasks(), user.get().getUserTasks());
    }

    @Test
    void shouldReturnEmptyUserWithTasksForNonExistingID(){
        Optional<UserWithUserTasks> user = userRepository.findUserWithUserTasksByID(999L);

        assertFalse(user.isPresent());
    }

    @Test
    void shouldReturnEmptyUserWithTasksForInvalidID(){
        Optional<UserWithUserTasks> user = userRepository.findUserWithUserTasksByID(-1);

        assertFalse(user.isPresent());
    }

    /// findUserFullByID

    @Test
    void shouldReturnUserFullByID(){
        Optional<UserFull> user = userRepository.findUserFullByID(testDataLoader.userMember.getID());

        assertTrue(user.isPresent());
        assertEquals(testDataLoader.userMember.getID(), user.get().getID());
        assertEquals(testDataLoader.userMember.getName(), user.get().getName());
        assertEquals(testDataLoader.userMember.getLastName(), user.get().getLastName());
        assertEquals(testDataLoader.userMember.getLogin(), user.get().getLogin());
        assertEquals(testDataLoader.userMember.getPassword(), user.get().getPassword());
        assertEquals(testDataLoader.userMember.getTeamRoles(), user.get().getTeamRoles());
        assertEquals(testDataLoader.userMember.getUserTasks(), user.get().getUserTasks());
    }

    @Test
    void shouldReturnEmptyUserFullForNonExistingID(){
        Optional<UserFull> user = userRepository.findUserFullByID(999L);

        assertFalse(user.isPresent());
    }

    @Test
    void shouldReturnEmptyUserFullForInvalidID(){
        Optional<UserFull> user = userRepository.findUserFullByID(-1);

        assertFalse(user.isPresent());
    }


    /// Schema tests

    @Test
    void shouldThrowForNonUniqueLogin(){
        User user = new User();
        user.setLogin(testDataLoader.userMember.getLogin());
        user.setPassword("Password");
        user.setName("Name");
        user.setLastName("LastName");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void shouldThrowForNullLogin(){
        User user = new User();
        user.setLogin(null);
        user.setPassword("Password");
        user.setName("Name");
        user.setLastName("LastName");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void shouldThrowForNullPassword(){
        User user = new User();
        user.setLogin("Login");
        user.setPassword(null);
        user.setName("Name");
        user.setLastName("LastName");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void shouldThrowForNullName(){
        User user = new User();
        user.setLogin("Login");
        user.setPassword("Password");
        user.setName(null);
        user.setLastName("LastName");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    void shouldThrowForNullLastName(){
        User user = new User();
        user.setLogin("Login");
        user.setPassword("Password");
        user.setName("Name");
        user.setLastName(null);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }
}
