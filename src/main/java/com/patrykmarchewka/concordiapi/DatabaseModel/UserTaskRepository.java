package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserTaskRepository extends JpaRepository<UserTask, Long> {
    @Deprecated
    Optional<UserTask> findByAssignedUserAndAssignedTask(User assignedUser, Task assignedTask);

    @Query("""
    SELECT ut FROM UserTask ut
    LEFT JOIN FETCH ut.assignedUser
    LEFT JOIN FETCH ut.assignedTask
    WHERE ut.assignedUser.id = :userID AND ut.assignedTask.id = :taskID
""")
    Optional<UserTask> findByAssignedUserIDAndAssignedTaskID(@Param("userID") long userID,@Param("taskID") long taskID);
}
