package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.Teams.TeamRequestBodyHelper;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvitationServiceTest implements InvitationRequestBodyHelper, TeamRequestBodyHelper, UserRequestBodyHelper {

    private final InvitationService invitationService;
    private final TestDataLoader testDataLoader;

    @Autowired
    public InvitationServiceTest(InvitationService invitationService, TestDataLoader testDataLoader) {
        this.invitationService = invitationService;
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


    /// createInvitation

    @Test
    void shouldCreateInvitation(){
        InvitationRequestBody body = new InvitationRequestBody((short) 1, UserRole.MEMBER, null);
        Invitation invitation = invitationService.createInvitation(UserRole.OWNER, body, testDataLoader.teamWrite.getID());

        assertNotNull(invitation.getUUID());
        assertFalse(invitation.getUUID().isBlank());
        assertEquals(1, invitation.getUses());
        assertEquals(UserRole.MEMBER, invitation.getRole());
        assertNull(invitation.getDueTime());
    }

    /**
     * Cannot create invitation with higher role than caller (eg MEMBER cannot create invitation that would grant ADMIN role)
     */
    @Test
    void shouldThrowForLowerRole(){
        InvitationRequestBody body = new InvitationRequestBody((short) 1, UserRole.ADMIN, null);
        assertThrows(NoPrivilegesException.class, () -> invitationService.createInvitation(UserRole.MEMBER, body, testDataLoader.teamWrite.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamID(long ID){
        assertThrows(NotFoundException.class, () -> invitationService.createInvitation(UserRole.OWNER, new InvitationRequestBody(), ID));
    }

    /// putInvitation

    @Test
    void shouldPutInvitation(){
        InvitationRequestBody body = new InvitationRequestBody((short) 1, UserRole.ADMIN, OffsetDateTime.MAX);
        InvitationFull invitation = invitationService.putInvitation(testDataLoader.invitationWrite.getUUID(), body);

        assertEquals(testDataLoader.invitationWrite.getUUID(), invitation.getUUID());
        assertEquals(body.getUses(), invitation.getUses());
        assertEquals(body.getRole(), invitation.getRole());
        assertEquals(body.getDueDate(), invitation.getDueTime());
        assertEquals(testDataLoader.invitationWrite.getInvitingTeam(), invitation.getInvitingTeam());
    }

    @Test
    void shouldPutInvitationDBCheck(){
        InvitationRequestBody body = new InvitationRequestBody((short) 1, UserRole.ADMIN, OffsetDateTime.MAX);
        InvitationFull invitation = invitationService.putInvitation(testDataLoader.invitationWrite.getUUID(), body);

        InvitationFull actual = invitationService.getInvitationFullByUUID(invitation.getUUID());

        assertEquals(invitation.getUUID(), actual.getUUID());
        assertEquals(invitation.getUses(), actual.getUses());
        assertEquals(invitation.getRole(), actual.getRole());
        assertEquals(OffsetDateTimeConverter.converted(invitation.getDueTime()), actual.getDueTime());
        assertEquals(invitation.getInvitingTeam(), actual.getInvitingTeam());
    }

    /// patchInvitation

    @Test
    void shouldPatchInvitation(){
        InvitationRequestBody body = new InvitationRequestBody(null, UserRole.MEMBER, null);
        InvitationFull invitation = invitationService.patchInvitation(testDataLoader.invitationWrite.getUUID(), body);

        assertEquals(testDataLoader.invitationWrite.getUUID(), invitation.getUUID());
        assertEquals(testDataLoader.invitationWrite.getUses(), invitation.getUses());
        assertEquals(body.getRole(), invitation.getRole());
        assertEquals(testDataLoader.invitationWrite.getDueTime(), invitation.getDueTime());
        assertEquals(testDataLoader.invitationWrite.getInvitingTeam(), invitation.getInvitingTeam());
    }

    @Test
    void shouldPatchInvitationDBCheck(){
        InvitationRequestBody body = new InvitationRequestBody(null, UserRole.MEMBER, null);
        InvitationFull invitation = invitationService.patchInvitation(testDataLoader.invitationWrite.getUUID(), body);

        InvitationFull actual = invitationService.getInvitationFullByUUID(invitation.getUUID());

        assertEquals(invitation.getUUID(), actual.getUUID());
        assertEquals(invitation.getUses(), actual.getUses());
        assertEquals(invitation.getRole(), actual.getRole());
        assertEquals(invitation.getDueTime(), actual.getDueTime());
        assertEquals(invitation.getInvitingTeam(), actual.getInvitingTeam());
    }

    @Test
    void shouldPatchInvitationFully(){
        InvitationRequestBody body = new InvitationRequestBody((short) 1, UserRole.MANAGER, OffsetDateTime.MAX);
        InvitationFull invitation = invitationService.patchInvitation(testDataLoader.invitationWrite.getUUID(), body);

        assertEquals(testDataLoader.invitationWrite.getUUID(), invitation.getUUID());
        assertEquals(body.getUses(), invitation.getUses());
        assertEquals(body.getRole(), invitation.getRole());
        assertEquals(body.getDueDate(), invitation.getDueTime());
        assertEquals(testDataLoader.invitationWrite.getInvitingTeam(), invitation.getInvitingTeam());
    }

    @Test
    void shouldPatchInvitationFullyDBCheck(){
        InvitationRequestBody body = new InvitationRequestBody((short) 1, UserRole.MANAGER, OffsetDateTime.MAX);
        InvitationFull invitation = invitationService.patchInvitation(testDataLoader.invitationWrite.getUUID(), body);

        InvitationFull actual = invitationService.getInvitationFullByUUID(invitation.getUUID());

        assertEquals(invitation.getUUID(), actual.getUUID());
        assertEquals(invitation.getUses(), actual.getUses());
        assertEquals(invitation.getRole(), actual.getRole());
        assertEquals(OffsetDateTimeConverter.converted(invitation.getDueTime()), actual.getDueTime());
        assertEquals(invitation.getInvitingTeam(), actual.getInvitingTeam());
    }

    /// useInvitation

    @Test
    void shouldUseInvitation(){
        Invitation invitation = (Invitation) invitationService.useInvitation(testDataLoader.invitationWrite.getUUID(), testDataLoader.userNoTeam);
        Team refreshedTeam = testDataLoader.refreshTeam(invitation.getInvitingTeam());
        User refreshedUser = testDataLoader.refreshUser(testDataLoader.userNoTeam);

        assertEquals(testDataLoader.invitationWrite.getUUID(), invitation.getUUID());
        assertEquals(testDataLoader.invitationWrite.getUses() - 1, invitation.getUses());
        assertEquals(testDataLoader.invitationWrite.getRole(), invitation.getRole());
        assertEquals(testDataLoader.invitationWrite.getDueTime(), invitation.getDueTime());
        assertEquals(testDataLoader.invitationWrite.getInvitingTeam(), invitation.getInvitingTeam());

        assertTrue(refreshedUser.getTeams().contains(invitation.getInvitingTeam()));
        assertTrue(refreshedTeam.getUserRoles().stream().anyMatch(teamUserRole -> teamUserRole.getUser().equals(refreshedUser) && teamUserRole.getUserRole() == invitation.getRole()));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"TEST"})
    void shouldThrowForInvalidUUIDUseInvitation(String UUID){
        assertThrows(NotFoundException.class, () -> invitationService.useInvitation(UUID, testDataLoader.userNoTeam));
    }

    @Test
    void shouldThrowForExpiredDateUseInvitation(){
        assertThrows(BadRequestException.class, () -> invitationService.useInvitation(testDataLoader.invitationExpired.getUUID(), testDataLoader.userNoTeam));
    }

    @Test
    void shouldThrowForNoUsesUseInvitation(){
        assertThrows(BadRequestException.class, () -> invitationService.useInvitation(testDataLoader.invitationNoUses.getUUID(), testDataLoader.userNoTeam));
    }

    @Test
    void shouldThrowForUserAlreadyInTeamUseInvitation(){
        assertThrows(ConflictException.class, () -> invitationService.useInvitation(testDataLoader.invitationWrite.getUUID(), testDataLoader.userMember));
    }

    /// saveInvitation

    @Test
    void shouldsaveInvitation(){
        testDataLoader.invitationWrite.setUses((short) 101);
        invitationService.saveInvitation(testDataLoader.invitationWrite);

        assertEquals(101, testDataLoader.refreshInvitation(testDataLoader.invitationWrite).getUses());
    }

    /// deleteInvitation

    @Test
    void shouldDeleteInvitation(){
        invitationService.deleteInvitation(testDataLoader.invitationDelete.getUUID());

        assertThrows(NotFoundException.class, () -> invitationService.getInvitationByUUID(testDataLoader.invitationDelete.getUUID()));
    }

    /// getInvitationsDTO

    @Test
    void shouldGetInvitationsDTO(){
        Set<InvitationManagerDTO> set = invitationService.getInvitationsDTO(testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead.getInvitations().size(), set.size());
        Set<InvitationManagerDTO> actual = testDataLoader.teamRead.getInvitations().stream().map(InvitationManagerDTO::new).collect(Collectors.toUnmodifiableSet());

        for (InvitationManagerDTO dto : actual){
            System.err.println(dto.getUUID() + " : " + set.contains(dto));
        }
        assertTrue(set.containsAll(testDataLoader.teamRead.getInvitations().stream().map(InvitationManagerDTO::new).collect(Collectors.toUnmodifiableSet())));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetInvitationsDTO(long ID){
        assertThrows(NotFoundException.class, () -> invitationService.getInvitationsDTO(ID));
    }

    /// getInvitationByUUID

    @Test
    void shouldGetInvitationByUUID(){
        InvitationIdentity invitation = invitationService.getInvitationByUUID(testDataLoader.invitationRead.getUUID());

        assertEquals(testDataLoader.invitationRead.getUUID(), invitation.getUUID());
        assertEquals(testDataLoader.invitationRead.getUses(), invitation.getUses());
        assertEquals(testDataLoader.invitationRead.getRole(), invitation.getRole());
        assertEquals(testDataLoader.invitationRead.getDueTime(), invitation.getDueTime());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"TEST"})
    void shouldThrowForInvalidUUIDGetInvitationByUUID(String uuid){
        assertThrows(NotFoundException.class, () -> invitationService.getInvitationByUUID(uuid));
    }

    /// getInvitationWithTeamByUUID

    @Test
    void shouldGetInvitationWithTeamByUUID(){
        InvitationWithTeam invitation = invitationService.getInvitationWithTeamByUUID(testDataLoader.invitationRead.getUUID());

        assertEquals(testDataLoader.invitationRead.getUUID(), invitation.getUUID());
        assertEquals(testDataLoader.invitationRead.getUses(), invitation.getUses());
        assertEquals(testDataLoader.invitationRead.getRole(), invitation.getRole());
        assertEquals(testDataLoader.invitationRead.getDueTime(), invitation.getDueTime());
        assertEquals(testDataLoader.invitationRead.getInvitingTeam(), invitation.getInvitingTeam());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"TEST"})
    void shouldThrowForInvalidUUIDGetInvitationWithTeamByUUID(String uuid){
        assertThrows(NotFoundException.class, () -> invitationService.getInvitationWithTeamByUUID(uuid));
    }

    /// getAllInvitationsWithTeamByInvitingTeamID

    @Test
    void shouldGetAllInvitationsWithTeamByInvitingTeamID(){
        Set<InvitationWithTeam> set = invitationService.getAllInvitationsWithTeamByInvitingTeamID(testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead.getInvitations().size(), set.size());
        assertTrue(testDataLoader.teamRead.getInvitations().containsAll(set));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetAllInvitationsWithTeamByInvitingTeamID(long ID){
        assertThrows(NotFoundException.class, () -> invitationService.getAllInvitationsWithTeamByInvitingTeamID(ID));
    }

    /// getInvitationFullByUUID

    @Test
    void shouldGetInvitationFullByUUID(){
        InvitationFull invitation = invitationService.getInvitationFullByUUID(testDataLoader.invitationRead.getUUID());

        assertEquals(testDataLoader.invitationRead.getUUID(), invitation.getUUID());
        assertEquals(testDataLoader.invitationRead.getUses(), invitation.getUses());
        assertEquals(testDataLoader.invitationRead.getRole(), invitation.getRole());
        assertEquals(testDataLoader.invitationRead.getDueTime(), invitation.getDueTime());
        assertEquals(testDataLoader.invitationRead.getInvitingTeam(), invitation.getInvitingTeam());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"TEST"})
    void shouldThrowForInvalidUUIDGetInvitationFullByUUID(String uuid){
        assertThrows(NotFoundException.class, () -> invitationService.getInvitationFullByUUID(uuid));
    }
}
