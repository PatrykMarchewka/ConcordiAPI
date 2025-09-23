package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByLogin(String Login);
    
    Optional<User> findByLogin(String login);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.teamRoles tr
        LEFT JOIN FETCH tr.team
        WHERE u.id = :id
""")
    Optional<User>findUserWithTeamRolesAndTeamsByID(@Param("id") long id);

    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.userTasks
    WHERE u.id = :id
""")
    Optional<User>findUserWithUserTasksByID(@Param("id") long id);

    @EntityGraph(attributePaths = {"teamRoles", "teamRoles.team", "userTasks"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserFullByID(@Param("id") long id);
}
