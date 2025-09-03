package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task,Long> {
    Optional<Task> findByIdAndAssignedTeam(Long id, Team assignedTeam);
    Set<Task> getByAssignedTeam(Team assignedTeam);
}
