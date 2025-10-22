package com.patrykmarchewka.concordiapi.Teams.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamUpdatersService {
    private final TeamUpdatersCREATE teamUpdatersCREATE;
    private final TeamUpdatersPUT teamUpdatersPUT;
    private final TeamUpdatersPATCH teamUpdatersPATCH;

    @Autowired
    public TeamUpdatersService(TeamUpdatersCREATE teamUpdatersCREATE, TeamUpdatersPUT teamUpdatersPUT, TeamUpdatersPATCH teamUpdatersPATCH) {
        this.teamUpdatersCREATE = teamUpdatersCREATE;
        this.teamUpdatersPUT = teamUpdatersPUT;
        this.teamUpdatersPATCH = teamUpdatersPATCH;
    }

    public void createUpdate(Team team, TeamRequestBody body){
        teamUpdatersCREATE.applyCreateUpdates(team, body);
    }

    public void putUpdate(Team team, TeamRequestBody body){
        teamUpdatersPUT.applyPutUpdates(team, body);
    }

    public void patchUpdate(Team team, TeamRequestBody body){
        teamUpdatersPATCH.applyPatchUpdates(team, body);
    }
}
