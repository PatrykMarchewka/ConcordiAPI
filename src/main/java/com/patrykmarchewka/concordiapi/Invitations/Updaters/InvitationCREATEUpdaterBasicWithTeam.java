package com.patrykmarchewka.concordiapi.Invitations.Updaters;

import com.patrykmarchewka.concordiapi.DatabaseModel.Team;

public interface InvitationCREATEUpdaterBasicWithTeam extends InvitationCREATEUpdaterBasic{
    void setTeam(Team team);
}
