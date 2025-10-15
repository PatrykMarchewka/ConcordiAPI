package com.patrykmarchewka.concordiapi.HydrationContracts.Team;

import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;

import java.util.Set;
import java.util.stream.Collectors;

public interface TeamWithUserRoles extends TeamIdentity{
    Set<TeamUserRole> getUserRoles();
    default Set<User> getTeammates(){ return getUserRoles().stream().map(TeamUserRole::getUser).collect(Collectors.toUnmodifiableSet()); }
    default boolean checkUser(final long ID){ return getUserRoles().stream().anyMatch(ur -> ur.getUser().getID() == ID); }
}
