package com.patrykmarchewka.concordiapi.DatabaseModel;

public interface TeamTestHelper {
    default Team createTeam(TeamRepository teamRepository){
        Team team = new Team();
        team.setName("Testing");

        return teamRepository.save(team);
    }
}
