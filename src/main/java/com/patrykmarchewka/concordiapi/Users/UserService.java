package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestLogin;
import com.patrykmarchewka.concordiapi.DatabaseModel.*;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Exceptions.WrongCredentialsException;
import com.patrykmarchewka.concordiapi.Passwords;
import com.patrykmarchewka.concordiapi.PublicVariables;
import com.patrykmarchewka.concordiapi.RoleRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final RoleRegistry roleRegistry;

    @Autowired
    public UserService(UserRepository userRepository, RoleRegistry roleRegistry){
        this.userRepository = userRepository;
        this.roleRegistry = roleRegistry;
    }
    
    final List<UserUpdater> updaters(){
        return List.of(
                new UserLoginUpdater(this),
                new UserPasswordUpdater(),
                new UserNameUpdater(),
                new UserLastNameUpdater()
        );
    }

    private void applyCreateUpdates(User user, UserRequestBody body){
        for (UserUpdater updater : updaters()){
            if (updater instanceof UserCREATEUpdater createUpdater){
                createUpdater.CREATEUpdate(user,body);
            }
        }
    }

    private void applyPutUpdates(User user, UserRequestBody body){
        for (UserUpdater updater : updaters()){
            if (updater instanceof UserPUTUpdater putUpdater){
                putUpdater.PUTUpdate(user, body);
            }
        }
    }

    private void applyPatchUpdates(User user, UserRequestBody body){
        for (UserUpdater updater : updaters()){
            if (updater instanceof  UserPATCHUpdater patchUpdater){
                patchUpdater.PATCHUpdate(user, body);
            }
        }
    }


    public User getUserByID(Long id){
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public User getUserByLogin(String Login){
        return userRepository.findByLogin(Login).orElseThrow(NotFoundException::new);
    }

    /**
     * TODO: Replace with one below
     * @param login
     * @param password
     * @return
     */
    public User getUserByLoginAndPassword(String login, String password){
        User user = getUserByLogin(login);
        if (!Passwords.CheckPasswordBCrypt(password,user.getPassword())){
            throw new NotFoundException();
        }
        return user;
    }

    public User getUserByLoginAndPassword(UserRequestLogin body){
        User user = getUserByLogin(body.getLogin());
        if (!Passwords.CheckPasswordBCrypt(body.getPassword(),user.getPassword())){
            throw new WrongCredentialsException();
        }
        return user;
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

    public User getUserByNameAndLastName(String fullname){
        String answer[] = fullname.split(" ");
        return userRepository.findByNameAndLastName(answer[0], answer[1]);
    }

    /**
     * TODO: Replace with one below
     * @param login
     * @param password
     * @param name
     * @param lastName
     * @return
     */
    @Transactional
    public User createUser(String login, String password, String name, String lastName){
        User user = new User();
        user.setLogin(login);
        user.setPassword(Passwords.HashPasswordBCrypt(password));
        user.setName(name);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    @Transactional
    public User createUser(UserRequestBody body){
        User user = new User();
        applyCreateUpdates(user,body);
        return saveUser(user);
    }

    @Transactional
    public User putUser(User user, UserRequestBody body){
        applyPutUpdates(user,body);
        return saveUser(user);
    }

    @Transactional
    public User patchUser(User user,UserRequestBody body){
        applyPatchUpdates(user, body);
        return saveUser(user);
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
    
    public Set<UserMemberDTO> userMemberDTOSetProcess(Set<User> users){
        Set<UserMemberDTO> ret = new HashSet<>();
        for (User user : users){
            ret.add(new UserMemberDTO(user));
        }
        return ret;
    }

    public Set<UserMemberDTO> userMemberDTOSetParam(PublicVariables.UserRole myRole, PublicVariables.UserRole param, Team team){
        Set<User> users = roleRegistry.createUserDTOMapWithParam(team,param).getOrDefault(myRole, () -> {throw new NoPrivilegesException();}).get();
        return userMemberDTOSetProcess(users);
    }

    public Set<UserMemberDTO> userMemberDTOSetNoParam(PublicVariables.UserRole myRole, Team team){
        Set<User> users = roleRegistry.createUserDTOMapNoParam(team).getOrDefault(myRole, () -> {throw new NoPrivilegesException();}).get();
        return userMemberDTOSetProcess(users);
    }


    //TODO: Check if this response is okay, otherwise add string requirement
    public boolean validateUsers(Set<Integer> userIDs, Team team){
        if (userIDs == null || team == null) return false;
        for (int id : userIDs) {
            if (!checkIfUserExistsInATeam(getUserByID((long) id), team)) {
                throw new BadRequestException("Cannot add user to this task that is not part of the team");
            }
        }
        return true;
    }

    //TODO: delete?
    private void validateUser(User user){
        if (user == null){
            throw new BadRequestException("User is set to null");
        }
    }

    public Set<Team> getTeams(User user){
        return user.getTeams();
    }


    public Set<User> getUsersFromIDs(Set<Integer> userIDs){
        Set<User> users = new HashSet<>();
        for(int id : userIDs){
            users.add(getUserByID((long)id));
        }
        return users;
    }

    @Transactional
    public void addTaskToUser(User user, Task task) {
        user.getTasks().add(task);
        saveUser(user);
    }

    @Transactional
    public void removeTaskFromUser(User user, Task task){
        user.getTasks().remove(task);
        saveUser(user);
    }

    public void removeTaskFromAllUsers(Task task){
        for (User user : task.getUsers()){
            removeTaskFromUser(user,task);
        }
    }


}
