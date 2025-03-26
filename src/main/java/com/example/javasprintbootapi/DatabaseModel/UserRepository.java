package com.example.javasprintbootapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    //For custom queries

    boolean existsByName(String Name);

    boolean existsByLogin(String Login);
    
    User findByLogin(String login);

    boolean existsByLoginAndTeam_Name(String login, String teamName);

    User findByLoginAndTeam_Name(String login, String teamName);

    boolean existsByLoginAndTeam_Id(String login, long teamId);

    User findByLoginAndTeam_Id(String login, long teamId);

    //@Query("SELECT u.Name, u.LastName FROM Users u;")
    //List<User> allUsers();
}
