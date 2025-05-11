package com.patrykmarchewka.concordiapi;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.InvitationRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private TeamService teamService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;

    @Transactional
    public Invitation createInvitation(Team team,InvitationRequestBody body){
        Invitation invitation = new Invitation();
        invitation.setTeam(team);
        invitation.setRole(body.getRole());
        invitation.setUses(body.getUses());
        invitation.setDueTime(body.getDueDate());
        return invitationRepository.save(invitation);
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
        return invitationRepository.findByUUID(UUID);
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
            if (body.getRole() != null){
                invitation.setRole(body.getRole());
            }
            if (body.getUses() != null){
                invitation.setUses(body.getUses());
            }
            if (body.getDueDate() != null){
                invitation.setDueTime(body.getDueDate());
            }
            return saveInvitation(invitation);
    }

}
