package com.patrykmarchewka.concordiapi.DatabaseModel;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TeamRepositoryTest implements TeamTestHelper{

    private final TeamRepository teamRepository;

    @Autowired
    public TeamRepositoryTest(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @AfterEach
    void cleanUp(){
        teamRepository.deleteAll();
        teamRepository.flush();
    }

    @Test
    void shouldSaveAndRetrieveTeamCorrectlyBasic(){
        long id = createTeam(teamRepository).getID();

        Team found = teamRepository.findTeamById(id).orElse(null);

        assertNotNull(found);
        assertEquals(id, found.getID());
        assertEquals("Testing", found.getName());
    }

    @Test
    void shouldReturnTeamWithTeamTasks(){
        long id = createTeam(teamRepository).getID();

        Team found = teamRepository.findTeamWithTeamTasksByID(id).orElse(null);

        assertNotNull(found);
        assertTrue(found.getTeamTasks().isEmpty());
    }

    @Test
    void shouldReturnTrueForNonExistingTeamWithNoTasks(){
        Optional<Team> found = teamRepository.findTeamWithTeamTasksByID(0);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnTeamWithUserRolesAndUsers(){
        long id = createTeam(teamRepository).getID();

        Team found = teamRepository.findTeamWithUserRolesAndUsersByID(id).orElse(null);

        assertNotNull(found);
        assertTrue(found.getUserRoles().isEmpty());
        assertTrue(found.getTeammates().isEmpty());
    }

    @Test
    void shouldReturnTrueForNonExistingTeamWithNoRoles(){
        Optional<Team> found = teamRepository.findTeamWithUserRolesAndUsersByID(0);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnTeamWithInvitations(){
        long id = createTeam(teamRepository).getID();

        Team found = teamRepository.findTeamWithInvitationsByID(id).orElse(null);

        assertNotNull(found);
        assertTrue(found.getInvitations().isEmpty());
    }

    @Test
    void shouldReturnTrueForNonExistingTeamWithNoInvitations(){
        Optional<Team> found = teamRepository.findTeamWithInvitationsByID(0);
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldSaveAndRetrieveTeamCorrectlyFull(){
        long id = createTeam(teamRepository).getID();

        Team found = teamRepository.findTeamFullByID(id).orElse(null);

        assertNotNull(found);
        assertEquals(id, found.getID());
        assertEquals("Testing", found.getName());
        assertTrue(found.getTeamTasks().isEmpty());
        assertTrue(found.getTeammates().isEmpty());
        assertTrue(found.getInvitations().isEmpty());
        assertTrue(found.getUserRoles().isEmpty());
    }
}
