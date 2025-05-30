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

    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;




    final List<InvitationUpdater> updaters(){
        return List.of(
                new InvitationRoleUpdater(),
                new InvitationUsesUpdater(),
                new InvitationDueTimeUpdater()
        );
    }

    void applyCreateUpdates(Invitation invitation, InvitationRequestBody body){
        for (InvitationUpdater updater : updaters()){
            if (updater instanceof InvitationCREATEUpdater createUpdater){
                createUpdater.CREATEUpdate(invitation, body);
            }
        }
    }

    void applyPutUpdates(Invitation invitation, InvitationRequestBody body){
        for (InvitationUpdater updater : updaters()){
            if (updater instanceof  InvitationPUTUpdater putUpdater){
                putUpdater.PUTUpdate(invitation, body);
            }
        }
    }

    void applyPatchUpdates(Invitation invitation, InvitationRequestBody body){
        for (InvitationUpdater updater : updaters()){
            if (updater instanceof InvitationPATCHUpdater patchUpdater){
                patchUpdater.PATCHUpdate(invitation, body);
            }
        }
    }




    @Transactional
    public Invitation createInvitation(Team team,InvitationRequestBody body){
        Invitation invitation = new Invitation();
        invitation.setTeam(team);
        applyCreateUpdates(invitation,body);
        return saveInvitation(invitation);
    }

    @Transactional(rollbackFor = Exception.class)
    public void useInvitation(Invitation invitation,User user) throws Exception {
        invitation.useOne();
        invitationRepository.save(invitation);
        teamService.addUser(invitation.getTeam(), user,invitation.getRole());
    }

    public Invitation saveInvitation(Invitation invitation){
        return invitationRepository.save(invitation);
    }

    public Invitation getInvitationByUUID(String UUID){
        return invitationRepository.findByUUID(UUID).orElseThrow(NotFoundException::new);
    }

    public Set<Invitation> getAllInvitations(Team team){
        return invitationRepository.findAllByTeam(team);
    }

    public void deleteInvitation(Invitation invitation){
        invitationRepository.delete(invitation);
    }



    public Set<InvitationManagerDTO> getInvitationsDTO(Team team){
            Set<InvitationManagerDTO> invitations = new HashSet<>();
            for (Invitation inv : invitationRepository.findAllByTeam(team)){
                invitations.add(new InvitationManagerDTO(inv,teamUserRoleService));
            }
            return invitations;
    }

    @Transactional
    public Invitation partialUpdate(Invitation invitation, InvitationRequestBody body){
            applyPatchUpdates(invitation, body);
            return saveInvitation(invitation);
    }

    @Transactional
    public Invitation putUpdate(Invitation invitation, InvitationRequestBody body){
        applyPutUpdates(invitation, body);
        return saveInvitation(invitation);
    }

}
