package com.patrykmarchewka.concordiapi.Users;

import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestBody;
import com.patrykmarchewka.concordiapi.DTO.UserDTO.UserRequestLogin;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.Exceptions.WrongCredentialsException;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithCredentials;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithTeamRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithUserTasks;
import com.patrykmarchewka.concordiapi.Passwords;
import com.patrykmarchewka.concordiapi.Teams.TeamRequestBodyHelper;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import com.patrykmarchewka.concordiapi.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest implements UserRequestBodyHelper, UserRequestLoginHelper, TeamRequestBodyHelper {

    private final UserService userService;
    private final TestDataLoader testDataLoader;

    @Autowired
    public UserServiceTest(UserService userService, TestDataLoader testDataLoader) {
        this.userService = userService;
        this.testDataLoader = testDataLoader;
    }

    @BeforeAll
    void initialize(){
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }


    /// checkIfUserExistsByLogin

    @Test
    void shouldReturnTrueIfUserExistsByLogin(){
        assertTrue(userService.checkIfUserExistsByLogin(testDataLoader.userMember.getLogin()));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"TEST"})
    void shouldReturnFalseIfUserExistsForNonExistentLogin(String login){
        assertFalse(userService.checkIfUserExistsByLogin(login));
    }

    @Test
    void shouldReturnFalseIfUserExistsForCaseSensitivity(){
        assertFalse(userService.checkIfUserExistsByLogin(testDataLoader.userMember.getLogin().toLowerCase()));
    }

    /// createUser

    @Test
    void shouldCreateUser(){
        UserRequestBody body = new UserRequestBody("NEWLogin","NEWPassword","NEWName","NEWLastName");
        User user = userService.createUser(body);

        assertDoesNotThrow(user::getID);
        assertEquals(body.getLogin(), user.getLogin());
        assertTrue(Passwords.CheckPasswordBCrypt(body.getPassword(), user.getPassword()));
        assertEquals(body.getName(), user.getName());
        assertEquals(body.getLastName(), user.getLastName());
    }

    @Test
    void shouldCreateUserDBCheck(){
        UserRequestBody body = new UserRequestBody("DBNEWLogin","DBNEWPassword","DBNEWName","DBNEWLastName");
        User user = userService.createUser(body);

        UserFull actual = userService.getUserFull(user.getID());

        assertEquals(user.getID(), actual.getID());
        assertEquals(user.getLogin(), actual.getLogin());
        assertEquals(user.getPassword(), actual.getPassword());
        assertEquals(user.getName(), actual.getName());
        assertEquals(user.getLastName(), actual.getLastName());
    }

    @Test
    void shouldThrowForNonUniqueLoginCreateUser(){
        UserRequestBody body = new UserRequestBody("MEMBER", "MEMBER", "MEMBER", "MEMBER");
        assertThrows(ConflictException.class, () -> userService.createUser(body));
    }

    /// putUser

    @Test
    void shouldPutUser(){
        UserRequestBody body = new UserRequestBody("PUTLogin","PUTPassword","PUTName","PUTLastName");
        UserWithCredentials user = userService.putUser(testDataLoader.userWriteOwner, body);

        assertDoesNotThrow(user::getID);
        assertEquals(body.getLogin(), user.getLogin());
        assertTrue(Passwords.CheckPasswordBCrypt(body.getPassword(), user.getPassword()));
        assertEquals(body.getName(), user.getName());
        assertEquals(body.getLastName(), user.getLastName());
    }

    @Test
    void shouldPutUserDB(){
        UserRequestBody body = new UserRequestBody("DBPUTLogin","DBPUTPassword","DBPUTName","DBPUTLastName");
        UserWithCredentials user = userService.putUser(testDataLoader.userWriteOwner, body);

        UserWithCredentials actual = userService.getUserWithCredentialsByLogin(user.getLogin());

        assertEquals(user.getID(), actual.getID());
        assertEquals(user.getLogin(), actual.getLogin());
        assertEquals(user.getPassword(), actual.getPassword());
        assertEquals(user.getName(), actual.getName());
        assertEquals(user.getLastName(), actual.getLastName());
    }

    @Test
    void shouldThrowForNonUniqueLoginPutUser(){
        UserRequestBody body = new UserRequestBody("MEMBER", "MEMBER", "MEMBER", "MEMBER");
        assertThrows(ConflictException.class, () -> userService.putUser(testDataLoader.userWriteOwner, body));
    }

    /// patchUser

    @Test
    void shouldPatchUser(){
        UserRequestBody body = new UserRequestBody(null, null, "PATCHName", null);
        UserWithCredentials user = userService.patchUser(testDataLoader.userWriteOwner, body);

        assertDoesNotThrow(user::getID);
        assertNotNull(user.getLogin());
        assertNotNull(user.getPassword());
        assertEquals(body.getName(), user.getName());
        assertNotNull(user.getLastName());
    }

    @Test
    void shouldPatchUserDB(){
        UserRequestBody body = new UserRequestBody(null, null, "DBPATCHName", null);
        UserWithCredentials user = userService.patchUser(testDataLoader.userWriteOwner, body);

        UserWithCredentials actual = userService.getUserWithCredentialsByLogin(user.getLogin());

        assertEquals(user.getID(), actual.getID());
        assertNotNull(actual.getLogin());
        assertNotNull(actual.getPassword());
        assertEquals(user.getName(), actual.getName());
        assertNotNull(actual.getLastName());
    }

    @Test
    void shouldPatchUserFully(){
        UserRequestBody body = new UserRequestBody("PATCHLogin", "PATCHPassword", "PATCHName", "PATCHLastName");
        UserWithCredentials user = userService.patchUser(testDataLoader.userWriteOwner, body);

        assertDoesNotThrow(user::getID);
        assertNotNull(user.getLogin());
        assertNotNull(user.getPassword());
        assertEquals(body.getName(), user.getName());
        assertNotNull(user.getLastName());
    }

    @Test
    void shouldPatchUserFullyDB(){
        UserRequestBody body = new UserRequestBody("DBPATCHLogin", "DBPATCHPassword", "DBPATCHName", "DBPATCHLastName");
        UserWithCredentials user = userService.patchUser(testDataLoader.userWriteOwner, body);

        UserWithCredentials actual = userService.getUserWithCredentialsByLogin(user.getLogin());

        assertEquals(user.getID(), actual.getID());
        assertNotNull(actual.getLogin());
        assertNotNull(actual.getPassword());
        assertEquals(body.getName(), actual.getName());
        assertNotNull(actual.getLastName());
    }

    @Test
    void shouldThrowForNonUniqueLoginPatchUser(){
        UserRequestBody body = new UserRequestBody("MEMBER", "MEMBER", "MEMBER", "MEMBER");
        assertThrows(ConflictException.class, () -> userService.patchUser(testDataLoader.userWriteOwner, body));
    }

    /// deleteUser

    @Test
    void shouldDeleteUser(){
        userService.deleteUser(testDataLoader.userDeleteOwner);

        assertThrows(NotFoundException.class, () -> userService.getUserByID(testDataLoader.userDeleteOwner.getID()));
    }

    /// saveUser

    @Test
    void shouldSaveUser(){
        testDataLoader.userWriteOwner.setName("newName");
        userService.saveUser(testDataLoader.userWriteOwner);

        assertEquals("newName", testDataLoader.userWriteOwner.getName());
    }

    /// userMemberDTOSetProcess

    @Test
    void shouldReturnSingleUserMemberDTOSetProcess(){
        Set<UserMemberDTO> set = userService.userMemberDTOSetProcess(Set.of(testDataLoader.userMember));

        assertEquals(1, set.size());
        assertTrue(set.contains(new UserMemberDTO(testDataLoader.userMember)));
    }

    @Test
    void shouldReturnMultipleUserMemberDTOSetProcess(){
        Set<UserMemberDTO> set = userService.userMemberDTOSetProcess(Set.of(testDataLoader.userMember, testDataLoader.userReadOwner));

        assertEquals(2, set.size());
        assertTrue(set.contains(new UserMemberDTO(testDataLoader.userMember)));
        assertTrue(set.contains(new UserMemberDTO(testDataLoader.userReadOwner)));
    }

    @Test
    void shouldReturnNoneUserMemberDTOSetProcess(){
        Set<UserMemberDTO> set = userService.userMemberDTOSetProcess(Set.of());

        assertTrue(set.isEmpty());
    }

    @Test
    void shouldThrowForNullUserMemberDTOSetProcess(){
        assertThrows(NullPointerException.class, () -> userService.userMemberDTOSetProcess(null));
    }

    /// userMemberDTOSetParam

    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"OWNER", "ADMIN"})
    void shouldReturnSingleUserMemberDTOSetParam(UserRole role){
        Set<UserMemberDTO> set = userService.userMemberDTOSetParam(role, UserRole.ADMIN, testDataLoader.teamRead.getID());

        assertEquals(1, set.size());
        assertTrue(set.contains(new UserMemberDTO(testDataLoader.userAdmin)));
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"OWNER", "ADMIN"})
    void shouldReturnMultipleUserMemberDTOSetParam(UserRole role){
        Set<UserMemberDTO> set = userService.userMemberDTOSetParam(role, UserRole.OWNER, testDataLoader.teamRead.getID());

        assertEquals(2, set.size());
        assertTrue(set.contains(new UserMemberDTO(testDataLoader.userReadOwner)));
        assertTrue(set.contains(new UserMemberDTO(testDataLoader.userSecondOwner)));
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = {"OWNER", "ADMIN"})
    void shouldThrowForNoPrivilegesUserMemberDTOSetParam(UserRole role){
        assertThrows(NoPrivilegesException.class, () -> userService.userMemberDTOSetParam(role, UserRole.MEMBER, testDataLoader.teamRead.getID()));
    }

    /// userMemberDTOSetNoParam

    @ParameterizedTest
    @EnumSource(value = UserRole.class, names = {"OWNER", "ADMIN"})
    void shouldReturnMultipleUserMemberDTOSetNoParam(UserRole role){
        Set<UserMemberDTO> set = userService.userMemberDTOSetNoParam(role, testDataLoader.teamRead);

        Set<UserMemberDTO> expected = testDataLoader.teamRead.getUserRoles().stream().map(teamUserRole -> new UserMemberDTO(teamUserRole.getUser())).collect(Collectors.toUnmodifiableSet());
        assertEquals(expected, set);
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = {"OWNER", "ADMIN"})
    void shouldThrowForNoPrivilegesUserMemberDTOSetNoParam(UserRole role){
        assertThrows(NoPrivilegesException.class, () -> userService.userMemberDTOSetNoParam(role, testDataLoader.teamRead));
    }

    /// getUserByID

    @Test
    void shouldGetUserByID(){
        UserIdentity user = userService.getUserByID(testDataLoader.userReadOwner.getID());

        assertEquals(testDataLoader.userReadOwner.getID(), user.getID());
        assertEquals(testDataLoader.userReadOwner.getName(), user.getName());
        assertEquals(testDataLoader.userReadOwner.getLastName(), user.getLastName());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidGetUserByID(long ID){
        assertThrows(NotFoundException.class, () -> userService.getUserByID(ID));
    }

    /// getUserWithCredentialsByLogin

    @Test
    void shouldGetUserWithCredentialsByLogin(){
        UserWithCredentials user = userService.getUserWithCredentialsByLogin(testDataLoader.userReadOwner.getLogin());

        assertEquals(testDataLoader.userReadOwner.getID(), user.getID());
        assertEquals(testDataLoader.userReadOwner.getName(), user.getName());
        assertEquals(testDataLoader.userReadOwner.getLastName(), user.getLastName());
        assertEquals(testDataLoader.userReadOwner.getLogin(), user.getLogin());
        assertEquals(testDataLoader.userReadOwner.getPassword(), user.getPassword());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"TEST"})
    void shouldThrowForInvalidUserWithCredentialsByLogin(String login){
        assertThrows(NotFoundException.class, () -> userService.getUserWithCredentialsByLogin(login));
    }

    /// getUserWithCredentialsByLoginAndPassword

    @Test
    void shouldGetUserWithCredentialsByLoginAndPassword(){
        UserRequestLogin body = new UserRequestLogin("MEMBER", "MEMBER");
        UserWithCredentials user = userService.getUserWithCredentialsByLoginAndPassword(body);

        assertEquals(testDataLoader.userReadOwner.getID(), user.getID());
        assertEquals(testDataLoader.userReadOwner.getName(), user.getName());
        assertEquals(testDataLoader.userReadOwner.getLastName(), user.getLastName());
        assertEquals(testDataLoader.userReadOwner.getLogin(), user.getLogin());
        assertEquals(testDataLoader.userReadOwner.getPassword(), user.getPassword());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"TEST"})
    void shouldThrowForInvalidUserWithCredentialsByLoginAndPassword(String test){
        assertThrows(WrongCredentialsException.class, () -> userService.getUserWithCredentialsByLoginAndPassword(new UserRequestLogin(test, test)));
    }

    /// getUserWithTeamRolesAndTeams

    @Test
    void shouldGetUserWithTeamRolesAndTeams(){
        UserWithTeamRoles user = userService.getUserWithTeamRolesAndTeams(testDataLoader.userReadOwner.getID());

        assertEquals(testDataLoader.userReadOwner.getID(), user.getID());
        assertEquals(testDataLoader.userReadOwner.getName(), user.getName());
        assertEquals(testDataLoader.userReadOwner.getLastName(), user.getLastName());
        assertEquals(testDataLoader.userReadOwner.getTeamRoles(), user.getTeamRoles());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidGetUserWithTeamRolesAndTeams(long ID){
        assertThrows(NotFoundException.class, () -> userService.getUserWithTeamRolesAndTeams(ID));
    }

    /// getUserWithUserTasks

    @Test
    void shouldGetUserWithUserTasks(){
        UserWithUserTasks user = userService.getUserWithUserTasks(testDataLoader.userReadOwner.getID());

        assertEquals(testDataLoader.userReadOwner.getID(), user.getID());
        assertEquals(testDataLoader.userReadOwner.getName(), user.getName());
        assertEquals(testDataLoader.userReadOwner.getLastName(), user.getLastName());
        assertEquals(testDataLoader.userReadOwner.getUserTasks(), user.getUserTasks());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidGetUserWithUserTasks(long ID){
        assertThrows(NotFoundException.class, () -> userService.getUserWithUserTasks(ID));
    }
}
