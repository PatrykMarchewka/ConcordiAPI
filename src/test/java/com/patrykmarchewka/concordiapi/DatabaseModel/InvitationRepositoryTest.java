package com.patrykmarchewka.concordiapi.DatabaseModel;

import com.patrykmarchewka.concordiapi.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class InvitationRepositoryTest implements InvitationTestHelper, TeamTestHelper{

    private final InvitationRepository invitationRepository;
    private final TeamRepository teamRepository;
    @Autowired
    public InvitationRepositoryTest(InvitationRepository invitationRepository, TeamRepository teamRepository) {
        this.invitationRepository = invitationRepository;
        this.teamRepository = teamRepository;
    }

    @AfterEach
    void cleanUp(){
        invitationRepository.deleteAll();
        invitationRepository.flush();
        teamRepository.deleteAll();
        teamRepository.flush();
    }

    @Test
    void shouldSaveAndRetrieveInvitationCorrectly(){
        Team team = createTeam(teamRepository);
        Invitation invitation = createInvitation(team, UserRole.MEMBER,1,invitationRepository);

        Invitation found = invitationRepository.findByUUID(invitation.getUUID()).orElse(null);

        assertNotNull(found);
        assertEquals(team,found.getInvitingTeam());
        assertEquals(UserRole.MEMBER, found.getRole());
        assertEquals(101,found.getUses());
        assertTrue(OffsetDateTime.now().isBefore(found.getDueTime()));
        assertTrue(OffsetDateTime.now().plusDays(2).isAfter(found.getDueTime()));
    }

    @Test
    void shouldFindByTeam(){
        Team team = createTeam(teamRepository);
        createInvitation(team,UserRole.MEMBER,0,invitationRepository);
        createInvitation(team,UserRole.OWNER,0,invitationRepository);

        Set<Invitation> found = invitationRepository.getAllByInvitingTeam(team);

        assertNotNull(found);
        assertEquals(2, found.size());
    }

    @Test
    void shouldReturnTrueForEmptyTeam(){
        Team team = createTeam(teamRepository);

        Set<Invitation> found = invitationRepository.getAllByInvitingTeam(team);

        assertTrue(found.isEmpty());
    }




}
