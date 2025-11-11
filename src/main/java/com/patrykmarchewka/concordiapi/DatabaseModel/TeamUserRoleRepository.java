package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.patrykmarchewka.concordiapi.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface TeamUserRoleRepository extends JpaRepository<TeamUserRole,Long> {
    @Query("""
        SELECT t from TeamUserRole t
        LEFT JOIN FETCH t.team team
        LEFT JOIN FETCH t.user user
        WHERE team.id = :teamID AND user.id = :userID
""")
    Optional<TeamUserRole> findByUserAndTeam(@Param("userID") long userID, @Param("teamID") long teamID);

    @Query("""
        SELECT DISTINCT t from TeamUserRole t
        JOIN FETCH t.team team
        JOIN FETCH t.user user
        WHERE team.id = :id AND t.userRole = :userRole
""")
    Set<TeamUserRole> findAllByTeamAndUserRole(@Param("id") long teamID, @Param("userRole") UserRole role);
}
