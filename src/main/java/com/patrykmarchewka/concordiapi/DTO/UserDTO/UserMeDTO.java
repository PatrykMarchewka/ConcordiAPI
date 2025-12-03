package com.patrykmarchewka.concordiapi.DTO.UserDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserWithTeamRoles;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonPropertyOrder({"ID", "Name", "Last name", "Teams"})
public class UserMeDTO implements UserDTO{
    private long id;
    private String name;
    private String lastName;
    private Set<TeamMemberDTO> teams = new HashSet<>();


    public UserMeDTO(UserWithTeamRoles user){
        this.id = user.getID();
        this.name = user.getName();
        this.lastName = user.getLastName();
        for (Team team : user.getTeams()){
            this.teams.add(new TeamMemberDTO(team,user.getID()));
        }
    }

    public UserMeDTO(){}

    @Override
    @JsonProperty("ID")
    public long getID() {return id;}
    @Override
    public void setID(Long id) {this.id = id;}

    @Override
    @JsonProperty("Name")
    public String getName() {return name;}
    @Override
    public void setName(String name) {this.name = name;}

    @Override
    @JsonProperty("Last name")
    public String getLastName(){return lastName;}
    @Override
    public void setLastName(String lastName){this.lastName = lastName;}

    @JsonProperty("Teams")
    public Set<TeamMemberDTO> getTeams(){return teams;}
    public void setTeams(Set<TeamMemberDTO> teams){this.teams = teams;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof UserMeDTO userMeDTO)) return false;
        return id == userMeDTO.id &&
                Objects.equals(name, userMeDTO.name) &&
                Objects.equals(lastName, userMeDTO.lastName) &&
                Objects.equals(teams, userMeDTO.teams);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id,name,lastName, teams);
    }
}
