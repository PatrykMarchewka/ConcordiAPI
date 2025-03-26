package com.example.javasprintbootapi.DatabaseModel;

import com.example.javasprintbootapi.PublicVariables;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TeamUserRoleRepository extends JpaRepository<TeamUserRole,Long> {

    TeamUserRole findByUserAndTeam(User user, Team team);

    Set<TeamUserRole> findAllByTeamAndUserRole(Team team, PublicVariables.UserRole userRole);
}
