package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DatabaseModel.TeamUserRole;
import com.patrykmarchewka.concordiapi.Exceptions.NoPrivilegesException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import com.patrykmarchewka.concordiapi.UserRole;
import com.patrykmarchewka.concordiapi.Users.UserRequestBodyHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamUserRoleServiceTest implements TeamRequestBodyHelper, UserRequestBodyHelper {

    private final TeamUserRoleService teamUserRoleService;
    private final TestDataLoader testDataLoader;

    @Autowired
    public TeamUserRoleServiceTest(TeamUserRoleService teamUserRoleService, TestDataLoader testDataLoader) {
        this.teamUserRoleService = teamUserRoleService;
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


    /// getByUserAndTeam

    @Test
    void shouldGetByUserAndTeam(){
        TeamUserRole teamUserRole = teamUserRoleService.getByUserAndTeam(testDataLoader.userReadOwner.getID(), testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead, teamUserRole.getTeam());
        assertEquals(testDataLoader.userReadOwner, teamUserRole.getUser());
        assertEquals(UserRole.OWNER, teamUserRole.getUserRole());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidGetByUserAndTeam(long ID){
        assertThrows(NotFoundException.class, () -> teamUserRoleService.getByUserAndTeam(ID, ID));
    }

    /// getRole

    @Test
    void shouldGetRole(){
        UserRole role = teamUserRoleService.getRole(testDataLoader.userReadOwner.getID(), testDataLoader.teamRead.getID());

        assertEquals(UserRole.OWNER, role);
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidGetRole(long ID){
        assertThrows(NotFoundException.class, () -> teamUserRoleService.getRole(ID, ID));
    }

    /// setRole

    @Test
    void shouldSetRole(){
        teamUserRoleService.setRole(testDataLoader.userSecondOwner.getID(), testDataLoader.teamWrite.getID(), UserRole.ADMIN);
        UserRole role = teamUserRoleService.getRole(testDataLoader.userSecondOwner.getID(), testDataLoader.teamWrite.getID());

        assertEquals(UserRole.ADMIN, role);
    }

    @Test
    void shouldThrowForOnlyOwnerSetRole(){
        assertThrows(NoPrivilegesException.class, () -> teamUserRoleService.setRole(testDataLoader.userDeleteOwner.getID(), testDataLoader.teamDelete.getID(), UserRole.ADMIN));
    }

    /// getAllByTeamAndUserRole

    @Test
    void shouldReturnSingleGetAllByTeamAndUserRole(){
        Set<TeamUserRole> set = teamUserRoleService.getAllByTeamAndUserRole(testDataLoader.teamRead.getID(), UserRole.ADMIN);
        TeamUserRole expected = teamUserRoleService.getByUserAndTeam(testDataLoader.userAdmin.getID(), testDataLoader.teamRead.getID());

        assertEquals(1, set.size());
        assertTrue(set.contains(expected));
    }

    @Test
    void shouldReturnMultipleGetAllByTeamAndUserRole(){
        Set<TeamUserRole> set = teamUserRoleService.getAllByTeamAndUserRole(testDataLoader.teamRead.getID(), UserRole.OWNER);
        TeamUserRole expected = teamUserRoleService.getByUserAndTeam(testDataLoader.userReadOwner.getID(), testDataLoader.teamRead.getID());
        TeamUserRole expected1 = teamUserRoleService.getByUserAndTeam(testDataLoader.userSecondOwner.getID(), testDataLoader.teamRead.getID());

        assertEquals(2, set.size());
        assertTrue(set.contains(expected));
        assertTrue(set.contains(expected1));
    }

    @Test
    void shouldThrowForNoResultsGetAllByTeamAndUserRole(){
        assertThrows(NotFoundException.class, () -> teamUserRoleService.getAllByTeamAndUserRole(testDataLoader.teamDelete.getID(), UserRole.BANNED));
    }

    /// saveTMR

    @Test
    void shouldSaveTMR(){
        TeamUserRole teamUserRole = teamUserRoleService.getByUserAndTeam(testDataLoader.userManager.getID(), testDataLoader.teamWrite.getID());
        teamUserRole.setUserRole(UserRole.MEMBER);

        teamUserRoleService.saveTMR(teamUserRole);

        assertEquals(UserRole.MEMBER, teamUserRoleService.getByUserAndTeam(testDataLoader.userManager.getID(), testDataLoader.teamWrite.getID()).getUserRole());
    }

    /// checkRoles

    @ParameterizedTest
    @EnumSource(value = UserRole.class)
    void shouldCheckRolesOwner(UserRole role){
        assertTrue(teamUserRoleService.checkRoles(UserRole.OWNER, role));
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = {"OWNER"})
    void shouldCheckRolesAdmin(UserRole role){
        assertFalse(teamUserRoleService.checkRoles(UserRole.ADMIN, UserRole.OWNER));
        assertTrue(teamUserRoleService.checkRoles(UserRole.ADMIN, role));
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = {"OWNER", "ADMIN"})
    void shouldCheckRolesManager(UserRole role){
        assertFalse(teamUserRoleService.checkRoles(UserRole.MANAGER, UserRole.OWNER));
        assertFalse(teamUserRoleService.checkRoles(UserRole.MANAGER, UserRole.ADMIN));
        assertTrue(teamUserRoleService.checkRoles(UserRole.MANAGER, role));
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = {"OWNER", "ADMIN", "MANAGER"})
    void shouldCheckRolesMember(UserRole role){
        assertFalse(teamUserRoleService.checkRoles(UserRole.MEMBER, UserRole.OWNER));
        assertFalse(teamUserRoleService.checkRoles(UserRole.MEMBER, UserRole.ADMIN));
        assertFalse(teamUserRoleService.checkRoles(UserRole.MEMBER, UserRole.MANAGER));
        assertTrue(teamUserRoleService.checkRoles(UserRole.MEMBER, role));
    }

    @ParameterizedTest
    @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = {"OWNER", "ADMIN", "MANAGER", "MEMBER"})
    void shouldCheckRolesBanned(UserRole role){
        assertFalse(teamUserRoleService.checkRoles(UserRole.BANNED, UserRole.OWNER));
        assertFalse(teamUserRoleService.checkRoles(UserRole.BANNED, UserRole.ADMIN));
        assertFalse(teamUserRoleService.checkRoles(UserRole.BANNED, UserRole.MANAGER));
        assertFalse(teamUserRoleService.checkRoles(UserRole.BANNED, UserRole.MEMBER));
        assertTrue(teamUserRoleService.checkRoles(UserRole.BANNED, role));
    }

    /// forceCheckRoles

    @Test
    void shouldForceCheckRoles(){
        assertDoesNotThrow(() -> teamUserRoleService.forceCheckRoles(UserRole.OWNER, UserRole.BANNED));
    }

    @Test
    void shouldThrowForNoPrivilegesForceCheckRoles(){
        assertThrows(NoPrivilegesException.class, () -> teamUserRoleService.forceCheckRoles(UserRole.BANNED, UserRole.OWNER));
    }

    /// canOwnerLeave

    @Test
    void shouldReturnTrueIfCanOwnerLeave(){
        assertTrue(teamUserRoleService.canOwnerLeave(testDataLoader.teamRead.getID()));
    }

    @Test
    void shouldReturnFalseForSingleOwnerOwnerCanLeave(){
        assertFalse(teamUserRoleService.canOwnerLeave(testDataLoader.teamDelete.getID()));
    }
}
