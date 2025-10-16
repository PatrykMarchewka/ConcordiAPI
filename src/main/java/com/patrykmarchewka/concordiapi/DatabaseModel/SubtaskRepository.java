package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.HydrationContracts.Subtask.SubtaskIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubtaskRepository extends JpaRepository<Subtask,Long> {

    Optional<Subtask> findSubtaskByIdAndTaskId(long subtaskID,long taskID);

    @Query("""
    SELECT s FROM Subtask s
    LEFT JOIN FETCH s.task
    WHERE s.id = :id AND s.task.id = :taskID
""")
    Optional<SubtaskIdentity> findSubtaskIdentityByIDAndTaskID(@Param("id") long subtaskID,@Param("taskID") long taskID);
}
