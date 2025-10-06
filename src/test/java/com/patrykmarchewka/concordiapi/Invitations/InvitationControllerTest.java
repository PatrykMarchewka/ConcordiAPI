package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.Exceptions.NotFoundException;
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.TestDataLoader;
import com.patrykmarchewka.concordiapi.UserRole;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvitationControllerTest {

    @Autowired
    private RestClient.Builder builder;
    @Autowired
    private TestDataLoader testDataLoader;

    private RestClient restClient;

    @LocalServerPort
    private int port;


    @BeforeAll
    void restInitialize(){
        this.restClient = builder.baseUrl("http://localhost:" + port).build();
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }
    
    @Test
    void shouldGetInvitationsForTeam(){
        var response = restClient.get().uri("/api/teams/{teamID}/invitations", testDataLoader.team1.getID()).header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<InvitationManagerDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("List of all invitations for this team", response.getBody().getMessage());
        assertTrue(response.getBody().getData().stream().anyMatch(invitationManagerDTO -> invitationManagerDTO.equalsInvitation(testDataLoader.invitation)));
        assertTrue(response.getBody().getData().stream().anyMatch(invitationManagerDTO -> invitationManagerDTO.equalsInvitation(testDataLoader.invitationExpired)));
        assertTrue(response.getBody().getData().stream().anyMatch(invitationManagerDTO -> invitationManagerDTO.equalsInvitation(testDataLoader.invitationNoUses)));
    }

    @Test
    void shouldCreateInvitation(){
        //To stop test from automatically failing in the future due to expired date, users can provide date as short as "2025-10-04T23:59Z"
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(10);

        String json = String.format("""
                {
                "uses": 100,
                "role": "MEMBER",
                "dueDate": "%s"
                }
                """, dueDate);
        var response = restClient.post().uri("/api/teams/{teamID}/invitations", testDataLoader.team2.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});
        testDataLoader.refreshTeam(testDataLoader.team2);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Created new invitation", response.getBody().getMessage());
        assertEquals(testDataLoader.team2.getID(), response.getBody().getData().getTeam().getID());
        assertEquals(testDataLoader.team2.getInvitations().size() + 1, testDataLoader.refreshedTeam.getInvitations().size());
        assertEquals((short)100, response.getBody().getData().getUses());
        assertEquals(UserRole.MEMBER, response.getBody().getData().getRole());
        //Some databases convert to UTC and strip offset, that is why compare we accept either solution as they ultimately point to same time
        assertTrue((dueDate.toString().equals(response.getBody().getData().getDueTime()) || (dueDate.toInstant().toString().equals(response.getBody().getData().getDueTime()))));
    }

    @Test
    void shouldCheckInvitation(){
        var response = restClient.get().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.team1.getID(), testDataLoader.invitation.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Information about this invitation", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsInvitation(testDataLoader.invitation));
    }

    @Test
    void shouldPutInvitation(){
        //To stop test from automatically failing in the future due to expired date, users can provide date as short as "2025-10-04T23:59Z"
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(1);

        String json = String.format("""
                {
                "uses": 99,
                "role": "OWNER",
                "dueDate": "%s"
                }
                """, dueDate);
        var response = restClient.put().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.team2.getID(), testDataLoader.invitation1New.getUUID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});
        testDataLoader.refreshInvitation(testDataLoader.invitation1New);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invitation fully changed", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsInvitation(testDataLoader.refreshedInvitation));
        assertEquals(99, response.getBody().getData().getUses());
        //Some databases convert to UTC and strip offset, that is why compare we accept either solution as they ultimately point to same time
        assertTrue((dueDate.toString().equals(response.getBody().getData().getDueTime()) || (dueDate.toInstant().toString().equals(response.getBody().getData().getDueTime()))));

    }

    @Test
    void shouldPatchInvitation(){
        String json = """
                {
                "role": "MANAGER"
                }
                """;
        var response = restClient.patch().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.team2.getID(), testDataLoader.invitation1New.getUUID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwt1).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invitation updated", response.getBody().getMessage());
        assertEquals(UserRole.MANAGER, response.getBody().getData().getRole());
    }

    @Test
    void shouldDeleteInvitation(){
        var response = restClient.delete().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamToDelete.getID(), testDataLoader.invitationToDelete.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwt2).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invitation has been deleted", response.getBody().getMessage());
        assertThrows(NotFoundException.class,() -> testDataLoader.refreshInvitation(testDataLoader.invitationToDelete));
    }

}
