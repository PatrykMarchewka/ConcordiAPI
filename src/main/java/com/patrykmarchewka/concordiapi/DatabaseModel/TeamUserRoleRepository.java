package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TeamUserRoleRepository extends JpaRepository<TeamUserRole,Long> {
    Optional<TeamUserRole> findByUserAndTeam(User user, Team team);
    Set<TeamUserRole> getAllByTeamAndUserRole(Team team, UserRole userRole);
}
