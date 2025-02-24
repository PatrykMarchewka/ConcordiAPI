package com.example.javasprintbootapi.DatabaseModel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    //For custom queries
}
