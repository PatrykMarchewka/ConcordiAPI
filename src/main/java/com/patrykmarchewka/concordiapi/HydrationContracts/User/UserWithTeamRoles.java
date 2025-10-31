package com.patrykmarchewka.concordiapi.HydrationContracts.User;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;

import java.util.Set;
import java.util.stream.Collectors;

public interface UserWithTeamRoles extends UserIdentity{
    Set<TeamUserRole> teamRoles();
    default Set<Team> getTeams(){return teamRoles().stream().map(TeamUserRole::getTeam).collect(Collectors.toUnmodifiableSet());}
    default Set<Long> getTeamsIDs(){return teamRoles().stream().map(teamUserRole -> teamUserRole.getTeam().getID()).collect(Collectors.toUnmodifiableSet());}
}
