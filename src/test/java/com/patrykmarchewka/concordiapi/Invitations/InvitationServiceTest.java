package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.Teams.TeamRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import com.patrykmarchewka.concordiapi.Users.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class InvitationServiceTest implements InvitationRequestBodyHelper, TeamRequestBodyHelper, UserRequestBodyHelper {

    private final InvitationService invitationService;
    private final TeamUserRoleService teamUserRoleService;
    private final TeamService teamService;
    private final UserService userService;

    private User user;
    private Team team;
    private Invitation invitation;


    public InvitationServiceTest(InvitationService invitationService, TeamUserRoleService teamUserRoleService, TeamService teamService, UserService userService) {
        this.invitationService = invitationService;
        this.teamUserRoleService = teamUserRoleService;
        this.teamService = teamService;
        this.userService = userService;
    }

    @BeforeEach
    void initialize(){
        InvitationRequestBody body = createInvitationRequestBody(UserRole.ADMIN);
        TeamRequestBody teamRequestBody = createTeamRequestBody("InvitingTeam");
        UserRequestBody userRequestBody = createUserRequestBody("JohnD");
        user = userService.createUser(userRequestBody);
        team = teamService.createTeam(teamRequestBody, user);
        invitation = invitationService.createInvitation(UserRole.OWNER,body, team.getID());
    }

    @AfterEach
    void cleanUp(){
        invitationService.deleteAll();
        teamUserRoleService.deleteAll();
        teamService.deleteAll();
        userService.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveInvitationCorrectly(){
        InvitationWithTeam found = invitationService.getInvitationWithTeamByUUID(invitation.getUUID());

        assertEquals(invitation.getUUID(), found.getUUID());
        assertEquals(invitation.getRole(), found.getRole());
        assertEquals(invitation.getUses(), found.getUses());
        assertEquals(invitation.getDueTime(), found.getDueTime());
        assertEquals(invitation.getInvitingTeam(), found.getInvitingTeam());
    }

    @Test
    void shouldPutInvitation(){
        InvitationRequestBody body1 = createInvitationRequestBody(UserRole.MEMBER, (short)101, OffsetDateTimeConverter.MINConverted());

        Invitation found = invitationService.putInvitation(invitation.getUUID(), body1);

        assertEquals(invitation.getUUID(), found.getUUID());
        assertEquals(UserRole.MEMBER, found.getRole());
        assertEquals(101, found.getUses());
        assertEquals(OffsetDateTimeConverter.MINConverted(), found.getDueTime());
        assertEquals(invitation.getInvitingTeam(), found.getInvitingTeam());
    }

    @Test
    void shouldPatchInvitation(){
        InvitationRequestBody body1 = createInvitationRequestBody(UserRole.MANAGER);

        Invitation found = invitationService.patchInvitation(invitation.getUUID(), body1);

        assertEquals(invitation.getUUID(), found.getUUID());
        assertEquals(UserRole.MANAGER, found.getRole());
        assertEquals(invitation.getUses(), found.getUses());
        assertEquals(invitation.getDueTime(), found.getDueTime());
        assertEquals(invitation.getInvitingTeam(), found.getInvitingTeam());
    }

    @Test
    void shouldPatchInvitationFull(){
        InvitationRequestBody body1 = createInvitationRequestBody(UserRole.MEMBER, (short)101, OffsetDateTimeConverter.MINConverted());

        Invitation found = invitationService.patchInvitation(invitation.getUUID(), body1);

        assertEquals(invitation.getUUID(), found.getUUID());
        assertEquals(UserRole.MEMBER, found.getRole());
        assertEquals(101, found.getUses());
        assertEquals(OffsetDateTimeConverter.MINConverted(), found.getDueTime());
        assertEquals(invitation.getInvitingTeam(), found.getInvitingTeam());
    }

    @Test
    void shouldDeleteInvitation(){
        invitationService.deleteInvitation(invitation.getUUID());

        assertThrows(NotFoundException.class, () -> invitationService.getInvitationByUUID(invitation.getUUID()));
    }

    @Test
    void shouldUseInvitation(){
        InvitationRequestBody body1 = createInvitationRequestBody(UserRole.MANAGER, (short) 2, OffsetDateTimeConverter.MAXConverted());
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJohnD");
        Invitation invitation1 = invitationService.createInvitation(UserRole.OWNER,body1, team.getID());
        User user1 = userService.createUser(userRequestBody1);

        invitation = invitationService.useInvitation(invitation1.getUUID(), user1);
        team = (Team) teamService.getTeamWithUserRoles(team.getID());

        assertEquals(1, invitation.getUses());
        assertDoesNotThrow(() -> teamUserRoleService.getByUserAndTeam(user1.getID(), team.getID()));
        assertEquals(2, team.getTeammates().size());
    }

    @Test
    void shouldThrowForExpiredInvitation(){
        InvitationRequestBody body1 = createInvitationRequestBody(UserRole.MEMBER, (short)101, OffsetDateTimeConverter.MINConverted());
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJohnD");
        Invitation invitation1 = invitationService.createInvitation(UserRole.OWNER,body1, team.getID());
        User user1 = userService.createUser(userRequestBody1);

        assertThrows(BadRequestException.class, () -> invitationService.useInvitation(invitation1.getUUID(), user1));
    }

    @Test
    void shouldThrowForUnusableInvitation(){
        InvitationRequestBody body1 = createInvitationRequestBody(UserRole.MEMBER, (short)0, OffsetDateTimeConverter.MAXConverted());
        UserRequestBody userRequestBody1 = createUserRequestBody("NotJohnD");
        Invitation invitation1 = invitationService.createInvitation(UserRole.OWNER,body1, team.getID());
        User user1 = userService.createUser(userRequestBody1);

        assertThrows(BadRequestException.class, () -> invitationService.useInvitation(invitation1.getUUID(), user1));
    }

    @Test
    void shouldThrowForAlreadyJoinedInvitation(){
        InvitationRequestBody body1 = createInvitationRequestBody(UserRole.MEMBER, (short)101, OffsetDateTimeConverter.MAXConverted());
        Invitation invitation1 = invitationService.createInvitation(UserRole.OWNER,body1, team.getID());

        assertThrows(ConflictException.class, () -> invitationService.useInvitation(invitation1.getUUID(), user));
    }

    @Test
    void shouldGetInvitationsDTO(){
        InvitationRequestBody body1 = createInvitationRequestBody(UserRole.MEMBER, (short)101, OffsetDateTimeConverter.MAXConverted());
        Invitation invitation1 = invitationService.createInvitation(UserRole.OWNER,body1, team.getID());
        Set<InvitationManagerDTO> found = invitationService.getInvitationsDTO(team.getID());

        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(i -> i.equalsInvitation(invitation)));
        assertTrue(found.stream().anyMatch(i -> i.equalsInvitation(invitation1)));
    }
}
