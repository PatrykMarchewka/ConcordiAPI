package com.patrykmarchewka.concordiapi.Teams;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;

public interface TeamRequestBodyHelper {
    default TeamRequestBody createTeamRequestBody(String name){
        TeamRequestBody body = new TeamRequestBody();
        body.setName(name);
        return body;
    }
}
