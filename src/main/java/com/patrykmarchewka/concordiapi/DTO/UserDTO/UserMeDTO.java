package com.patrykmarchewka.concordiapi.DTO.UserDTO;

import com.patrykmarchewka.concordiapi.DTO.TeamDTO.TeamMemberDTO;
import com.patrykmarchewka.concordiapi.DatabaseModel.Team;
import com.patrykmarchewka.concordiapi.DatabaseModel.User;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserMeDTO implements UserDTO{
    private long id;
    private String name;
    private String lastName;
    private Set<TeamMemberDTO> teams = new HashSet<>();


    public UserMeDTO(User user){
        this.id = user.getID();
        this.name = user.getName();
        this.lastName = user.getLastName();
        for (Team team : user.getTeams()){
            this.teams.add(new TeamMemberDTO(team,user.getID()));
        }
    }

    public UserMeDTO(){}

    @Override
    public long getID() {return id;}
    @Override
    public void setID(Long id) {this.id = id;}

    @Override
    public String getName() {return name;}
    @Override
    public void setName(String name) {this.name = name;}

    @Override
    public String getLastName(){return lastName;}
    @Override
    public void setLastName(String lastName){this.lastName = lastName;}

    public Set<TeamMemberDTO> getTeams(){return teams;}
    public void setTeams(Set<TeamMemberDTO> teams){this.teams = teams;}

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof UserMeDTO userMeDTO)) return false;
        return Objects.equals(id, userMeDTO.getID()) &&
                Objects.equals(name, userMeDTO.getName()) &&
                Objects.equals(lastName, userMeDTO.getLastName()) &&
                Objects.equals(teams, userMeDTO.getTeams());
    }

    @Override
    public int hashCode(){
        return Objects.hash(id,name,lastName);
    }
}
