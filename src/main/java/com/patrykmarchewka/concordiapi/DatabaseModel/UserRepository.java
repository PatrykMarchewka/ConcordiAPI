package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    //For custom queries
    boolean existsByLogin(String Login);
    
    User findByLogin(String login);

    boolean existsByNameAndLastName(String name, String lastName);

    User findByNameAndLastName(String name, String lastName);

    //@Query("SELECT u.Name, u.LastName FROM Users u;")
    //List<User> allUsers();
}
