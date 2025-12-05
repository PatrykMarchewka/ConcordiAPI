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
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvitationControllerTest {

    @LocalServerPort
    private int port;

    private final RestClient.Builder builder;
    private final TestDataLoader testDataLoader;
    private RestClient restClient;

    @Autowired
    public InvitationControllerTest(final RestClient.Builder builder, final TestDataLoader testDataLoader){
        this.builder = builder;
        this.testDataLoader = testDataLoader;
    }


    @BeforeAll
    void restInitialize(){
        this.restClient = builder.baseUrl("http://localhost:" + port).build();
        testDataLoader.loadDataForTests();
    }

    @AfterAll
    void cleanUp(){
        testDataLoader.clearDB();
    }


    /// getInvitations

    /// 200
    @Test
    void shouldGetInvitations(){
        var response = restClient.get().uri("/api/teams/{teamID}/invitations", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<InvitationManagerDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("List of all invitations for this team", response.getBody().getMessage());
        assertEquals(3, response.getBody().getData().size());
        assertEquals(Set.of(testDataLoader.invitationRead.getUUID(), testDataLoader.invitationNoUses.getUUID(), testDataLoader.invitationExpired.getUUID()), response.getBody().getData().stream().map(InvitationManagerDTO::getUUID).collect(Collectors.toUnmodifiableSet()));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetInvitations(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/invitations", testDataLoader.teamRead.getID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }



    /// 403
    @Test
    void shouldThrowForNoPrivilegesGetInvitations(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/invitations", testDataLoader.teamRead.getID())
                .header("Authorization", "Bearer " + testDataLoader.jwtBanned)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }


    /// 404
    @Test
    void shouldThrowForInvalidTeamIDGetInvitations(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/invitations", -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());

    }

    /// createInvitation

    /// 201
    @Test
    void shouldCreateInvitation(){
        //To stop test from automatically failing in the future due to expired date, users can provide date as short as "2025-10-04T23:59Z"
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(10).withOffsetSameInstant(ZoneOffset.UTC);

        String json = String.format("""
                {
                "uses": 100,
                "role": "MEMBER",
                "dueDate": "%s"
                }
                """, dueDate);
        var response = restClient.post().uri("/api/teams/{teamID}/invitations", testDataLoader.teamWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Created new invitation", response.getBody().getMessage());
        assertEquals((short)100, response.getBody().getData().getUses());
        assertEquals(UserRole.MEMBER, response.getBody().getData().getRole());
        assertEquals(OffsetDateTimeConverter.formatDate(dueDate), response.getBody().getData().getDueTimeString());
    }

    @Test
    void shouldCreateInvitationForShortDateString(){
        //To stop test from automatically failing in the future due to expired date, users can provide date as short as "2025-10-04T23:59Z"
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(10).withOffsetSameInstant(ZoneOffset.UTC);
        String dueDateString = dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
        String json = String.format("""
                {
                "uses": 100,
                "role": "MEMBER",
                "dueDate": "%s"
                }
                """, dueDateString);
        var response = restClient.post()
                .uri("/api/teams/{teamID}/invitations", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(String.format("\"Due time\":\"%s\"", OffsetDateTimeConverter.formatDate(dueDate))));
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyCreateInvitation(){
        String json = String.format("""
                {
                "role": "MEMBER"
                }
                """);
        var response = restClient.post()
                .uri("/api/teams/{teamID}/invitations", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        LinkedHashMap<String, String> errors = (LinkedHashMap<String, String>) response.getBody().getData();
        assertEquals("Field cannot be null", errors.get("uses"));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationCreateInvitation(){
        String json = String.format("""
                {
                "uses": 100,
                "role": "MEMBER"
                }
                """);
        var response = restClient.post()
                .uri("/api/teams/{teamID}/invitations", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                .status(res.getStatusCode())
                .headers(res.getHeaders())
                .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesCreateInvitation(){
        String json = String.format("""
                {
                "uses": 100,
                "role": "MEMBER"
                }
                """);
        var response = restClient.post()
                .uri("/api/teams/{teamID}/invitations", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForTooHighRoleCreateInvitation(){
        String json = String.format("""
                {
                "uses": 100,
                "role": "OWNER"
                }
                """);

        var response = restClient.post()
                .uri("/api/teams/{teamID}/invitations", testDataLoader.teamWrite.getID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtAdmin)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDCreateInvitation(){
        String json = String.format("""
                {
                "uses": 100,
                "role": "MEMBER"
                }
                """);
        var response = restClient.post()
                .uri("/api/teams/{teamID}/invitations", -1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// getInvitation

    /// 200
    @Test
    void shouldGetInvitation(){
        var response = restClient.get().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamRead.getID(), testDataLoader.invitationRead.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Information about this invitation", response.getBody().getMessage());
        assertEquals(testDataLoader.invitationRead.getUUID(), response.getBody().getData().getUUID());
        assertEquals(testDataLoader.invitationRead.getInvitingTeam().getID(), response.getBody().getData().getTeam().getID());
        assertEquals(testDataLoader.invitationRead.getUses(), response.getBody().getData().getUses());
        assertEquals(testDataLoader.invitationRead.getRole(), response.getBody().getData().getRole());
        assertEquals(OffsetDateTimeConverter.formatDate(testDataLoader.invitationRead.getDueTime()), response.getBody().getData().getDueTimeString());
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationGetInvitation(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamRead.getID(), testDataLoader.invitationRead.getUUID())
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesGetInvitation(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamRead.getID(), testDataLoader.invitationRead.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());

    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDGetInvitation(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/invitations/{invID}", -1, testDataLoader.invitationRead.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidInvitationUUIDGetInvitation(){
        var response = restClient.get()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamRead.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtRead)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// putInvitation

    /// 200
    @Test
    void shouldPutInvitation(){
        //To stop test from automatically failing in the future due to expired date, users can provide date as short as "2025-10-04T23:59Z"
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(1).withOffsetSameInstant(ZoneOffset.UTC);

        String json = String.format("""
                {
                "uses": 99,
                "role": "OWNER",
                "dueDate": "%s"
                }
                """, dueDate);
        var response = restClient.put().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<InvitationManagerDTO>>() {});


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invitation fully changed", response.getBody().getMessage());
        assertEquals(testDataLoader.invitationWrite.getUUID(), response.getBody().getData().getUUID());
        assertEquals(99, response.getBody().getData().getUses());
        assertEquals(UserRole.OWNER, response.getBody().getData().getRole());
        assertEquals(OffsetDateTimeConverter.formatDate(dueDate), response.getBody().getData().getDueTimeString());
    }

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyPutInvitation(){
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(1);
        String json = String.format("""
                {
                "uses": 99,
                "dueDate": "%s"
                }
                """, dueDate);

        var response = restClient.put()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        LinkedHashMap<String, String> errors = (LinkedHashMap<String, String>) response.getBody().getData();
        assertEquals("Field cannot be null", errors.get("role"));

    }


    /// 401
    @Test
    void shouldThrowForNoAuthenticationPutInvitation(){
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(1);
        String json = String.format("""
                {
                "uses": 99,
                "role": "OWNER",
                "dueDate": "%s"
                }
                """, dueDate);

        var response = restClient.put()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesPutInvitation(){
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(1);
        String json = String.format("""
                {
                "uses": 99,
                "role": "OWNER",
                "dueDate": "%s"
                }
                """, dueDate);

        var response = restClient.put()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForTooHighRolePutInvitation(){
        String json = String.format("""
                {
                "uses": 100,
                "role": "OWNER"
                }
                """);

        var response = restClient.put()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtAdmin)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDPutInvitation(){
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(1);
        String json = String.format("""
                {
                "uses": 99,
                "role": "OWNER",
                "dueDate": "%s"
                }
                """, dueDate);
        var response = restClient.put()
                .uri("/api/teams/{teamID}/invitations/{invID}", -1, testDataLoader.invitationWrite.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidInvitationUUIDPutInvitation(){
        OffsetDateTime dueDate = OffsetDateTimeConverter.nowConverted().plusDays(1);
        String json = String.format("""
                {
                "uses": 99,
                "role": "OWNER",
                "dueDate": "%s"
                }
                """, dueDate);
        var response = restClient.put()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// patchInvitation

    /// 200
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

    /// 400
    @Test
    void shouldThrowForInvalidRequestBodyPatchInvitation(){
        String json = """
                {
                "uses": -1
                }
                """;
        var response = restClient.patch()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        LinkedHashMap<String, String> errors = (LinkedHashMap<String, String>) response.getBody().getData();
        assertEquals("Value must be 1 or greater", errors.get("uses"));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationPatchInvitation(){
        String json = String.format("""
                {
                "uses": 99
                }
                """);

        var response = restClient.patch()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesPatchInvitation(){
        String json = String.format("""
                {
                "uses": 99
                }
                """);

        var response = restClient.patch()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForTooHighRolePatchInvitation(){
        String json = String.format("""
                {
                "uses": 100,
                "role": "OWNER"
                }
                """);

        var response = restClient.patch()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .header("Authorization", "Bearer " + testDataLoader.jwtAdmin)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDPatchInvitation(){
        String json = String.format("""
                {
                "uses": 99
                }
                """);

        var response = restClient.patch()
                .uri("/api/teams/{teamID}/invitations/{invID}", -1, testDataLoader.invitationWrite.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidInvitationUUIDPatchInvitation(){
        String json = String.format("""
                {
                "uses": 99
                }
                """);

        var response = restClient.patch()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// deleteInvitation

    /// 200
    @Test
    void shouldDeleteInvitation(){
        var response = restClient.delete().uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamDelete.getID(), testDataLoader.invitationDelete.getUUID()).header("Authorization", "Bearer " + testDataLoader.jwtDelete).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invitation has been deleted", response.getBody().getMessage());
        assertThrows(NotFoundException.class,() -> testDataLoader.refreshInvitation(testDataLoader.invitationDelete));
    }

    /// 401
    @Test
    void shouldThrowForNoAuthenticationDeleteInvitation(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .exchange((req, res) -> ResponseEntity
                .status(res.getStatusCode())
                .headers(res.getHeaders())
                .body(res.bodyTo(String.class)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("{\"error\": \"You are not authenticated\"}", response.getBody());
    }

    /// 403
    @Test
    void shouldThrowForNoPrivilegesDeleteInvitation(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), testDataLoader.invitationWrite.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtMember)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("You are not authorized to do that action", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    /// 404
    @Test
    void shouldThrowForInvalidTeamIDDeleteInvitation(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/invitations/{invID}", -1, testDataLoader.invitationWrite.getUUID())
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void shouldThrowForInvalidInvitationUUIDDeleteInvitation(){
        var response = restClient.delete()
                .uri("/api/teams/{teamID}/invitations/{invID}", testDataLoader.teamWrite.getID(), -1)
                .header("Authorization", "Bearer " + testDataLoader.jwtWrite)
                .exchange((req, res) -> ResponseEntity
                        .status(res.getStatusCode())
                        .headers(res.getHeaders())
                        .body(res.bodyTo(APIResponse.class)));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Provided resource was not found on the server", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }
}
