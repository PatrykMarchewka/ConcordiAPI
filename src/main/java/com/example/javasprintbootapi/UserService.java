package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamUserRoleService teamUserRoleService;


    public User getUserByID(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByLogin(String Login){
        return userRepository.findByLogin(Login);
    }

    public User getUserByLoginAndPassword(String login, String password){
        User user = userRepository.findByLogin(login);
        if (Passwords.CheckPasswordBCrypt(password,user.getPassword())){
            return user;
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

    public boolean checkIfUserExistsByLogin(String login){
        return userRepository.existsByLogin(login);
    }

    public User createUser(String login, String password, String name, String lastName){
        User user = new User();
        user.setLogin(login);
        user.setPassword(Passwords.HashPasswordBCrypt(password));
        user.setName(name);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    public void banUserByID(long id, Team team){
        User user = userRepository.findById(id).orElseThrow();
        teamUserRoleService.setRole(user,team, PublicVariables.UserRole.BANNED);
    }

    //TODO: change so it only delets from the team
    public void deleteUser(User user){
        userRepository.delete(user);
    }

    public void deleteUserByID(long id){
        userRepository.deleteById(id);
    }

    

}
