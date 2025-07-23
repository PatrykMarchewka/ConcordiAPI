package com.patrykmarchewka.concordiapi.Invitations;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.InvitationRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final TeamService teamService;
    private final TeamUserRoleService teamUserRoleService;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository, TeamService teamService, TeamUserRoleService teamUserRoleService){
        this.invitationRepository = invitationRepository;
        this.teamService = teamService;
        this.teamUserRoleService = teamUserRoleService;
    }

    /**
     * List of updaters, used in {@link #applyCreateUpdates(Invitation, InvitationRequestBody)}, {@link #applyPutUpdates(Invitation, InvitationRequestBody)}, {@link #applyPatchUpdates(Invitation, InvitationRequestBody)}
     * @return List of updaters to execute
     */
    final List<InvitationUpdater> updaters(){
        return List.of(
                new InvitationTeamUpdater(teamService),
                new InvitationRoleUpdater(),
                new InvitationUsesUpdater(),
                new InvitationDueTimeUpdater()
        );
    }
    
    /**
     * Applies CREATE updates for the Invitation given the InvitationRequestBody, should only be called from {@link #createInvitation(InvitationRequestBody)}
     * @param invitation Invitation to create
     * @param body InvitationRequestBody with information to update
     */
    void applyCreateUpdates(Invitation invitation, InvitationRequestBody body){
        for (InvitationUpdater updater : updaters()){
            if (updater instanceof InvitationCREATEUpdater createUpdater){
                createUpdater.CREATEUpdate(invitation, body);
            }
        }
    }

    /**
     * Applies PUT updates for the Invitation given the InvitationRequestBody, should only be called from {@link #putUpdate(Invitation, InvitationRequestBody)}
     * @param invitation Invitation to edit
     * @param body InvitationRequestBody with information to update
     */
    void applyPutUpdates(Invitation invitation, InvitationRequestBody body){
        for (InvitationUpdater updater : updaters()){
            if (updater instanceof  InvitationPUTUpdater putUpdater){
                putUpdater.PUTUpdate(invitation, body);
            }
        }
    }

    /**
     * Applies PATCH updates for the Invitation given the InvitationRequestBody, should only be called from {@link #partialUpdate(Invitation, InvitationRequestBody)}
     * @param invitation Invitation to edit
     * @param body InvitationRequestBody with information to update
     */
    void applyPatchUpdates(Invitation invitation, InvitationRequestBody body){
        for (InvitationUpdater updater : updaters()){
            if (updater instanceof InvitationPATCHUpdater patchUpdater){
                patchUpdater.PATCHUpdate(invitation, body);
            }
        }
    }

    /**
     * Creates new invitation with specified data
     * @param body InvitationRequestBody with information to update
     * @return Created invitation
     */
    @Transactional
    public Invitation createInvitation(InvitationRequestBody body){
        Invitation invitation = new Invitation();
        applyCreateUpdates(invitation,body);
        return saveInvitation(invitation);
    }

    /**
     * Uses invitation and adds user to the team
     * @param invitation Invitation to use
     * @param user User using the invitation
     * @throws Exception Thrown when user can't join the specified team (Invitation expired?)
     */
    @Transactional(rollbackFor = Exception.class)
    public void useInvitation(Invitation invitation,User user) throws Exception {
        invitation.useOne();
        saveInvitation(invitation);
        teamService.addUser(invitation.getTeam(), user,invitation.getRole());
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
     * Returns all invitations in the team
     * @param team Team to check for
     * @return All Invitations for the given team
     */
    public Set<Invitation> getAllInvitations(Team team){
        return invitationRepository.findAllByTeam(team);
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
    public Set<InvitationManagerDTO> getInvitationsDTO(Team team){
            Set<InvitationManagerDTO> invitations = new HashSet<>();
            for (Invitation inv : invitationRepository.findAllByTeam(team)){
                invitations.add(new InvitationManagerDTO(inv,teamUserRoleService));
            }
            return invitations;
    }

    /**
     * Edits Invitation with new values
     * @param invitation Invitation to edit
     * @param body InvitationRequestBody with new values
     * @return Invitation after changes
     */
    @Transactional
    public Invitation partialUpdate(Invitation invitation, InvitationRequestBody body){
            applyPatchUpdates(invitation, body);
            return saveInvitation(invitation);
    }

    /**
     * Edits Invitation completely with new values
     * @param invitation Invitation to edit
     * @param body InvitationRequestBody with new values
     * @return Invitation after changes
     */
    @Transactional
    public Invitation putUpdate(Invitation invitation, InvitationRequestBody body){
        applyPutUpdates(invitation, body);
        return saveInvitation(invitation);
    }

}
