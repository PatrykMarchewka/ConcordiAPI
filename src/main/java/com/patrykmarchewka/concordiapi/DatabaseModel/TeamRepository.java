package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithInvitations;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithTasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository  extends JpaRepository<Team,Long> {
    Optional<Team> findTeamById(long id);

    @Query("""
    SELECT t FROM Team t
    LEFT JOIN FETCH t.userRoles ur
    LEFT JOIN FETCH ur.user
    WHERE t.id = :id
""")
    Optional<Team> findTeamWithUserRolesAndUsersByID(@Param("id") long id);

    @Query("""
    SELECT t FROM Team t
    LEFT JOIN FETCH t.teamTasks
    WHERE t.id = :id
""")
    Optional<TeamWithTasks> findTeamWithTeamTasksByID(@Param("id") long id);

    @Query("""
    SELECT t FROM Team t
    LEFT JOIN FETCH t.invitations
    WHERE t.id = :id
""")
    Optional<TeamWithInvitations> findTeamWithInvitationsByID(@Param("id") long id);

    @Query("""
    SELECT t FROM Team t
    LEFT JOIN FETCH t.userRoles ur
    LEFT JOIN FETCH ur.user
    LEFT JOIN FETCH t.teamTasks
    WHERE t.id = :id
""")
    Optional<TeamWithUserRolesAndTasks> findTeamWithUserRolesAndTasksByID(@Param("id") long id);

    @EntityGraph(attributePaths = {"userRoles","userRoles.user","teamTasks","invitations"})
    @Query("SELECT t FROM Team t WHERE t.id = :id")
    Optional<Team> findTeamFullByID(@Param("id") long id);
}
