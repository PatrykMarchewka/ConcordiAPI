package com.patrykmarchewka.concordiapi.DatabaseModel;


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

        User found = userRepository.findByLogin("TEST").orElse(null);

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

        User found = userRepository.findUserWithTeamRolesAndTeamsByID(id).orElse(null);

        assertNotNull(found);
        assertTrue(found.getTeams().isEmpty());
        assertTrue(found.getTeamRoles().isEmpty());
    }

    @Test
    void shouldReturnTrueForNonExistingUserWithNoTeamRoles(){
        Optional<User> found = userRepository.findUserWithTeamRolesAndTeamsByID(0);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnUserWithTasks(){
        long id = createUser("TEST", userRepository).getID();

        User found = userRepository.findUserWithUserTasksByID(id).orElse(null);

        assertNotNull(found);
        assertTrue(found.getUserTasks().isEmpty());
    }

    @Test
    void shouldReturnTrueForNonExistingUserWithNoUserTasks(){
        Optional<User> found = userRepository.findUserWithUserTasksByID(0);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldSaveAndRetrieveUserCorrectlyFull(){
        long id = createUser("TEST",userRepository).getID();

        User found = userRepository.findUserFullByID(id).orElse(null);

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
        Optional<User> found = userRepository.findUserFullByID(0);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnUserByLogin(){
        createUser("TEST",userRepository);

        User found = userRepository.findByLogin("TEST").orElse(null);

        assertNotNull(found);
    }

    @Test
    void shouldReturnTrueForNonExistingUser() {
        Optional<User> found = userRepository.findByLogin("NonExistingUser");
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

        User found = userRepository.findUserWithTeamRolesAndTeamsByID(id).orElse(null);


        assertNotNull(found);
    }

    @Test
    void shouldReturnNullForNonExistingUserID(){
        User found = userRepository.findUserWithTeamRolesAndTeamsByID(999L).orElse(null);

        assertNull(found);
    }

    @Test
    void shouldThrowWhenCreatingUserWithAlreadyExistingLogin(){
        createUser("TEST",userRepository);
        assertThrows(DataIntegrityViolationException.class,() -> createUser("TEST",userRepository));
    }
}
