package com.patrykmarchewka.concordiapi.Teams.Updaters;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.UpdateType;
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

    public void update(Team team, TeamRequestBody body, UpdateType type){
        switch (type){
            case CREATE -> teamUpdatersCREATE.applyCreateUpdates(team, body);
            case PUT -> teamUpdatersPUT.applyPutUpdates(team, body);
            case PATCH -> teamUpdatersPATCH.applyPatchUpdates(team, body);
            case null, default -> throw new BadRequestException("Called update type that isn't CREATE/PUT/PATCH");
        }
    }
}
