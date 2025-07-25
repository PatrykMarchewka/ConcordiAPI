package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TeamUserRoleRepository extends JpaRepository<TeamUserRole,Long> {

    TeamUserRole findByUserAndTeam(User user, Team team);

    Set<User> findAllByTeamAndUserRole(Team team, UserRole userRole);
}
