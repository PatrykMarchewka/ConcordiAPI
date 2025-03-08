package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.Task;
import com.example.javasprintbootapi.DatabaseModel.TaskRepository;
import com.example.javasprintbootapi.DatabaseModel.User;
import com.example.javasprintbootapi.DatabaseModel.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<User> getUsersWithoutTasks(TaskRepository taskRepository){
        List<User> temp = new ArrayList<>();
        HashSet<User> assignedUsers = new HashSet<>();
        for (Task task : taskRepository.findAll()){
            assignedUsers.addAll(task.getUsers());
        }

        for (User user : this.getAllUsers()){
            if (!assignedUsers.contains(user)){
                temp.add(user);
            }
        }
        return temp;

    }

    public boolean checkIfAdminExists(){
        return userRepository.existsByLogin("admin");
    }

    public User createUser(String login, String password, String name, String lastName, PublicVariables.UserStatus status){
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setName(name);
        user.setLastName(lastName);
        user.setStatus(status);

        return userRepository.save(user);
    }

    public void deleteUser(User user){
        userRepository.delete(user);
    }

    public void deleteUserByID(long id){
        userRepository.deleteById(id);
    }

    

}
