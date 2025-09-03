package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository  extends JpaRepository<Team,Long> {
    Optional<Team> findTeamById(Long id);
}
