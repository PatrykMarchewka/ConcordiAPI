package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRoleRepository;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.function.BiPredicate;

@Service
public class TeamUserRoleService {
    @Autowired
    private TeamUserRoleRepository teamUserRoleRepository;

    @Transactional
    public TeamUserRole createTMR(User user, Team team, PublicVariables.UserRole role){
        TeamUserRole tmr = new TeamUserRole();
        tmr.setTeam(team);
        tmr.setUser(user);
        tmr.setUserRole(role);
        return saveTMR(tmr);
    }

    public TeamUserRole getByUserAndTeam(User user, Team team){
        return teamUserRoleRepository.findByUserAndTeam(user,team);
    }

    public void deleteTMR(TeamUserRole tmr){
        teamUserRoleRepository.delete(tmr);
    }

    public PublicVariables.UserRole getRole(User user, Team team){
        TeamUserRole tmr = getByUserAndTeam(user,team);
        return tmr.getUserRole();
    }

    public void setRole(User user, Team team, PublicVariables.UserRole role){
        TeamUserRole tmr = getByUserAndTeam(user,team);
        tmr.setUserRole(role);
        saveTMR(tmr);
    }

    /**
     * TODO: Replace with the one below
     * @param team
     * @param role
     * @return
     */
    @Transactional(readOnly = true)
    public Set<User> getAllRole(Team team, PublicVariables.UserRole role){
        return getAllByTeamAndUserRole(team,role);
    }

    public Set<User> getAllByTeamAndUserRole(Team team, PublicVariables.UserRole role){
        return teamUserRoleRepository.findAllByTeamAndUserRole(team,role);
    }

    public TeamUserRole saveTMR(TeamUserRole tmr){
        return teamUserRoleRepository.save(tmr);
    }



    public BiPredicate<PublicVariables.UserRole, PublicVariables.UserRole> checkRoles = (mine,other) -> mine.compareTo(other) >= 0;

    public int checkRoles(PublicVariables.UserRole mine, PublicVariables.UserRole other){
        return mine.compareTo(other);
    }



}
