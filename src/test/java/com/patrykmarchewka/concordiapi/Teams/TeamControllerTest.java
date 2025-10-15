package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.APIResponse;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamAdminDTO;
import com.patrykmarchewka.concordiapi.Exceptions.ImpossibleStateException;
import com.patrykmarchewka.concordiapi.TestDataLoader;
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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeamControllerTest {

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
    void shouldGetTeams(){
        //JSON Deserialization sees APIResponse<Set> so it doesn't add "type" field to TeamDTO even when annotated to do it
        //because of that it has issues with deserializing Set<TeamDTO> into objects
        //In this test responses are TeamAdminDTO, but for mixed DTOs it would need to be Map<String, Object> or something better
        var response = restClient.get().uri("/api/teams").header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<Set<TeamAdminDTO>>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Information about all joined teams", response.getBody().getMessage());
        assertEquals(testDataLoader.userReadOwner.getTeams().size(), response.getBody().getData().size());
        assertTrue(response.getBody().getData().stream().anyMatch(teamDTO -> teamDTO.equalsTeam(testDataLoader.teamRead)));
    }

    @Test
    void shouldCreateTeam(){
        String json = """
                {
                "name":"NewestTeam"
                }
                """;

        var response = restClient.post().uri("/api/teams").contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(APIResponse.class);
        var refreshedUser = testDataLoader.refreshUser(testDataLoader.userWriteOwner);


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Team created with ID of "));
        assertEquals(testDataLoader.userWriteOwner.getTeams().size() + 1, refreshedUser.getTeams().size());
    }

    @Test
    void shouldGetInformationAboutTeam(){
        var response = restClient.get().uri("/api/teams/{teamID}", testDataLoader.teamRead.getID()).header("Authorization", "Bearer " + testDataLoader.jwtRead).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TeamAdminDTO>>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Information about the team", response.getBody().getMessage());
        assertTrue(response.getBody().getData().equalsTeam(testDataLoader.teamRead));
    }

    @Test
    void shouldPutTeam(){
        String json = """
                {
                "name":"newer"
                }
                """;

        var response = restClient.put().uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TeamAdminDTO>>() {});
        var refreshedTeam = testDataLoader.refreshTeam(testDataLoader.teamWrite);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Team has been edited", response.getBody().getMessage());
        assertEquals("newer", response.getBody().getData().getName());
        assertTrue(response.getBody().getData().equalsTeam(refreshedTeam));
    }

    @Test
    void shouldPatchTeam(){
        String json = """
                {
                "name":"newest"
                }
                """;

        var response = restClient.patch().uri("/api/teams/{teamID}", testDataLoader.teamWrite.getID()).contentType(MediaType.APPLICATION_JSON).body(json).header("Authorization", "Bearer " + testDataLoader.jwtWrite).retrieve().toEntity(new ParameterizedTypeReference<APIResponse<TeamAdminDTO>>() {});
        var refreshedTeam = testDataLoader.refreshTeam(testDataLoader.teamWrite);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Team has been edited", response.getBody().getMessage());
        assertEquals("newest", response.getBody().getData().getName());
        assertTrue(response.getBody().getData().equalsTeam(refreshedTeam));
    }

    @Test
    void shouldDeleteTeam(){
        var response = restClient.delete().uri("/api/teams/{teamID}", testDataLoader.teamDelete.getID()).header("Authorization", "Bearer " + testDataLoader.jwtDelete).retrieve().toEntity(APIResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The team has been disbanded", response.getBody().getMessage());
        assertThrows(ImpossibleStateException.class, () -> testDataLoader.refreshTeam(testDataLoader.teamDelete));
    }
}
