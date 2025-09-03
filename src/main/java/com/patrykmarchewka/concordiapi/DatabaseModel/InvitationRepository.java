package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface InvitationRepository extends JpaRepository<Invitation,String> {
    Optional<Invitation> findByUUID(String uuid);

    Set<Invitation> getAllByInvitingTeam(Team invitingTeam);
}
