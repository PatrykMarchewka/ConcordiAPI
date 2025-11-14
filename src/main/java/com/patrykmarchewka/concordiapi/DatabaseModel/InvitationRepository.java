package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Invitation.InvitationWithTeam;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface InvitationRepository extends JpaRepository<Invitation,String> {
    @Query("""
    SELECT i from Invitation i
    WHERE i.UUID = :uuid
""")
    Optional<InvitationIdentity> findInvitationByUUID(@Param("uuid") String uuid);

    @Query("""
    SELECT i from Invitation i
    LEFT JOIN FETCH i.invitingTeam
    WHERE i.UUID = :uuid
""")
    Optional<InvitationWithTeam> findInvitationWithTeamByUUID(@Param("uuid") String uuid);

    @Query("""
    SELECT i from Invitation i
    LEFT JOIN FETCH i.invitingTeam
    WHERE i.invitingTeam.id = :teamID
""")
    Set<InvitationWithTeam> findAllInvitationsWithTeamByInvitingTeam(@Param("teamID") long teamID);

    @EntityGraph(attributePaths = {"invitingTeam"})
    @Query("SELECT i FROM Invitation i WHERE i.UUID = :uuid")
    Optional<InvitationFull> findInvitationFullByUUID(@Param("uuid") String uuid);
}
