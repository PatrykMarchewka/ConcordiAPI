package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvitationRepositoryTest {

    private final InvitationRepository invitationRepository;
    private final TestDataLoader testDataLoader;

    @Autowired
    public InvitationRepositoryTest(InvitationRepository invitationRepository, TestDataLoader testDataLoader) {
        this.invitationRepository = invitationRepository;
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


    /// findInvitationByUUID

    @Test
    void shouldFindInvitationByUUID(){
        Optional<InvitationIdentity> invitation = invitationRepository.findInvitationByUUID(testDataLoader.invitationRead.getUUID());

        assertTrue(invitation.isPresent());
        assertEquals(testDataLoader.invitationRead.getUUID(), invitation.get().getUUID());
        assertEquals(testDataLoader.invitationRead.getUses(), invitation.get().getUses());
        assertEquals(testDataLoader.invitationRead.getRole(), invitation.get().getRole());
        assertEquals(testDataLoader.invitationRead.getDueTime(), invitation.get().getDueTime());
    }

    @Test
    void shouldReturnEmptyInvitationForNonExistentUUID(){
        Optional<InvitationIdentity> invitation = invitationRepository.findInvitationByUUID("TEST");

        assertFalse(invitation.isPresent());
    }

    @Test
    void shouldReturnEmptyInvitationForEmptyUUID(){
        Optional<InvitationIdentity> invitation = invitationRepository.findInvitationByUUID("");

        assertFalse(invitation.isPresent());
    }

    @Test
    void shouldReturnEmptyInvitationForNullUUID(){
        Optional<InvitationIdentity> invitation = invitationRepository.findInvitationByUUID(null);

        assertFalse(invitation.isPresent());
    }

    @Test
    void shouldReturnEmptyInvitationForCaseSensitivity(){
        Optional<InvitationIdentity> invitation = invitationRepository.findInvitationByUUID(testDataLoader.invitationRead.getUUID().toUpperCase());

        assertFalse(invitation.isPresent());
    }

    /// findInvitationWithTeamByUUID

    @Test
    void shouldFindInvitationWithTeamByUUID(){
        Optional<InvitationWithTeam> invitation = invitationRepository.findInvitationWithTeamByUUID(testDataLoader.invitationRead.getUUID());

        assertTrue(invitation.isPresent());
        assertEquals(testDataLoader.invitationRead.getUUID(), invitation.get().getUUID());
        assertEquals(testDataLoader.invitationRead.getUses(), invitation.get().getUses());
        assertEquals(testDataLoader.invitationRead.getRole(), invitation.get().getRole());
        assertEquals(testDataLoader.invitationRead.getDueTime(), invitation.get().getDueTime());
        assertEquals(testDataLoader.invitationRead.getInvitingTeam(), invitation.get().getInvitingTeam());
    }

    @Test
    void shouldReturnEmptyInvitationWithTeamForEmptyUUID(){
        Optional<InvitationWithTeam> invitation = invitationRepository.findInvitationWithTeamByUUID("");

        assertFalse(invitation.isPresent());
    }

    @Test
    void shouldReturnEmptyInvitationWithTeamForNullUUID(){
        Optional<InvitationWithTeam> invitation = invitationRepository.findInvitationWithTeamByUUID(null);

        assertFalse(invitation.isPresent());
    }

    @Test
    void shouldReturnEmptyInvitationWithTeamForCaseSensitivity(){
        Optional<InvitationWithTeam> invitation = invitationRepository.findInvitationWithTeamByUUID(testDataLoader.invitationRead.getUUID().toUpperCase());

        assertFalse(invitation.isPresent());
    }

    /// findAllInvitationsWithTeamByInvitingTeam

    @Test
    void shouldFindSingleInvitationsWithTeamByInvitingTeam(){
        Set<InvitationWithTeam> invitationSet = invitationRepository.findAllInvitationsWithTeamByInvitingTeam(testDataLoader.teamWrite.getID());

        assertFalse(invitationSet.isEmpty());
        assertEquals(testDataLoader.teamWrite.getInvitations(), invitationSet);
    }

    @Test
    void shouldFindMultipleInvitationsWithTeamByInvitingTeam(){
        Set<InvitationWithTeam> invitationSet = invitationRepository.findAllInvitationsWithTeamByInvitingTeam(testDataLoader.teamRead.getID());

        assertFalse(invitationSet.isEmpty());
        assertEquals(testDataLoader.teamRead.getInvitations(), invitationSet);
    }

    @Test
    void shouldFindNoneInvitationsWithTeamForNonExistentTeam(){
        Set<InvitationWithTeam> invitationSet = invitationRepository.findAllInvitationsWithTeamByInvitingTeam(999L);

        assertTrue(invitationSet.isEmpty());
    }

    @Test
    void shouldFindNoneInvitationsWithTeamForInvalidTeam(){
        Set<InvitationWithTeam> invitationSet = invitationRepository.findAllInvitationsWithTeamByInvitingTeam(-1);

        assertTrue(invitationSet.isEmpty());
    }

    /// findInvitationFullByUUID

    @Test
    void shouldFindInvitationFullByUUID(){
        Optional<InvitationFull> invitation = invitationRepository.findInvitationFullByUUID(testDataLoader.invitationRead.getUUID());

        assertTrue(invitation.isPresent());
        assertEquals(testDataLoader.invitationRead.getUUID(), invitation.get().getUUID());
        assertEquals(testDataLoader.invitationRead.getUses(), invitation.get().getUses());
        assertEquals(testDataLoader.invitationRead.getRole(), invitation.get().getRole());
        assertEquals(testDataLoader.invitationRead.getDueTime(), invitation.get().getDueTime());
        assertEquals(testDataLoader.invitationRead.getInvitingTeam(), invitation.get().getInvitingTeam());
    }

    @Test
    void shouldReturnEmptyInvitationFullForEmptyUUID(){
        Optional<InvitationFull> invitation = invitationRepository.findInvitationFullByUUID("");

        assertFalse(invitation.isPresent());
    }

    @Test
    void shouldReturnEmptyInvitationFullForNullUUID(){
        Optional<InvitationFull> invitation = invitationRepository.findInvitationFullByUUID(null);

        assertFalse(invitation.isPresent());
    }

    @Test
    void shouldReturnEmptyInvitationFullForCaseSensitivity(){
        Optional<InvitationFull> invitation = invitationRepository.findInvitationFullByUUID(testDataLoader.invitationRead.getUUID().toUpperCase());

        assertFalse(invitation.isPresent());
    }
}
