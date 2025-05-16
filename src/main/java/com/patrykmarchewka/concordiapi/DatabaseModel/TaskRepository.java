package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task,Long> {

    boolean existsByName(String name);
    Task findByName(String name);
    Task findById(long id);

    Optional<Task> findByIdAndTeam(long id, Team team);

    Set<Task> findByTeam(Team team);
}
