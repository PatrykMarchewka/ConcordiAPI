package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithCredentials;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithTeamRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithUserTasks;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByLogin(String login);

    Optional<User> findUserEntityByID(long id);

    @Query("""
    SELECT u FROM User u
    WHERE u.id = :id
""")
    Optional<UserIdentity> findUserByID(@Param("id") long id);

    @Query("""
        SELECT u from User u
        WHERE u.login = :login
""")
    Optional<UserWithCredentials> findUserWithCredentialsByLogin(@Param("login") String login);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN FETCH u.teamRoles tr
        LEFT JOIN FETCH tr.team
        WHERE u.id = :id
""")
    Optional<UserWithTeamRoles> findUserWithTeamRolesAndTeamsByID(@Param("id") long id);

    @Query("""
    SELECT u FROM User u
    LEFT JOIN FETCH u.userTasks
    WHERE u.id = :id
""")
    Optional<UserWithUserTasks> findUserWithUserTasksByID(@Param("id") long id);

    @EntityGraph(attributePaths = {"teamRoles", "teamRoles.team", "userTasks"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserEntityFullByID(@Param("id") long id);

    @EntityGraph(attributePaths = {"teamRoles", "teamRoles.team", "userTasks"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<UserFull> findUserFullByID(@Param("id") long id);
}
