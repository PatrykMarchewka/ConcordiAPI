package com.example.javaspringbootapi.DTO.UserDTO;

import com.example.javaspringbootapi.DTO.TeamDTO.TeamMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;

import java.util.HashSet;
import java.util.Set;

public class UserMeDTO {
    private long id;
    private String name;
    private String lastName;
    private Set<TeamMemberDTO> teams = new HashSet<>();


    public UserMeDTO(User user){
        this.id = user.getID();
        this.name = user.getName();
        this.lastName = user.getLastName();
        for (Team team : user.getTeams()){
            this.teams.add(new TeamMemberDTO(team,user));
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
