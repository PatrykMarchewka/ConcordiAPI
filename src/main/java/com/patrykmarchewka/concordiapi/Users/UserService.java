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
import com.patrykmarchewka.concordiapi.RoleRegistry;
import com.patrykmarchewka.concordiapi.UserRole;
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

    /**
     * Applies CREATE updates for the User given the UserRequestBody details, should be only called from {@link #createUser(UserRequestBody)}
     * @param user User to modify
     * @param body UserRequestBody with information to update
     */
    private void applyCreateUpdates(User user, UserRequestBody body){
        for (UserUpdater updater : updaters()){
            if (updater instanceof UserCREATEUpdater createUpdater){
                createUpdater.CREATEUpdate(user,body);
            }
        }
    }

    /**
     * Applies PUT updates for the User given the UserRequestBody details, should be only called from {@link #putUser(User, UserRequestBody)}
     * @param user User to modify
     * @param body UserRequestBody with information to update
     */
    private void applyPutUpdates(User user, UserRequestBody body){
        for (UserUpdater updater : updaters()){
            if (updater instanceof UserPUTUpdater putUpdater){
                putUpdater.PUTUpdate(user, body);
            }
        }
    }

    /**
     * Applies PATCH updates for the User given the UserRequestBody details, should be only called from {@link #patchUser(User, UserRequestBody)}
     * @param user User to modify
     * @param body UserRequestBody with information to update
     */
    private void applyPatchUpdates(User user, UserRequestBody body){
        for (UserUpdater updater : updaters()){
            if (updater instanceof  UserPATCHUpdater patchUpdater){
                patchUpdater.PATCHUpdate(user, body);
            }
        }
    }

    /**
     * Returns User given ID or throws
     * @param id ID of the user to search for
     * @return User with the given ID
     * @throws NotFoundException Thrown when can't find User with provided ID
     */
    public User getUserByID(Long id){
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    /**
     * Returns User given Login or throws
     * @param Login Login of the user to search for
     * @return User with the given Login
     * @throws NotFoundException Thrown when can't find User with provided Login
     */
    public User getUserByLogin(String Login){
        return userRepository.findByLogin(Login).orElseThrow(NotFoundException::new);
    }

    /**
     * Returns user given UserRequestLogin or throws
     * @param body UserRequestLogin with the credentials
     * @return User with the given credentials
     * @throws NotFoundException Thrown when can't find User with provided Login due to call to {@link #getUserByLogin(String)}
     * @throws WrongCredentialsException Thrown when credentials don't match
     */
    public User getUserByLoginAndPassword(UserRequestLogin body){
        User user = getUserByLogin(body.getLogin());
        if (!Passwords.CheckPasswordBCrypt(body.getPassword(),user.getPassword())){
            throw new WrongCredentialsException();
        }
        return user;
    }

    /**
     * Returns whether there is User with provided login
     * @param login Login to check for
     * @return True if user with given login exists, otherwise false
     */
    public boolean checkIfUserExistsByLogin(String login){
        return userRepository.existsByLogin(login);
    }

    /**
     * Returns whether provided user is in the provided team
     * @param user User to check for
     * @param team Team in which to check
     * @return True if user exists in given team, otherwise false
     */
    public boolean checkIfUserExistsInATeam(User user, Team team){
        return user.checkTeam(team) && team.checkTeammate(user);
    }

    /**
     * Creates user with specified UserRequestBody details
     * @param body UserRequestBody with User credentials
     * @return New created user
     */
    @Transactional
    public User createUser(UserRequestBody body){
        User user = new User();
        applyCreateUpdates(user,body);
        return saveUser(user);
    }

    /**
     * Modifies user entirely with specified UserRequestBody details
     * @param user User to modify
     * @param body UserRequestBody with new credentials
     * @return Modified User
     */
    @Transactional
    public User putUser(User user, UserRequestBody body){
        applyPutUpdates(user,body);
        return saveUser(user);
    }

    /**
     * Modifies user partially
     * @param user User to modify
     * @param body UserRequestBody with new credentials
     * @return Modified User
     */
    @Transactional
    public User patchUser(User user,UserRequestBody body){
        applyPatchUpdates(user, body);
        return saveUser(user);
    }

    /**
     * Deletes specified User
     * @param user User to delete
     */
    public void deleteUser(User user){
        userRepository.delete(user);
    }

    /**
     * Deletes specified User by ID
     * @param id ID of user to delete
     */
    public void deleteUserByID(long id){
        userRepository.deleteById(id);
    }

    /**
     * Saves pending changes to User
     * @param user User to save
     * @return User after changes
     */
    public User saveUser(User user){
        return userRepository.save(user);
    }

    /**
     * Returns DTO of given Set of Users
     * @param users Set of Users to get DTO of
     * @return UserMemberDTO of each User in the set
     */
    public Set<UserMemberDTO> userMemberDTOSetProcess(Set<User> users){
        Set<UserMemberDTO> ret = new HashSet<>();
        for (User user : users){
            ret.add(new UserMemberDTO(user));
        }
        return ret;
    }

    /**
     * Returns DTO of each user with the given UserRole
     * @param myRole Role of User asking for information
     * @param param UserRole of users to get
     * @param team Team in which to search
     * @return UserMemberDTO of each user with provided role
     * @throws NoPrivilegesException Thrown when User asking for information doesn't have sufficient privileges
     */
    public Set<UserMemberDTO> userMemberDTOSetParam(UserRole myRole, UserRole param, Team team){
        Set<User> users = roleRegistry.createUserDTOMapWithParam(team,param).getOrDefault(myRole, () -> {throw new NoPrivilegesException();}).get();
        return userMemberDTOSetProcess(users);
    }

    /**
     * Returns DTO of users in team
     * @param myRole Role of User asking for information
     * @param team Team in which to search
     * @return UserMemberDTO of each user in the team
     * @throws NoPrivilegesException Thrown when User asking for information doesn't have sufficient privileges
     */
    public Set<UserMemberDTO> userMemberDTOSetNoParam(UserRole myRole, Team team){
        Set<User> users = roleRegistry.createUserDTOMapNoParam(team).getOrDefault(myRole, () -> {throw new NoPrivilegesException();}).get();
        return userMemberDTOSetProcess(users);
    }


    /**
     * Checks if users belongs in a given team
     * @param userIDs Set of IDs of Users to check
     * @param team Team in which to search
     * @return True if all users are part of given team, otherwise false
     * @throws BadRequestException Thrown when one or more users are not part of the team
     */
    public boolean validateUsersForTasks(Set<Integer> userIDs, Team team){
        if (userIDs == null || team == null) return false;
        for (int id : userIDs) {
            if (!checkIfUserExistsInATeam(getUserByID((long) id), team)) {
                throw new BadRequestException("Cannot add user to this task that is not part of the team");
            }
        }
        return true;
    }

    /**
     * Returns teams in which user is part of
     * @param user User to check
     * @return Set of teams that user belongs to
     */
    public Set<Team> getTeams(User user){
        return user.getTeams();
    }

    /**
     * Returns Users with provided IDs
     * @param userIDs Set of IDs to check for
     * @return Set of Users with given IDs
     */
    public Set<User> getUsersFromIDs(Set<Integer> userIDs){
        Set<User> users = new HashSet<>();
        for(int id : userIDs){
            users.add(getUserByID((long)id));
        }
        return users;
    }

    /**
     * Adds Task to specified User
     * @param user User to get added to task
     * @param task Task to attach to user
     */
    @Transactional
    public void addTaskToUser(User user, Task task) {
        user.getTasks().add(task);
        saveUser(user);
    }

    /**
     * Removes Task from specified User
     * @param user User to get removed from the task
     * @param task Task to remove from user
     */
    @Transactional
    public void removeTaskFromUser(User user, Task task){
        user.getTasks().remove(task);
        saveUser(user);
    }

    /**
     * Removes Task from all users
     * @param task Task to remove
     */
    public void removeTaskFromAllUsers(Task task){
        for (User user : task.getUsers()){
            removeTaskFromUser(user,task);
        }
    }


}
