package com.patrykmarchewka.concordiapi.DTO;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.Team.TeamWithUserRolesAndTasks;
import com.patrykmarchewka.concordiapi.Teams.TeamService;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamDTOTest {

    private final TestDataLoader testDataLoader;
    private final TeamService teamService;

    @Autowired
    public TeamDTOTest(final TestDataLoader testDataLoader, final TeamService teamService) {
        this.testDataLoader = testDataLoader;
        this.teamService = teamService;
    }

    @BeforeAll
    void initialize(){
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }

    /// TeamAdmin
    @Transactional
    @Test
    void assertTeamAdmin(){
        TeamWithUserRolesAndTasks team = teamService.getTeamWithUserRolesAndTasksByID(testDataLoader.teamRead.getID());
        TeamAdminDTO dto = new TeamAdminDTO(team);

        assertEquals(testDataLoader.teamRead.getID(), dto.getID());
        assertEquals(testDataLoader.teamRead.getName(), dto.getName());
        assertEquals(testDataLoader.teamRead.getTeamTasks().size(), dto.getTasks().size());
        assertEquals(testDataLoader.teamRead.getUserRoles().size(), dto.getUsersByRole().values().stream().mapToInt(Set::size).sum());
    }

    /// TeamManager
    @Transactional
    @Test
    void assertTeamManager(){
        TeamWithUserRolesAndTasks team = teamService.getTeamWithUserRolesAndTasksByID(testDataLoader.teamRead.getID());
        TeamManagerDTO dto = new TeamManagerDTO(team);

        assertEquals(testDataLoader.teamRead.getID(), dto.getID());
        assertEquals(testDataLoader.teamRead.getName(), dto.getName());
        assertEquals(testDataLoader.teamRead.getTeamTasks().size(), dto.getTasks().size());
        assertEquals(testDataLoader.teamRead.getUserRoles().stream().filter(teamUserRole -> !(teamUserRole.getUserRole().isAdmin() || teamUserRole.getUserRole().isBanned())).collect(Collectors.toUnmodifiableSet()).size(), dto.getUsersByRole().values().stream().mapToInt(Set::size).sum());
    }



    /// TeamMember
    @Test
    void assertTeamMember(){
        TeamMemberDTO dto = new TeamMemberDTO(testDataLoader.teamRead);

        assertEquals(testDataLoader.teamRead.getID(), dto.getID());
        assertEquals(testDataLoader.teamRead.getName(), dto.getName());
        assertEquals(testDataLoader.teamRead.getUserRoles().size(), dto.getTeammateCount());
        assertEquals(testDataLoader.teamRead.getUserRoles().stream().filter(ur -> ur.getUserRole().isOwner()).collect(Collectors.toUnmodifiableSet()).size(), dto.getOwners().size());
    }

    @Transactional
    @Test
    void assertTeamMemberSecond(){
        TeamWithUserRolesAndTasks team = teamService.getTeamWithUserRolesAndTasksByID(testDataLoader.teamRead.getID());
        TeamMemberDTO dto = new TeamMemberDTO(team, testDataLoader.userMember.getID());

        assertEquals(team.getID(), dto.getID());
        assertEquals(team.getName(), dto.getName());
        assertEquals(team.getUserRoles().size(), dto.getTeammateCount());
        assertEquals(team.getTeamTasks().stream().filter(task -> task.hasUser(testDataLoader.userMember.getID())).collect(Collectors.toUnmodifiableSet()).size(), dto.getTasks().size());
        assertEquals(team.getUserRoles().stream().filter(ur -> ur.getUserRole().isOwner()).collect(Collectors.toUnmodifiableSet()).size(), dto.getOwners().size());
    }


    /// Comparison
    @Transactional
    @Test
    void shouldFailComparison(){
        TeamWithUserRolesAndTasks team = teamService.getTeamWithUserRolesAndTasksByID(testDataLoader.teamRead.getID());
        TeamAdminDTO dto = new TeamAdminDTO(team);
        TeamManagerDTO dto1 = new TeamManagerDTO(team);
        TeamMemberDTO dto2 = new TeamMemberDTO(team);
        TeamMemberDTO dto3 = new TeamMemberDTO(team, testDataLoader.userMember.getID());

        assertNotEquals(dto, dto1);
        assertNotEquals(dto, dto2);
        assertNotEquals(dto, dto3);

        assertNotEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);

        assertNotEquals(dto2, dto3);
    }
}
