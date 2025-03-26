package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.Team;
import com.example.javasprintbootapi.DatabaseModel.TeamUserRole;
import com.example.javasprintbootapi.DatabaseModel.TeamUserRoleRepository;
import com.example.javasprintbootapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TeamUserRoleService {
    @Autowired
    private TeamUserRoleRepository teamUserRoleRepository;

    public TeamUserRole getByUserAndTeam(User user, Team team){
        return teamUserRoleRepository.findByUserAndTeam(user,team);
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

    public Set<User> getAllRole(Team team, PublicVariables.UserRole role){
        Set<User> temp = new HashSet<>();
        for (TeamUserRole tmr : teamUserRoleRepository.findAllByTeamAndUserRole(team,role)){
            temp.add(tmr.getUser());
        }
        return temp;
    }
}
