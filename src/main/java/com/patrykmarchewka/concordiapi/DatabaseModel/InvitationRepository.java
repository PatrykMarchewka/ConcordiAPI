package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface InvitationRepository extends JpaRepository<Invitation,String> {
    Invitation findByUUID(String uuid);

    Set<Invitation> findAllByTeam(Team team);
}
