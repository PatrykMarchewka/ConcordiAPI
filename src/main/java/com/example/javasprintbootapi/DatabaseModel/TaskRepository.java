package com.example.javasprintbootapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {

    boolean existsByName(String name);
    Task findByName(String name);
    Task findById(long id);
}
