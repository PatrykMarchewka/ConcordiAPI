package com.example.javaspringbootapi;

import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.TeamUserRole;
import com.example.javaspringbootapi.DatabaseModel.TeamUserRoleRepository;
import com.example.javaspringbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class TeamUserRoleService {
    @Autowired
    private TeamUserRoleRepository teamUserRoleRepository;

    public TeamUserRole getByUserAndTeam(User user, Team team){
        return teamUserRoleRepository.findByUserAndTeam(user,team);
    }

    public void deleteTMR(TeamUserRole tmr){
        teamUserRoleRepository.delete(tmr);
    }

    public PublicVariables.UserRole getRole(User user, Team team){
        TeamUserRole tmr = teamUserRoleRepository.findByUserAndTeam(user,team);
        return tmr.getUserRole();
    }

    public void setRole(User user, Team team, PublicVariables.UserRole role){
        TeamUserRole tmr = teamUserRoleRepository.findByUserAndTeam(user,team);
        tmr.setUserRole(role);
        teamUserRoleRepository.save(tmr);
    }

    @Transactional(readOnly = true)
    public Set<User> getAllRole(Team team, PublicVariables.UserRole role){
        Set<User> temp = new HashSet<>();
        for (TeamUserRole tmr : teamUserRoleRepository.findAllByTeamAndUserRole(team,role)){
            temp.add(tmr.getUser());
        }
        return temp;
    }

    public TeamUserRole saveTMR(TeamUserRole tmr){
        return teamUserRoleRepository.save(tmr);
    }
}
