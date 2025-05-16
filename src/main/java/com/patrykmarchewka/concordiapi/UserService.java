package com.patrykmarchewka.concordiapi;

import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamUserRoleService teamUserRoleService;


    public User getUserByID(Long id){
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
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

    public User getUserByNameAndLastName(String name){
        String answer[] = name.split(" ");
        return userRepository.findByNameAndLastName(answer[0], answer[1]);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
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

    public boolean checkIfUserExistsInATeam(User user, Team team){
        return user.getTeams().contains(team) && team.getTeammates().contains(user);
    }

    public boolean checkIfUserExistsByID(long ID){
        return userRepository.existsById(ID);
    }

    public boolean checkIfUserExistsByNameAndLastName(String name){
        String answer[] = name.split(" ");
        return userRepository.existsByNameAndLastName(answer[0], answer[1]);
    }

    @Transactional
    public User createUser(String login, String password, String name, String lastName){
        User user = new User();
        user.setLogin(login);
        user.setPassword(Passwords.HashPasswordBCrypt(password));
        user.setName(name);
        user.setLastName(lastName);
        return userRepository.save(user);
    }


    public void deleteUser(User user){
        userRepository.delete(user);
    }

    public void deleteUserByID(long id){
        userRepository.deleteById(id);
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }


}
