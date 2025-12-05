package com.patrykmarchewka.concordiapi.DTO;


import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMeDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithTeamRoles;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDTOTest {

    private final TestDataLoader testDataLoader;
    private final UserService userService;

    @Autowired
    public UserDTOTest(TestDataLoader testDataLoader, UserService userService) {
        this.testDataLoader = testDataLoader;
        this.userService = userService;
    }

    @BeforeAll
    void initialize(){
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }

    /// UserMe
    @Transactional
    @Test
    void assertUserMe(){
        UserWithTeamRoles user = userService.getUserWithTeamRolesAndTeams(testDataLoader.userReadOwner.getID());
        UserMeDTO dto = new UserMeDTO(user);

        assertEquals(user.getID(), dto.getID());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getLastName(), dto.getLastName());
        assertEquals(user.getTeams().size(), dto.getTeams().size());
    }

    /// UserMember
    @Test
    void assertUserMember(){
        UserMemberDTO dto = new UserMemberDTO(testDataLoader.userReadOwner);
        assertEquals(testDataLoader.userReadOwner.getID(), dto.getID());
        assertEquals(testDataLoader.userReadOwner.getName(), dto.getName());
        assertEquals(testDataLoader.userReadOwner.getLastName(), dto.getLastName());
    }

    /// Comparison
    @Transactional
    @Test
    void shouldFailComparison(){
        UserWithTeamRoles user = userService.getUserWithTeamRolesAndTeams(testDataLoader.userReadOwner.getID());
        UserMeDTO dto = new UserMeDTO(user);
        UserMemberDTO dto1 = new UserMemberDTO(user);

        assertNotEquals(dto, dto1);
    }
}
