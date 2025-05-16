package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubtaskRepository extends JpaRepository<Subtask,Long> {

    Optional<Subtask> findSubtaskByIdAndTaskId(long subtaskID,long taskID);

    boolean existsByIdAndTask(long id, Task task);
}
