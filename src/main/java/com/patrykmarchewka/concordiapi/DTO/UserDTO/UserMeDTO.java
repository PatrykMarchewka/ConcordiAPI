package com.patrykmarchewka.concordiapi.DTO.UserDTO;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;
import com.patrykmarchewka.concordiapi.Teams.TeamUserRoleService;

import java.util.HashSet;
import java.util.Set;

public class UserMeDTO {
    private long id;
    private String name;
    private String lastName;
    private Set<TeamMemberDTO> teams = new HashSet<>();


    public UserMeDTO(User user, TeamUserRoleService teamUserRoleService){
        this.id = user.getID();
        this.name = user.getName();
        this.lastName = user.getLastName();
        for (Team team : user.getTeams()){
            this.teams.add(new TeamMemberDTO(team,user,teamUserRoleService));
        }
    }

    public UserMeDTO(){}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName = lastName;}

    public Set<TeamMemberDTO> getTeams(){return teams;}
    public void setTeams(Set<TeamMemberDTO> teams){this.teams = teams;}
}
