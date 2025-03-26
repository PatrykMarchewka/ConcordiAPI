package com.example.javasprintbootapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TaskRepository extends JpaRepository<Task,Long> {

    boolean existsByName(String name);
    Task findByName(String name);
    Task findById(long id);

    Set<Task> findByTeam(Team team);
}
