package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskWithSubtasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskWithUserTasks;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface TaskRepository extends JpaRepository<Task,Long> {
    @Query("""
        SELECT t FROM Task t
        LEFT JOIN FETCH t.assignedTeam
        WHERE t.id = :id AND t.assignedTeam.id = :teamID
""")
    Optional<TaskIdentity> findTaskByIDAndAssignedTeamID(@Param("id") long id, @Param("teamID") long teamID);

    @Query("""
    SELECT t FROM Task t
    LEFT JOIN FETCH t.assignedTeam
    WHERE t.assignedTeam.id = :teamID
""")
    Set<TaskIdentity> getByAssignedTeamID(@Param("teamID") long teamID);

    @Query("""
    SELECT DISTINCT t FROM Task t
    JOIN t.userTasks ut
    LEFT JOIN FETCH t.assignedTeam
    WHERE t.assignedTeam.id = :teamID AND ut.assignedUser.id = :userID
""")
    Set<TaskIdentity> getByAssignedTeamIDAndAssignedUserID(@Param("teamID") long teamID, @Param("userID") long userID);

    @Query("""
    SELECT t FROM Task t
    LEFT JOIN FETCH t.userTasks ut
    LEFT JOIN FETCH ut.assignedUser
    WHERE t.id = :id AND t.assignedTeam.id = :teamID
""")
    Optional<TaskWithUserTasks> findTaskWithUserTasksByIDAndAssignedTeamID(@Param("id") long id, @Param("teamID") long teamID);

    @Query("""
    SELECT t FROM Task t
    LEFT JOIN FETCH t.subtasks
    WHERE t.id = :id AND t.assignedTeam.id = :teamID
""")
    Optional<TaskWithSubtasks> findTaskWithSubtasksByIDAndAssignedTeamID(@Param("id") long id, @Param("teamID") long teamID);

    @EntityGraph(attributePaths = {"userTasks", "userTasks.assignedUser", "subtasks", "assignedTeam"})
    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.assignedTeam.id = :teamID")
    Optional<TaskFull> findTaskFullByIDAndAssignedTeamID(@Param("id") long id, @Param("teamID") long teamID);

    /// Legacy
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
