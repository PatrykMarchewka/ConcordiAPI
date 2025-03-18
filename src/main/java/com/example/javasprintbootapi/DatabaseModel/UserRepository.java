package com.example.javasprintbootapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    //For custom queries

    boolean existsByName(String Name);

    boolean existsByLogin(String Login);


    //@Query("SELECT u.Name, u.LastName FROM User u WHERE u.ID NOT IN (SELECT DISTINCT user FROM User user JOIN Task t)")
    //List<User> findUsersWithoutTasks();
}
