package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TeamUserRoleRepository teamUserRoleRepository;

    public Team createTeam(String name, User user){
        Team team = new Team();
        team.setName(name);
        TeamUserRole tmr = new TeamUserRole();
        tmr.setTeam(team);
        tmr.setUser(user);
        tmr.setUserRole(PublicVariables.UserRole.ADMIN);
        teamUserRoleRepository.save(tmr);
        return teamRepository.save(team);
    }

    public long getID(Team team){
        return team.getId();
    }

    public Team getTeamByID(long id){
        return teamRepository.getTeamById(id);
    }
}
