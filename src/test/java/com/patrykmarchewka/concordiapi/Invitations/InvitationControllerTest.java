package com.patrykmarchewka.concordiapi.Invitations;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
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
import java.time.ZoneOffset;
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
        var response = restClient.get().uri("/api/teams/{teamID}/invitations", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<InvitationManagerDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("List of all invitations for this team", response.getBody().getMessage());
        assertEquals(3, response.getBody().getData().size());
        assertTrue(response.getBody().getData().stream().anyMatch(invitationManagerDTO -> invitationManagerDTO.equalsInvitation(testDataLoader.invitationRead)));
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
        var response = restClient.post().uri("/api/teams/{teamID}/invitations", testDataLoader.teamWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});
        testDataLoader.teamWrite = testDataLoader.refreshTeam(testDataLoader.teamWrite);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Created new invitation", response.getBody().getMessage());
        assertEquals(new TeamMemberDTO(testDataLoader.teamWrite), response.getBody().getData().getTeam());
        assertEquals((short)100, response.getBody().getData().getUses());
        assertEquals(UserRole.MEMBER, response.getBody().getData().getRole());
    }

    @Test
    void shouldCreateInvitationString() {
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(10);

        String json = String.format("""
                {
                "uses": 100,
                "role": "MEMBER",
                "dueDate": "%s"
                }
                """, dueDate);
        var response = restClient.post().uri("/api/teams/{teamID}/invitations", testDataLoader.teamWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().body(String.class);
        /*
        Example Response:
        {"message":"Created new invitation",
        "data":{"UUID":"b4d3e5c9-ee3f-4526-8e39-fa2ff48d25a0",
        "team":{"id":2,"name":"teamWrite","teammateCount":4,"tasks":[],"owners":[{"id":2,"name":"WRITE","lastName":"WRITE"}]},
        "role":"MEMBER",
        "uses":100,
        "dueTime":"2025-11-24 18:25:29+00:00"},
        "timestamp":"2025-11-14 19:25:29+01:00"}
         */

        String expectedMessage = "\"message\":\"Created new invitation\"";
        String expectedRole = "\"role\":\"MEMBER\"";
        String expectedUses = "\"uses\":100";
        String expectedDueDate = "\"dueTime\":\"" + OffsetDateTimeConverter.formatDate(dueDate.withOffsetSameInstant(ZoneOffset.UTC)) + "\"";

        assertTrue(response.contains(expectedMessage));
        assertTrue(response.contains(expectedRole));
        assertTrue(response.contains(expectedUses));
        assertTrue(response.contains(expectedDueDate));
    }

    @Test
    void shouldCheckInvitation(){
        var response = restClient.get().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamRead.getID(), testDataLoader.invitationRead.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Information about this invitation", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsInvitation(testDataLoader.invitationRead));
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
        var response = restClient.put().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});
        testDataLoader.invitationWrite = testDataLoader.refreshInvitation(testDataLoader.invitationWrite);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invitation fully changed", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsInvitation(testDataLoader.invitationWrite));
        assertEquals(99, response.getBody().getData().getUses());
    }

    @Test
    void shouldPutInvitationString(){
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(1);

        String json = String.format("""
                {
                "uses": 99,
                "role": "OWNER",
                "dueDate": "%s"
                }
                """, dueDate);
        var response = restClient.put().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().body(String.class);

        /*
        Example response:
        {"message":"Invitation fully changed",
        "data":{"UUID":"87e84bdc-913d-4793-bb1f-9aa9c08a644c",
        "team":{"id":2,"name":"teamWrite","teammateCount":4,"tasks":[],"owners":[{"id":2,"name":"WRITE","lastName":"WRITE"}]},
        "role":"OWNER",
        "uses":99,
        "dueTime":"2025-11-15 18:34:12+00:00"},
        "timestamp":"2025-11-14 19:34:12+01:00"}
         */

        String expectedMessage = "\"message\":\"Invitation fully changed\"";
        String expectedRole = "\"role\":\"OWNER\"";
        String expectedUses = "\"uses\":99";
        String expectedDueTime = "\"dueTime\":\"" + OffsetDateTimeConverter.formatDate(dueDate.withOffsetSameInstant(ZoneOffset.UTC)) + "\"";

        assertTrue(response.contains(expectedMessage));
        assertTrue(response.contains(expectedRole));
        assertTrue(response.contains(expectedUses));
        assertTrue(response.contains(expectedDueTime));

    }

    @Test
    void shouldPatchInvitation(){
        String json = """
                {
                "role": "MANAGER"
                }
                """;
        var response = restClient.patch().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invitation updated", response.getBody().getMessage());
        assertEquals(UserRole.MANAGER, response.getBody().getData().getRole());
    }

    @Test
    void shouldDeleteInvitation(){
        var response = restClient.delete().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamDelete.getID(), testDataLoader.invitationDelete.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwtDelete).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invitation has been deleted", response.getBody().getMessage());
        assertThrows(NotFoundException.class,() -> testDataLoader.refreshInvitation(testDataLoader.invitationDelete));
    }

}
