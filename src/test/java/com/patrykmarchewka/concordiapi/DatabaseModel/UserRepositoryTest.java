package com.patrykmarchewka.concordiapi.DatabaseModel;


import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithCredentials;
import com.patrykmarchewka.concordiapi.Passwords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestConstructor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class UserRepositoryTest implements UserTestHelper{

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @AfterEach
    void cleanUp(){
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void shouldSaveAndRetrieveUserCorrectlyBasic() {
        long id = createUser("TEST",userRepository).getID();

        UserWithCredentials found = userRepository.findUserWithCredentialsByLogin("TEST").orElse(null);

        assertNotNull(found);
        assertEquals(id, found.getID());
        assertEquals("TEST", found.getLogin());
        assertEquals("John", found.getName());
        assertEquals("Doe", found.getLastName());
        assertNotEquals("d", found.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("d", found.getPassword()));
    }

    @Test
    void shouldReturnUserWithTeams(){
        long id = createUser("TEST",userRepository).getID();

        User found = userRepository.findUserEntityWithTeamRolesAndTeamsByID(id).orElse(null);

        assertNotNull(found);
        assertTrue(found.getTeams().isEmpty());
        assertTrue(found.getTeamRoles().isEmpty());
    }

    @Test
    void shouldReturnTrueForNonExistingUserWithNoTeamRoles(){
        Optional<User> found = userRepository.findUserEntityWithTeamRolesAndTeamsByID(0);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnUserWithTasks(){
        long id = createUser("TEST", userRepository).getID();

        User found = userRepository.findUserEntityWithUserTasksByID(id).orElse(null);

        assertNotNull(found);
        assertTrue(found.getUserTasks().isEmpty());
    }

    @Test
    void shouldReturnTrueForNonExistingUserWithNoUserTasks(){
        Optional<User> found = userRepository.findUserEntityWithUserTasksByID(0);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldSaveAndRetrieveUserCorrectlyFull(){
        long id = createUser("TEST",userRepository).getID();

        User found = userRepository.findUserEntityFullByID(id).orElse(null);

        assertNotNull(found);
        assertEquals(id, found.getID());
        assertEquals("TEST", found.getLogin());
        assertEquals("John", found.getName());
        assertEquals("Doe", found.getLastName());
        assertNotEquals("d", found.getPassword());
        assertTrue(Passwords.CheckPasswordBCrypt("d", found.getPassword()));

        assertTrue(found.getTeams().isEmpty());
        assertTrue(found.getTeamRoles().isEmpty());
        assertTrue(found.getUserTasks().isEmpty());
    }

    @Test
    void shouldReturnTrueForNonExistingUser(){
        Optional<User> found = userRepository.findUserEntityFullByID(0);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnUserByLogin(){
        createUser("TEST",userRepository);

        UserWithCredentials found = userRepository.findUserWithCredentialsByLogin("TEST").orElse(null);

        assertNotNull(found);
    }

    @Test
    void shouldReturnTrueForNonExistingUserByLogin() {
        Optional<UserWithCredentials> found = userRepository.findUserWithCredentialsByLogin("NonExistingUser");
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnTrueForExistsByLogin() {
        createUser("TEST",userRepository);
        assertTrue(userRepository.existsByLogin("TEST"));
    }

    @Test
    void shouldReturnUserByID(){
        long id = createUser("TEST",userRepository).getID();

        User found = userRepository.findUserEntityWithTeamRolesAndTeamsByID(id).orElse(null);

        assertNotNull(found);
    }

    @Test
    void shouldThrowWhenCreatingUserWithAlreadyExistingLogin(){
        createUser("TEST",userRepository);
        assertThrows(DataIntegrityViolationException.class,() -> createUser("TEST",userRepository));
    }
}
