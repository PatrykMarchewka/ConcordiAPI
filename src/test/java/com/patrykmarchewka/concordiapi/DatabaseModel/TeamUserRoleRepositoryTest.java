package com.patrykmarchewka.concordiapi.DatabaseModel;


import com.patrykmarchewka.concordiapi.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class TeamUserRoleRepositoryTest implements TeamUserRoleTestHelper, UserTestHelper, TeamTestHelper{

    private final TeamUserRoleRepository teamUserRoleRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public TeamUserRoleRepositoryTest(TeamUserRoleRepository teamUserRoleRepository, UserRepository userRepository, TeamRepository teamRepository) {
        this.teamUserRoleRepository = teamUserRoleRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    @AfterEach
    void cleanUp(){
        teamUserRoleRepository.deleteAll();
        teamUserRoleRepository.flush();
        teamRepository.deleteAll();
        teamRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }


    @Test
    void shouldSaveAndRetrieveTeamUserRoleCorrectly(){
        User user = createUser("TEST",userRepository);
        Team team = createTeam(teamRepository);
        TeamUserRole teamUserRole = createTeamUserRole(team,user, UserRole.OWNER,teamUserRoleRepository);

        TeamUserRole found = teamUserRoleRepository.findByUserAndTeam(user,team).orElse(null);

        assertNotNull(found);
        assertEquals(team, found.getTeam());
        assertEquals(user, found.getUser());
        assertEquals(UserRole.OWNER, found.getUserRole());
        assertEquals(teamUserRole.getID(), found.getID());
    }

    @Test
    void shouldReturnTrueForWrongUserAndTeam() {
        User user = createUser("TEST", userRepository);
        Team team = createTeam(teamRepository);

        Optional<TeamUserRole> found = teamUserRoleRepository.findByUserAndTeam(user,team);

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindByTeamAndRole(){
        User user = createUser("ONE",userRepository);
        User user1 = createUser("TWO",userRepository);
        Team team = createTeam(teamRepository);

        createTeamUserRole(team,user,UserRole.OWNER,teamUserRoleRepository);
        createTeamUserRole(team,user1,UserRole.OWNER,teamUserRoleRepository);

        assertEquals(2, teamUserRoleRepository.getAllByTeamAndUserRole(team, UserRole.OWNER).size());
    }



}
