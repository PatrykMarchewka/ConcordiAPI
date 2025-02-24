package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.User;
import com.example.javasprintbootapi.DatabaseModel.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    public User getUserByID(Long id){
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()){
            return user.get();
        }
        else{
            return null;
        }
    }

    

}
