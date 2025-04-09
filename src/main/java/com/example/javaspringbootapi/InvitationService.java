package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DTO.InvitationManagerDTO;
import com.example.javaspringbootapi.DatabaseModel.Invitation;
import com.example.javaspringbootapi.DatabaseModel.InvitationRepository;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Set;

@Service
public class InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private TeamService teamService;

    @Transactional
    public Invitation createInvitation(Team team, PublicVariables.UserRole role, short uses, ZonedDateTime dueTime){
        Invitation invitation = new Invitation();
        invitation.setTeam(team);
        invitation.setRole(role);
        invitation.setUses(uses);
        invitation.setDueTime(dueTime);
        return invitationRepository.save(invitation);
    }

    @Transactional
    public Invitation createInvitation(InvitationManagerDTO dto){
        Invitation invitation = new Invitation();
        invitation.setUUID(dto.getUUID());
        invitation.setTeam(dto.getTeam());
        invitation.setRole(dto.getRole());
        invitation.setUses(dto.getUses());
        invitation.setDueTime(dto.getDueTime());
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


}
