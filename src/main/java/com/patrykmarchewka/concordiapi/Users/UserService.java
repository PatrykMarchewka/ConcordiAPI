package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestLogin;
import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.DatabaseModel.UserRepository;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Exceptions.WrongCredentialsException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithCredentials;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithTeamRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithUserTasks;
import com.patrykmarchewka.concordiapi.Passwords;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.Updaters.UserUpdatersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserService {


    private final UserRepository userRepository;
    private final UserUpdatersService userUpdatersService;
    private final TeamUserRoleService teamUserRoleService;

    @Autowired
    public UserService(UserRepository userRepository, UserUpdatersService userUpdatersService, TeamUserRoleService teamUserRoleService){
        this.userRepository = userRepository;
        this.userUpdatersService = userUpdatersService;
        this.teamUserRoleService = teamUserRoleService;
    }

    /**
     * Returns whether there is User with provided login
     * @param login Login to check for
     * @return True if user with given login exists, otherwise false
     */
    public boolean checkIfUserExistsByLogin(@NonNull final String login){
        return userRepository.existsByLogin(login);
    }

    /**
     * Creates user with specified UserRequestBody details
     * @param body UserRequestBody with User credentials
     * @return New created user
     */
    @Transactional
    public User createUser(@NonNull final UserRequestBody body){
        User user = new User();
        userUpdatersService.createUpdate(user, body);
        return saveUser(user);
    }

    /**
     * Modifies user entirely with specified UserRequestBody details
     * @param user User to modify
     * @param body UserRequestBody with new credentials
     * @return Modified User
     */
    @Transactional
    public UserWithCredentials putUser(@NonNull final UserWithCredentials user, @NonNull final UserRequestBody body){
        userUpdatersService.putUpdate((User) user, body);
        return saveUser((User) user);
    }

    /**
     * Modifies user partially
     * @param user User to modify
     * @param body UserRequestBody with new credentials
     * @return Modified User
     */
    @Transactional
    public UserWithCredentials patchUser(@NonNull final UserWithCredentials user, @NonNull final UserRequestBody body){
        userUpdatersService.patchUpdate((User) user, body);
        return saveUser((User) user);
    }

    /**
     * Unused, Deletes specified User
     * @param user User to delete
     */
    @Transactional
    public void deleteUser(@NonNull final User user){
        userRepository.delete(user);
    }

    /**
     * Saves pending changes to User
     * @param user User to save
     * @return User after changes
     */
    @Transactional
    public User saveUser(@NonNull final User user){
        return userRepository.save(user);
    }

    /**
     * Returns DTO of given Set of Users
     * @param users Set of Users to get DTO of
     * @return UserMemberDTO of each User in the set
     */
    public Set<UserMemberDTO> userMemberDTOSetProcess(@NonNull final Set<UserIdentity> users){
        Set<UserMemberDTO> ret = new HashSet<>();
        for (UserIdentity user : users){
            ret.add(new UserMemberDTO(user));
        }
        return ret;
    }

    /**
     * Returns DTO of each user with the given UserRole
     * @param param UserRole of users to get
     * @param teamID ID of Team in which to search for
     * @return UserMemberDTO of each user with provided role
     * @throws NoPrivilegesException Thrown when User asking for information doesn't have sufficient privileges
     */
    public Set<UserMemberDTO> userMemberDTOSetParam(@NonNull final UserRole param, final long teamID){
        return userMemberDTOSetProcess(teamUserRoleService.getAllByTeamAndUserRole(teamID, param).stream().map(TeamUserRole::getUser).collect(Collectors.toUnmodifiableSet()));
    }

    /**
     * Returns DTO of users in team
     * @param team TeamWithUserRoles in which to search
     * @return UserMemberDTO of each user in the team
     * @throws NoPrivilegesException Thrown when User asking for information doesn't have sufficient privileges
     */
    public Set<UserMemberDTO> userMemberDTOSetNoParam(@NonNull final TeamWithUserRoles team){
        Set<UserIdentity> identities = new HashSet<>(team.getTeammates());
        return userMemberDTOSetProcess(identities);
    }

    /**
     * Returns User given ID or throws
     * @param id ID of the user to search for
     * @return User with the given ID
     * @throws NotFoundException Thrown when can't find User with provided ID
     */
    public UserIdentity getUserByID(final long id){
        return userRepository.findUserByID(id).orElseThrow(NotFoundException::new);
    }

        public UserWithCredentials getUserWithCredentialsByLogin(@NonNull final String login){
        return userRepository.findUserWithCredentialsByLogin(login).orElseThrow(NotFoundException::new);
    }

    /**
     * Returns user given UserRequestLogin or throws
     * @param body UserRequestLogin with the credentials
     * @return User with the given credentials
     * @throws WrongCredentialsException Thrown when credentials don't match
     */
    public UserWithCredentials getUserWithCredentialsByLoginAndPassword(@NonNull final UserRequestLogin body){
        UserWithCredentials user = userRepository.findUserWithCredentialsByLogin(body.getLogin()).orElse(null);

        //If user doesnt exist, we still run hash check
        //This is done to prevent timing attacks that could reveal valid usernames
        String hash = (user != null) ? user.getPassword() : "$2a$13$abcdefghijklmnopqrstuvwxyz";

        if (!Passwords.CheckPasswordBCrypt(body.getPassword(),hash)){
            throw new WrongCredentialsException();
        }
        return user;
    }

    public UserWithTeamRoles getUserWithTeamRolesAndTeams(final long id){
        return userRepository.findUserWithTeamRolesAndTeamsByID(id).orElseThrow(NotFoundException::new);
    }

    public UserWithUserTasks getUserWithUserTasks(final long id){
        return userRepository.findUserWithUserTasksByID(id).orElseThrow(NotFoundException::new);
    }

    public UserFull getUserFull(final long id){
        return userRepository.findUserFullByID(id).orElseThrow(NotFoundException::new);
    }

    /**
     * Deletes everything and flushes
     */
    public void deleteAll(){
        userRepository.deleteAll();
        userRepository.flush();
    }

}
