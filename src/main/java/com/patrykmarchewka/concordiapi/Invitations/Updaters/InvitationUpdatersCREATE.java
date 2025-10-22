package com.patrykmarchewka.concordiapi.Invitations.Updaters;

import com.patrykmarchewka.concordiapi.DTO.InvitationDTO.InvitationRequestBody;
import com.patrykmarchewka.concordiapi.DatabaseModel.Invitation;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class InvitationUpdatersCREATE {

    private final List<InvitationCREATEUpdater> updaters;
    private final List<InvitationCREATEUpdaterBasic> updaterBasics;
    private final List<InvitationCREATEUpdaterBasicWithTeam> updaterBasicWithTeams;

    @Autowired
    public InvitationUpdatersCREATE(List<InvitationCREATEUpdater> updaters, List<InvitationCREATEUpdaterBasic> updaterBasics, List<InvitationCREATEUpdaterBasicWithTeam> updaterBasicWithTeams) {
        this.updaters = updaters;
        this.updaterBasics = updaterBasics;
        this.updaterBasicWithTeams = updaterBasicWithTeams;
    }

    /**
     * Applies CREATE updates for the Invitation given the InvitationRequestBody, should only be called from {@link InvitationUpdatersService#createUpdate(Invitation, InvitationRequestBody, Supplier)}
     * @param invitation Invitation to create
     * @param body InvitationRequestBody with information to update
     */
    void applyCreateUpdates(Invitation invitation, InvitationRequestBody body, Supplier<Team> team){
        for (InvitationCREATEUpdater updater : updaters){
            updater.CREATEUpdate(invitation, body);
        }

        for (InvitationCREATEUpdaterBasicWithTeam updaterBasicWithTeam : updaterBasicWithTeams){
            updaterBasicWithTeam.setTeam(team.get());
            updaterBasicWithTeam.CREATEUpdate(invitation);
        }

        for (InvitationCREATEUpdaterBasic updaterBasic : updaterBasics){
            updaterBasic.CREATEUpdate(invitation);
        }
    }
}
