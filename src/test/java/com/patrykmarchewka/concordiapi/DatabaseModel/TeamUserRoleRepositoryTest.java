package com.patrykmarchewka.concordiapi.DatabaseModel;


import com.patrykmarchewka.concordiapi.TestDataLoader;
import com.patrykmarchewka.concordiapi.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamUserRoleRepositoryTest {

    private final TeamUserRoleRepository teamUserRoleRepository;
    private final TestDataLoader testDataLoader;

    @Autowired
    public TeamUserRoleRepositoryTest(TeamUserRoleRepository teamUserRoleRepository, TestDataLoader testDataLoader) {
        this.teamUserRoleRepository = teamUserRoleRepository;
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


    /// findAllTeamUserRolesByTeamIDAndUserRole

    @Test
    void shouldFindTeamUserRoleByUserIDAndTeamID(){
        Optional<TeamUserRole> teamUserRole = teamUserRoleRepository.findTeamUserRoleByUserIDAndTeamID(testDataLoader.userMember.getID(), testDataLoader.teamRead.getID());

        assertTrue(teamUserRole.isPresent());
        assertEquals(testDataLoader.userMember, teamUserRole.get().getUser());
        assertEquals(testDataLoader.teamRead, teamUserRole.get().getTeam());
        assertEquals(UserRole.MEMBER, teamUserRole.get().getUserRole());
    }

    @Test
    void shouldReturnEmptyTeamUserRoleForNonExistentUserIDAndTeamID(){
        Optional<TeamUserRole> teamUserRole = teamUserRoleRepository.findTeamUserRoleByUserIDAndTeamID(999L, testDataLoader.teamRead.getID());

        assertFalse(teamUserRole.isPresent());
    }

    @Test
    void shouldReturnEmptyTeamUserRoleForUserIDAndNonExistentTeamID(){
        Optional<TeamUserRole> teamUserRole = teamUserRoleRepository.findTeamUserRoleByUserIDAndTeamID(testDataLoader.userMember.getID(), 999L);

        assertFalse(teamUserRole.isPresent());
    }

    @Test
    void shouldReturnEmptyTeamUserRoleForInvalidUserIDAndTeamID(){
        Optional<TeamUserRole> teamUserRole = teamUserRoleRepository.findTeamUserRoleByUserIDAndTeamID(-1, testDataLoader.teamRead.getID());

        assertFalse(teamUserRole.isPresent());
    }

    @Test
    void shouldReturnEmptyTeamUserRoleForUserIDAndInvalidTeamID(){
        Optional<TeamUserRole> teamUserRole = teamUserRoleRepository.findTeamUserRoleByUserIDAndTeamID(testDataLoader.userMember.getID(), -1);

        assertFalse(teamUserRole.isPresent());
    }

    /// findAllTeamUserRolesByTeamIDAndUserRole

    @Test
    void shouldFindSingleTeamUserRolesForTeamIDAndUserRole(){
        Set<TeamUserRole> teamUserRoleSet = teamUserRoleRepository.findAllTeamUserRolesByTeamIDAndUserRole(testDataLoader.teamRead.getID(), UserRole.MEMBER);

        assertFalse(teamUserRoleSet.isEmpty());
        assertEquals(testDataLoader.teamRead.getUserRoles().stream().filter(teamUserRole -> teamUserRole.getUserRole().isMember()).collect(Collectors.toUnmodifiableSet()), teamUserRoleSet);
    }

    @Test
    void shouldFindMultipleTeamUserRolesForTeamIDAndUserRole(){
        Set<TeamUserRole> teamUserRoleSet = teamUserRoleRepository.findAllTeamUserRolesByTeamIDAndUserRole(testDataLoader.teamWrite.getID(), UserRole.OWNER);

        assertFalse(teamUserRoleSet.isEmpty());
        assertEquals(testDataLoader.teamWrite.getUserRoles().stream().filter(teamUserRole -> teamUserRole.getUserRole().isOwner()).collect(Collectors.toUnmodifiableSet()), teamUserRoleSet);
    }

    @Test
    void shouldFindNoneTeamUserRolesForNonExistentTeamIDAndUserRole(){
        Set<TeamUserRole> teamUserRoleSet = teamUserRoleRepository.findAllTeamUserRolesByTeamIDAndUserRole(999L, UserRole.MEMBER);

        assertTrue(teamUserRoleSet.isEmpty());
    }

    @Test
    void shouldFindNoneTeamUserRolesForInvalidTeamIDAndUserRole(){
        Set<TeamUserRole> teamUserRoleSet = teamUserRoleRepository.findAllTeamUserRolesByTeamIDAndUserRole(-1, UserRole.MEMBER);

        assertTrue(teamUserRoleSet.isEmpty());
    }


    /// Schema tests

    @Test
    void shouldThrowForNonUniqueTeamUserRole(){
        TeamUserRole teamUserRole = new TeamUserRole(testDataLoader.userMember, testDataLoader.teamRead, UserRole.MEMBER);

        assertThrows(DataIntegrityViolationException.class, () -> teamUserRoleRepository.save(teamUserRole));
    }

    @Test
    void shouldThrowForNullUser(){
        TeamUserRole teamUserRole = new TeamUserRole(null, testDataLoader.teamRead, UserRole.MEMBER);

        assertThrows(DataIntegrityViolationException.class, () -> teamUserRoleRepository.save(teamUserRole));
    }

    @Test
    void shouldThrowForNullTeam(){
        TeamUserRole teamUserRole = new TeamUserRole(testDataLoader.userMember, null, UserRole.MEMBER);

        assertThrows(DataIntegrityViolationException.class, () -> teamUserRoleRepository.save(teamUserRole));
    }

    @Test
    void shouldThrowForNullUserRole(){
        TeamUserRole teamUserRole = new TeamUserRole(testDataLoader.userNoTeam, testDataLoader.teamRead, null);

        assertThrows(DataIntegrityViolationException.class, () -> teamUserRoleRepository.save(teamUserRole));
    }



}
