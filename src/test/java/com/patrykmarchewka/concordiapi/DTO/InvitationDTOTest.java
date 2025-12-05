package com.patrykmarchewka.concordiapi.DTO;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationMemberDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
import com.patrykmarchewka.concordiapi.Invitations.InvitationService;
import com.patrykmarchewka.concordiapi.TestDataLoader;
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
public class InvitationDTOTest {

    private final TestDataLoader testDataLoader;
    private final InvitationService invitationService;

    @Autowired
    public InvitationDTOTest(final TestDataLoader testDataLoader, final InvitationService invitationService){
        this.testDataLoader = testDataLoader;
        this.invitationService = invitationService;
    }

    @BeforeAll
    void initialize(){
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }

    /// InvitationManager
    @Test
    @Transactional
    void assertInvitationManagerDTO(){
        InvitationWithTeam invitation = invitationService.getInvitationWithTeamByUUID(testDataLoader.invitationExpired.getUUID());
        InvitationManagerDTO dto = new InvitationManagerDTO(invitation);

        assertEquals(invitation.getUUID(), dto.getUUID());
        assertEquals(invitation.getInvitingTeam().getID(), dto.getTeam().getID());
        assertEquals(invitation.getRole(), dto.getRole());
        assertEquals(invitation.getUses(), dto.getUses());
        assertEquals(invitation.getDueTime(), dto.getDueTime());
    }

    /// InvitationMember
    @Test
    @Transactional
    void assertInvitationMemberDTO(){
        InvitationWithTeam invitation = invitationService.getInvitationWithTeamByUUID(testDataLoader.invitationExpired.getUUID());
        InvitationMemberDTO dto = new InvitationMemberDTO(invitation);

        assertEquals(invitation.getUUID(), dto.getUUID());
        assertEquals(invitation.getInvitingTeam().getID(), dto.getTeam().getID());
        assertEquals(invitation.getRole(), dto.getRole());
        assertEquals(invitation.getUses(), dto.getUses());
        assertEquals(invitation.getDueTime(), dto.getDueTime());
    }

    /// Comparison
    @Test
    @Transactional
    void shouldFailComparison(){
        InvitationWithTeam invitation = invitationService.getInvitationWithTeamByUUID(testDataLoader.invitationExpired.getUUID());
        InvitationManagerDTO dto = new InvitationManagerDTO(invitation);
        InvitationMemberDTO dto1 = new InvitationMemberDTO(invitation);

        assertNotEquals(dto, dto1);
    }

}
