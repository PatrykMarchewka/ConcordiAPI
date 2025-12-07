package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.ConflictException;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamFull;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamIdentity;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithInvitations;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithTasks;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRoles;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import com.patrykmarchewka.concordiapi.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamServiceTest {

    private final TeamService teamService;
    private final TestDataLoader testDataLoader;

    @Autowired
    public TeamServiceTest(TeamService teamService, TestDataLoader testDataLoader) {
        this.teamService = teamService;
        this.testDataLoader = testDataLoader;
    }

    @BeforeAll
    void initialize() {
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }

    /**
     * Refresh teamWrite to prevent comparing against stale data
     */
    private void refreshTeam(){
        testDataLoader.teamWrite = testDataLoader.refreshTeam(testDataLoader.teamWrite);
    }


    /// createTeam

    @Test
    void shouldCreateTeam(){
        TeamRequestBody body = new TeamRequestBody("NEWTeam");
        Team team = teamService.createTeam(body, testDataLoader.userWriteOwner);

        assertDoesNotThrow(team::getID);
        assertEquals(body.getName(), team.getName());
    }

    @Test
    void shouldCreateTeamDBCheck(){
        TeamRequestBody body = new TeamRequestBody("DBNewTeam");
        Team team = teamService.createTeam(body, testDataLoader.userWriteOwner);

        TeamIdentity actual = teamService.getTeamByID(team.getID());

        assertEquals(team.getID(), actual.getID());
        assertEquals(team.getName(), actual.getName());
    }

    /// putTeam

    @Test
    void shouldPutTeam(){
        refreshTeam();
        TeamRequestBody body = new TeamRequestBody("PUTTeam");
        TeamFull team = teamService.putTeam(testDataLoader.teamWrite.getID(), body);

        assertEquals(testDataLoader.teamWrite.getID(), team.getID());
        assertEquals(body.getName(), team.getName());
        assertEquals(testDataLoader.teamWrite.getUserRoles(), team.getUserRoles());
        assertEquals(testDataLoader.teamWrite.getTeamTasks(), team.getTeamTasks());
        assertEquals(testDataLoader.teamWrite.getInvitations(), team.getInvitations());
    }

    @Test
    void shouldPutTeamDBCheck(){
        TeamRequestBody body = new TeamRequestBody("DBPUTTeam");
        TeamFull team = teamService.putTeam(testDataLoader.teamWrite.getID(), body);

        TeamFull actual = teamService.getTeamFull(team.getID());

        assertEquals(team.getID(), actual.getID());
        assertEquals(body.getName(), actual.getName());
        assertEquals(team.getUserRoles(), actual.getUserRoles());
        assertEquals(team.getTeamTasks(), actual.getTeamTasks());
        assertEquals(team.getInvitations(), actual.getInvitations());
    }

    /// patchTeam

    @Test
    void shouldPatchTeam(){
        TeamRequestBody body = new TeamRequestBody("PATCHTeam");
        TeamFull team = teamService.patchTeam(testDataLoader.teamWrite.getID(), body);

        assertEquals(testDataLoader.teamWrite.getID(), team.getID());
        assertEquals(body.getName(), team.getName());
        assertEquals(testDataLoader.teamWrite.getUserRoles(), team.getUserRoles());
        assertEquals(testDataLoader.teamWrite.getTeamTasks(), team.getTeamTasks());
        assertEquals(testDataLoader.teamWrite.getInvitations(), team.getInvitations());
    }

    @Test
    void shouldPatchTeamDBCheck(){
        TeamRequestBody body = new TeamRequestBody("DBPUTTeam");
        TeamFull team = teamService.patchTeam(testDataLoader.teamWrite.getID(), body);

        TeamFull actual = teamService.getTeamFull(team.getID());

        assertEquals(team.getID(), actual.getID());
        assertEquals(body.getName(), actual.getName());
        assertEquals(team.getUserRoles(), actual.getUserRoles());
        assertEquals(team.getTeamTasks(), actual.getTeamTasks());
        assertEquals(team.getInvitations(), actual.getInvitations());
    }

    /// saveTeam

    @Test
    void shouldSaveTeam(){
        refreshTeam();
        testDataLoader.teamWrite.setName("newName");
        teamService.saveTeam(testDataLoader.teamWrite);

        assertEquals("newName", testDataLoader.refreshTeam(testDataLoader.teamWrite).getName());
    }

    /// deleteTeam

    @Test
    void shouldDeleteTeam(){
        Team team = teamService.createTeam(new TeamRequestBody("teamToDelete"), testDataLoader.userNoTeam);
        teamService.deleteTeam(team);

        assertThrows(NotFoundException.class, () -> teamService.getTeamByID(team.getID()));
    }

    /// addUser

    @Test
    void shouldAddUser(){
        TeamWithUserRoles team = teamService.addUser(testDataLoader.teamWrite.getID(), testDataLoader.userNoTeam, UserRole.MEMBER);

        assertTrue(team.checkUser(testDataLoader.userNoTeam.getID()));
    }

    @Test
    void shouldThrowForExistingUserWithSameRoleAddUser(){
        assertThrows(ConflictException.class, () -> teamService.addUser(testDataLoader.teamWrite.getID(), testDataLoader.userWriteOwner, UserRole.OWNER));
    }

    @Test
    void shouldThrowForExistingUserDifferentRoleAddUser(){
        assertThrows(ConflictException.class, () -> teamService.addUser(testDataLoader.teamWrite.getID(), testDataLoader.userWriteOwner, UserRole.ADMIN));

    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDAddUser(long teamID){
        assertThrows(NotFoundException.class, () -> teamService.addUser(teamID, testDataLoader.userNoTeam, UserRole.MEMBER));
    }

    /// removeUser

    @Test
    void shouldRemoveUser(){
        TeamWithUserRoles team = teamService.removeUser(testDataLoader.teamWrite.getID(), testDataLoader.userMember.getID());

        assertFalse(team.checkUser(testDataLoader.userMember.getID()));
    }

    @Test
    void shouldRemoveUserAndDeleteTeam(){
        long teamID = teamService.createTeam(new TeamRequestBody("newTeam"), testDataLoader.userNoTeam).getID();
        teamService.removeUser(teamID, testDataLoader.userNoTeam.getID());

        assertThrows(NotFoundException.class, () -> teamService.getTeamByID(teamID));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDRemoveUser(long teamID){
        assertThrows(NotFoundException.class, () -> teamService.removeUser(teamID, testDataLoader.userNoTeam.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDRemoveUser(long userID){
        assertThrows(NotFoundException.class, () -> teamService.removeUser(testDataLoader.teamRead.getID(), userID));
    }

    /// getTeamsDTO

    @Test
    void shouldGetTeamsDTOOwner(){
        Set<TeamDTO> set = teamService.getTeamsDTO(testDataLoader.userReadOwner);

        assertEquals(1, set.size());
        assertTrue(set.stream().allMatch(teamDTO -> teamDTO instanceof TeamAdminDTO));
    }

    @Test
    void shouldGetTeamsDTOAdmin(){
        Set<TeamDTO> set = teamService.getTeamsDTO(testDataLoader.userAdmin);

        assertEquals(3, set.size());
        assertTrue(set.stream().allMatch(teamDTO -> teamDTO instanceof TeamAdminDTO));
    }

    @Test
    void shouldGetTeamsDTOManager(){
        Set<TeamDTO> set = teamService.getTeamsDTO(testDataLoader.userManager);

        assertEquals(3, set.size());
        assertTrue(set.stream().allMatch(teamDTO -> teamDTO instanceof TeamManagerDTO));
    }

    @Test
    void shouldGetTeamsDTOMember(){
        Set<TeamDTO> set = teamService.getTeamsDTO(testDataLoader.userMember);

        assertEquals(3, set.size());
        assertTrue(set.stream().allMatch(teamDTO -> teamDTO instanceof TeamMemberDTO));
    }

    @Test
    void shouldReturnNullForBannedGetTeamsDTO(){
        Set<TeamDTO> set = teamService.getTeamsDTO(testDataLoader.userBanned);

        assertTrue(set.isEmpty());
    }

    /// getTeamDTOByRole

    @Test
    void shouldGetTeamDTOByRole(){
        TeamDTO teamDTO = teamService.getTeamDTOByRole(testDataLoader.userReadOwner.getID(), testDataLoader.teamRead.getID());

        assertInstanceOf(TeamAdminDTO.class, teamDTO);
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidUserIDGetTeamDTOByRole(long userID){
        assertThrows(NotFoundException.class, () -> teamService.getTeamDTOByRole(userID, testDataLoader.teamRead.getID()));
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidTeamIDGetTeamDTOByRole(long teamID){
        assertThrows(NotFoundException.class, () -> teamService.getTeamDTOByRole(testDataLoader.userReadOwner.getID(), teamID));
    }

    /// getTeamByID

    @Test
    void shouldGetTeamByID(){
        TeamIdentity team = teamService.getTeamByID(testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead.getID(), team.getID());
        assertEquals(testDataLoader.teamRead.getName(), team.getName());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidIDGetTeamByID(long teamID){
        assertThrows(NotFoundException.class, () -> teamService.getTeamByID(teamID));
    }

    /// getTeamWithUserRoles

    @Test
    void shouldGetTeamWithUserRoles(){
        TeamWithUserRoles team = teamService.getTeamWithUserRoles(testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead.getID(), team.getID());
        assertEquals(testDataLoader.teamRead.getName(), team.getName());
        assertEquals(testDataLoader.teamRead.getUserRoles(), team.getUserRoles());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidIDGetTeamWithUserRoles(long teamID){
        assertThrows(NotFoundException.class, () -> teamService.getTeamWithUserRoles(teamID));
    }

    /// getTeamWithTasks

    @Test
    void shouldGetTeamWithTasks(){
        TeamWithTasks team = teamService.getTeamWithTasks(testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead.getID(), team.getID());
        assertEquals(testDataLoader.teamRead.getName(), team.getName());
        assertEquals(testDataLoader.teamRead.getTeamTasks(), team.getTeamTasks());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidIDGetTeamWithTasks(long teamID){
        assertThrows(NotFoundException.class, () -> teamService.getTeamWithTasks(teamID));
    }

    /// getTeamWithUserRolesAndTasksByID

    @Test
    void shouldGetTeamWithUserRolesAndTasksByID(){
        TeamWithUserRolesAndTasks team = teamService.getTeamWithUserRolesAndTasksByID(testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead.getID(), team.getID());
        assertEquals(testDataLoader.teamRead.getName(), team.getName());
        assertEquals(testDataLoader.teamRead.getUserRoles(), team.getUserRoles());
        assertEquals(testDataLoader.teamRead.getTeamTasks(), team.getTeamTasks());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidIDGetTeamWithUserRolesAndTasksByID(long teamID){
        assertThrows(NotFoundException.class, () -> teamService.getTeamWithUserRolesAndTasksByID(teamID));
    }

    /// getTeamWithInvitations

    @Test
    void shouldGetTeamWithInvitations(){
        TeamWithInvitations team = teamService.getTeamWithInvitations(testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead.getID(), team.getID());
        assertEquals(testDataLoader.teamRead.getName(), team.getName());
        assertEquals(testDataLoader.teamRead.getInvitations(), team.getInvitations());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidIDGetTeamWithInvitations(long teamID){
        assertThrows(NotFoundException.class, () -> teamService.getTeamWithInvitations(teamID));
    }

    /// getTeamFull

    @Test
    void shouldGetTeamFull(){
        TeamFull team = teamService.getTeamFull(testDataLoader.teamRead.getID());

        assertEquals(testDataLoader.teamRead.getID(), team.getID());
        assertEquals(testDataLoader.teamRead.getName(), team.getName());
        assertEquals(testDataLoader.teamRead.getUserRoles(), team.getUserRoles());
        assertEquals(testDataLoader.teamRead.getTeamTasks(), team.getTeamTasks());
        assertEquals(testDataLoader.teamRead.getInvitations(), team.getInvitations());
    }

    @ParameterizedTest
    @ValueSource(longs = {999L, -1})
    void shouldThrowForInvalidIDGetTeamFull(long teamID){
        assertThrows(NotFoundException.class, () -> teamService.getTeamFull(teamID));
    }
}
