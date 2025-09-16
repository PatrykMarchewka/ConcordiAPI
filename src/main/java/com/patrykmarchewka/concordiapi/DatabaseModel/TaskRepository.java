package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task,Long> {
    Optional<Task> findByIdAndAssignedTeam(long id, Team assignedTeam);
    Set<Task> getByAssignedTeam(Team assignedTeam);

    @Query("""
    SELECT t FROM Task t
    LEFT JOIN FETCH t.userTasks ut
    LEFT JOIN FETCH ut.assignedUser
    WHERE t.id = :id AND t.assignedTeam = :team
""")
    Optional<Task> findTaskWithUserTasksByIDAndAssignedTeam(@Param("id") long id, @Param("team") Team assignedTeam);

    @Query("""
    SELECT t FROM Task t
    LEFT JOIN FETCH t.subtasks
    WHERE t.id = :id AND t.assignedTeam = :team
""")
    Optional<Task> findTaskWithSubtasksByIDAndAssignedTeam(@Param("id") long id, @Param("team") Team assignedTeam);

    @EntityGraph(attributePaths = {"userTasks", "userTasks.assignedUser", "subtasks", "assignedTeam"})
    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.assignedTeam = :team")
    Optional<Task> findTaskFullByID(@Param("id") long id, @Param("team") Team assignedTeam);
}
