package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.UserRole;

public interface TeamUserRoleTestHelper {
    default TeamUserRole createTeamUserRole(Team team, User user, UserRole userRole, TeamUserRoleRepository teamUserRoleRepository){
        TeamUserRole teamUserRole = new TeamUserRole();
        teamUserRole.setTeam(team);
        teamUserRole.setUser(user);
        teamUserRole.setUserRole(userRole);

        return teamUserRoleRepository.save(teamUserRole);
    }
}
