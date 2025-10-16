package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface TeamUserRoleRepository extends JpaRepository<TeamUserRole,Long> {
    Optional<TeamUserRole> findByUserAndTeam(User user, Team team);
    Set<TeamUserRole> getAllByTeamAndUserRole(Team team, UserRole userRole);



    @Query("""
        SELECT t from TeamUserRole t
        LEFT JOIN FETCH t.team team
        LEFT JOIN FETCH t.user user
        WHERE team.id = :teamID AND user.id = :userID
""")
    Optional<TeamUserRole> findByUserAndTeam(@Param("teamID") long teamID, @Param("userID") long userID);
}
