package com.example.javasprintbootapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository  extends JpaRepository<Team,Long> {
    Team getTeamByName(String name);

    Team getTeamById(long id);
}
