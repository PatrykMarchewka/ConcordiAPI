package com.patrykmarchewka.concordiapi.Invitations;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.InvitationRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Invitations.Updaters.InvitationUpdatersService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Invitation createInvitation(InvitationRequestBody body, long teamID){
        Invitation invitation = new Invitation();
        Supplier<Team> teamSupplier = () -> teamService.getTeamByID(teamID);
        invitationUpdatersService.update(invitation,body, UpdateType.CREATE, teamSupplier);
        return saveInvitation(invitation);
    }

    /**
     * Edits Invitation completely with new values
     * @param invitation Invitation to edit
     * @param body InvitationRequestBody with new values
     * @return Invitation after changes
     */
    @Transactional
    public Invitation putInvitation(Invitation invitation, InvitationRequestBody body, long teamID){
        Supplier<Team> teamSupplier = () -> teamService.getTeamByID(teamID);
        invitationUpdatersService.update(invitation,body,UpdateType.PUT, teamSupplier);
        return saveInvitation(invitation);
    }

    /**
     * Edits Invitation with new values
     * @param invitation Invitation to edit
     * @param body InvitationRequestBody with new values
     * @return Invitation after changes
     */
    @Transactional
    public Invitation patchInvitation(Invitation invitation, InvitationRequestBody body, long teamID){
        Supplier<Team> teamSupplier = () -> teamService.getTeamByID(teamID);
        invitationUpdatersService.update(invitation,body,UpdateType.PATCH, teamSupplier);
        return saveInvitation(invitation);
    }

    /**
     * Uses invitation and adds user to the team
     * @param invitation Invitation to use
     * @param user User using the invitation
     * @throws ConflictException Thrown when user tries to join a team they are already part of
     * @throws BadRequestException Thrown when user can't join the specified team due to invitation being no longer usable
     */
    @Transactional
    public Invitation useInvitation(Invitation invitation,User user){
        Team team = teamService.getTeamWithUserRoles(invitation.getInvitingTeam());
        if (team.checkUser(user)){
            throw new ConflictException("You are already part of that team!");
        }
        invitation.useOne();
        teamService.addUser(team, user, invitation.getRole());
        return saveInvitation(invitation);
    }

    /**
     * Saves changes to invitation
     * @param invitation Invitation to save
     * @return Invitation after changes
     */
    public Invitation saveInvitation(Invitation invitation){
        return invitationRepository.save(invitation);
    }

    /**
     * Gets invitation with provided UUID
     * @param UUID UUID to check for
     * @return Invitation with specified UUID
     * @throws NotFoundException Thrown when no invitation can be found with given UUID
     */
    public Invitation getInvitationByUUID(String UUID){
        return invitationRepository.findByUUID(UUID).orElseThrow(NotFoundException::new);
    }

    /**
     * Deletes invitation completely
     * @param invitation Invitation to delete
     */
    public void deleteInvitation(Invitation invitation){
        invitationRepository.delete(invitation);
    }

    /**
     * Gets all invitations in a team and converts them to DTO
     * @param team Team to check in
     * @return Set of InvitationDTO for all invitations in the team
     */
    @Transactional(readOnly = true)
    public Set<InvitationManagerDTO> getInvitationsDTO(Team team){
            Set<InvitationManagerDTO> invitations = new HashSet<>();
            for (Invitation inv : invitationRepository.getAllByInvitingTeam(team)){
                invitations.add(new InvitationManagerDTO(inv));
            }
            return invitations;
    }

    /**
     * Deletes everything and flushes
     */
    public void deleteAll(){
        invitationRepository.deleteAll();
        invitationRepository.flush();
    }
}
