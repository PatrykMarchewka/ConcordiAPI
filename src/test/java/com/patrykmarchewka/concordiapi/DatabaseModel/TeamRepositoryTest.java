package com.patrykmarchewka.concordiapi.DatabaseModel;


import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithInvitations;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithTasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;
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
public class TeamRepositoryTest {

    private final TeamRepository teamRepository;
    private final TestDataLoader testDataLoader;

    @Autowired
    public TeamRepositoryTest(TeamRepository teamRepository, TestDataLoader testDataLoader) {
        this.teamRepository = teamRepository;
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


    /// findTeamByID

    @Test
    void shouldFindTeamByID(){
        Optional<TeamIdentity> team = teamRepository.findTeamByID(testDataLoader.teamRead.getID());

        assertTrue(team.isPresent());
        assertEquals(testDataLoader.teamRead.getID(), team.get().getID());
        assertEquals(testDataLoader.teamRead.getName(), team.get().getName());
    }

    @Test
    void shouldReturnEmptyTeamForNonExistentID(){
        Optional<TeamIdentity> team = teamRepository.findTeamByID(999L);

        assertFalse(team.isPresent());
    }

    @Test
    void shouldReturnEmptyForInvalidID(){
        Optional<TeamIdentity> team = teamRepository.findTeamByID(-1);

        assertFalse(team.isPresent());
    }

    /// findTeamWithUserRolesByID

    @Test
    void shouldFindTeamWithUserRolesByID(){
        Optional<TeamWithUserRoles> team = teamRepository.findTeamWithUserRolesByID(testDataLoader.teamRead.getID());

        assertTrue(team.isPresent());
        assertEquals(testDataLoader.teamRead.getID(), team.get().getID());
        assertEquals(testDataLoader.teamRead.getName(), team.get().getName());
        assertEquals(testDataLoader.teamRead.getUserRoles(), team.get().getUserRoles());
    }

    @Test
    void shouldReturnEmptyTeamWithUserRolesForNonExistentID(){
        Optional<TeamWithUserRoles> team = teamRepository.findTeamWithUserRolesByID(999L);

        assertFalse(team.isPresent());
    }

    @Test
    void shouldReturnEmptyTeamWithUserRolesForInvalidID(){
        Optional<TeamWithUserRoles> team = teamRepository.findTeamWithUserRolesByID(-1);

        assertFalse(team.isPresent());
    }

    /// findTeamWithTasksByID

    @Test
    void shouldFindTeamWithTasksByID(){
        Optional<TeamWithTasks> team = teamRepository.findTeamWithTasksByID(testDataLoader.teamRead.getID());

        assertTrue(team.isPresent());
        assertEquals(testDataLoader.teamRead.getID(), team.get().getID());
        assertEquals(testDataLoader.teamRead.getName(), team.get().getName());
        assertEquals(testDataLoader.teamRead.getTeamTasks(), team.get().getTeamTasks());
    }

    @Test
    void shouldReturnEmptyTeamWithTasksForNonExistentID(){
        Optional<TeamWithTasks> team = teamRepository.findTeamWithTasksByID(999L);

        assertFalse(team.isPresent());
    }

    @Test
    void shouldReturnEmptyTeamWithTasksForInvalidID(){
        Optional<TeamWithTasks> team = teamRepository.findTeamWithTasksByID(-1);

        assertFalse(team.isPresent());
    }

    /// findTeamWithInvitationsByID

    @Test
    void shouldFindTeamWithInvitationsByID(){
        Optional<TeamWithInvitations> team = teamRepository.findTeamWithInvitationsByID(testDataLoader.teamRead.getID());

        assertTrue(team.isPresent());
        assertEquals(testDataLoader.teamRead.getID(), team.get().getID());
        assertEquals(testDataLoader.teamRead.getName(), team.get().getName());
        assertEquals(testDataLoader.teamRead.getInvitations(), team.get().getInvitations());
    }

    @Test
    void shouldReturnEmptyTeamWithInvitationsForNonExistentID(){
        Optional<TeamWithInvitations> team = teamRepository.findTeamWithInvitationsByID(999L);

        assertFalse(team.isPresent());
    }

    @Test
    void shouldReturnEmptyTeamWithInvitationsForInvalidID(){
        Optional<TeamWithInvitations> team = teamRepository.findTeamWithInvitationsByID(-1);

        assertFalse(team.isPresent());
    }

    /// findTeamWithUserRolesAndTasksByID

    @Test
    void shouldFindTeamWithUserRolesAndTasksByID(){
        Optional<TeamWithUserRolesAndTasks> team = teamRepository.findTeamWithUserRolesAndTasksByID(testDataLoader.teamRead.getID());

        assertTrue(team.isPresent());
        assertEquals(testDataLoader.teamRead.getID(), team.get().getID());
        assertEquals(testDataLoader.teamRead.getName(), team.get().getName());
        assertEquals(testDataLoader.teamRead.getUserRoles(), team.get().getUserRoles());
        assertEquals(testDataLoader.teamRead.getTeamTasks(), team.get().getTeamTasks());
    }

    @Test
    void shouldReturnEmptyTeamWithUserRolesAndTasksForNonExistentID(){
        Optional<TeamWithUserRolesAndTasks> team = teamRepository.findTeamWithUserRolesAndTasksByID(999L);

        assertFalse(team.isPresent());
    }

    @Test
    void shouldReturnEmptyTeamWithUserRolesAndTasksForInvalidID(){
        Optional<TeamWithUserRolesAndTasks> team = teamRepository.findTeamWithUserRolesAndTasksByID(-1);

        assertFalse(team.isPresent());
    }

    /// findTeamFullByID

    @Test
    void shouldFindTeamFullByID(){
        Optional<TeamFull> team = teamRepository.findTeamFullByID(testDataLoader.teamRead.getID());

        assertTrue(team.isPresent());
        assertEquals(testDataLoader.teamRead.getID(), team.get().getID());
        assertEquals(testDataLoader.teamRead.getName(), team.get().getName());
        assertEquals(testDataLoader.teamRead.getUserRoles(), team.get().getUserRoles());
        assertEquals(testDataLoader.teamRead.getTeamTasks(), team.get().getTeamTasks());
        assertEquals(testDataLoader.teamRead.getInvitations(), team.get().getInvitations());
    }

    @Test
    void shouldReturnEmptyTeamFullForNonExistentID(){
        Optional<TeamFull> team = teamRepository.findTeamFullByID(999L);

        assertFalse(team.isPresent());
    }

    @Test
    void shouldReturnEmptyTeamFullForInvalidID(){
        Optional<TeamFull> team = teamRepository.findTeamFullByID(-1);

        assertFalse(team.isPresent());
    }


    /// Schema tests

    @Test
    void shouldThrowForNullName(){
        Team team = new Team();
        team.setName(null);

        assertThrows(DataIntegrityViolationException.class, () -> teamRepository.save(team));
    }
}
