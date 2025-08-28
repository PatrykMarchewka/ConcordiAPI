package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestLogin;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserRepository;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Exceptions.WrongCredentialsException;
import com.patrykmarchewka.concordiapi.Passwords;
import com.patrykmarchewka.concordiapi.RoleRegistry;
import com.patrykmarchewka.concordiapi.UpdateType;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserUpdatersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class UserService {


    private final UserRepository userRepository;
    private final RoleRegistry roleRegistry;
    private final UserUpdatersService userUpdatersService;

    @Autowired
    public UserService(UserRepository userRepository, RoleRegistry roleRegistry, UserUpdatersService userUpdatersService){
        this.userRepository = userRepository;
        this.roleRegistry = roleRegistry;
        this.userUpdatersService = userUpdatersService;
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
     * Returns user with given login or null
     * @param Login Login of the user to search for
     * @return User with the given login or null value
     */
    public User getUserByLogin(String Login){
        return userRepository.findByLogin(Login).orElse(null);
    }

    /**
     * Returns user given UserRequestLogin or throws
     * @param body UserRequestLogin with the credentials
     * @return User with the given credentials
     * @throws WrongCredentialsException Thrown when credentials don't match
     */
    public User getUserByLoginAndPassword(UserRequestLogin body){
        User user = getUserByLogin(body.getLogin());

        //If user doesnt exist, we still run hash check
        //This is done to prevent timing attacks that could reveal valid usernames
        String hash = (user != null) ? user.getPassword() : "$2a$13$abcdefghijklmnopqrstuvwxyz";

        if (!Passwords.CheckPasswordBCrypt(body.getPassword(),hash)){
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
     * Creates user with specified UserRequestBody details
     * @param body UserRequestBody with User credentials
     * @return New created user
     */
    @Transactional
    public User createUser(UserRequestBody body){
        User user = new User();
        userUpdatersService.update(user,body, UpdateType.CREATE);
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
        userUpdatersService.update(user,body,UpdateType.PUT);
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
        userUpdatersService.update(user,body,UpdateType.PATCH);
        return saveUser(user);
    }

    /**
     * Unused, Deletes specified User
     * @param user User to delete
     */
    public void deleteUser(User user){
        userRepository.delete(user);
    }

    /**
     * Unused, Deletes specified User by ID
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

    public List<User> saveAllUsers(Set<User> users){
        return userRepository.saveAll(users);
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
            if (!team.checkUser(getUserByID((long) id))) {
                throw new BadRequestException("Cannot add user to this task that is not part of the team: UserID - " + id);
            }
        }
        return true;
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


}
