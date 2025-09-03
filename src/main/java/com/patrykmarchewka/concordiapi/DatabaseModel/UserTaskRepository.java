package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    Optional<UserTask> findByAssignedUserAndAssignedTask(User assignedUser, Task assignedTask);
}
