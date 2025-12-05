package com.patrykmarchewka.concordiapi.Invitations;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.InvitationRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithTeamRoles;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationUpdatersService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final TeamService teamService;
    private final InvitationUpdatersService invitationUpdatersService;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository, TeamService teamService, InvitationUpdatersService invitationUpdatersService){
        this.invitationRepository = invitationRepository;
        this.teamService = teamService;
        this.invitationUpdatersService = invitationUpdatersService;
    }
    

    /**
     * Creates new invitation with specified data
     * @param body InvitationRequestBody with information to update
     * @return Created invitation
     */
    @Transactional
    public Invitation createInvitation(@NonNull final InvitationRequestBody body, final long teamID){
        Invitation invitation = new Invitation();
        Supplier<Team> teamSupplier = () -> (Team)teamService.getTeamByID(teamID);
        invitationUpdatersService.createUpdate(invitation,body,teamSupplier);
        return saveInvitation(invitation);
    }

    /**
     * Edits Invitation completely with new values
     * @param invitationID UUID of Invitation to edit
     * @param body InvitationRequestBody with new values
     * @return Invitation after changes
     */
    @Transactional
    public InvitationFull putInvitation(@NonNull final String invitationID, @NonNull final InvitationRequestBody body){
        Invitation invitation = (Invitation) getInvitationFullByUUID(invitationID);
        invitationUpdatersService.putUpdate(invitation, body);
        return saveInvitation(invitation);
    }

    /**
     * Edits Invitation with new values
     * @param invitationID UUID of Invitation to edit
     * @param body InvitationRequestBody with new values
     * @return Invitation after changes
     */
    @Transactional
    public InvitationFull patchInvitation(@NonNull final String invitationID, @NonNull final InvitationRequestBody body){
        Invitation invitation = (Invitation) getInvitationFullByUUID(invitationID);
        invitationUpdatersService.patchUpdate(invitation, body);
        return saveInvitation(invitation);
    }

    /**
     * Uses invitation and adds user to the team
     * @param invitationID ID of Invitation to use
     * @param user User using the invitation
     * @throws ConflictException Thrown when user tries to join a team they are already part of
     * @throws BadRequestException Thrown when user can't join the specified team due to invitation being no longer usable
     */
    @Transactional
    public InvitationWithTeam useInvitation(@NonNull final String invitationID, @NonNull final UserWithTeamRoles user){
        Invitation invitation = (Invitation) getInvitationWithTeamByUUID(invitationID);
        Team team = (Team) teamService.getTeamWithUserRoles(invitation.getInvitingTeam().getID());
        invitation.useOne();
        teamService.addUser(team.getID(), user, invitation.getRole());
        return saveInvitation(invitation);
    }

    /**
     * Saves changes to invitation
     * @param invitation Invitation to save
     * @return Invitation after changes
     */
    public Invitation saveInvitation(@NonNull final Invitation invitation){
        return invitationRepository.save(invitation);
    }

    /**
     * Deletes invitation completely
     * @param invID ID of Invitation to delete
     */
    public void deleteInvitation(@NonNull final String invID){
        Invitation invitation = (Invitation) getInvitationByUUID(invID);
        invitationRepository.delete(invitation);
    }

    /**
     * Gets all invitations in a team and converts them to DTOs
     * @param teamID ID of Team to check in
     * @return Set of InvitationManagerDTO for all invitations in the team
     */
    @Transactional(readOnly = true)
    public Set<InvitationManagerDTO> getInvitationsDTO(final long teamID){
        Set<InvitationManagerDTO> invitations = new HashSet<>();
        for (InvitationWithTeam inv : getAllInvitationsWithTeamByInvitingTeamID(teamID)){
            invitations.add(new InvitationManagerDTO(inv));
        }
        return invitations;
    }

    /**
     * Gets invitation with provided UUID
     * @param UUID UUID to check for
     * @return InvitationIdentity with specified UUID
     * @throws NotFoundException Thrown when no invitation can be found with given UUID
     */
    public InvitationIdentity getInvitationByUUID(@NonNull final String UUID){
        return invitationRepository.findInvitationByUUID(UUID).orElseThrow(NotFoundException::new);
    }

    public InvitationWithTeam getInvitationWithTeamByUUID(@NonNull final String UUID){
        return invitationRepository.findInvitationWithTeamByUUID(UUID).orElseThrow(NotFoundException::new);
    }

    public Set<InvitationWithTeam> getAllInvitationsWithTeamByInvitingTeamID(final long teamID){
        Set<InvitationWithTeam> result = invitationRepository.findAllInvitationsWithTeamByInvitingTeam(teamID);
        if (result.isEmpty()){
            throw new NotFoundException(String.format("Couldnt find invitations with inviting team if of %d", teamID));
        }
        return result;
    }

    public InvitationFull getInvitationFullByUUID(@NonNull final String UUID){
        return invitationRepository.findInvitationFullByUUID(UUID).orElseThrow(NotFoundException::new);
    }

    /**
     * Deletes everything and flushes
     */
    public void deleteAll(){
        invitationRepository.deleteAll();
        invitationRepository.flush();
    }
}
