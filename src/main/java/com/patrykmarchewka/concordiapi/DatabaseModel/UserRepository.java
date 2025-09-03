package com.patrykmarchewka.concordiapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByLogin(String Login);
    
    Optional<User> findByLogin(String login);

}
