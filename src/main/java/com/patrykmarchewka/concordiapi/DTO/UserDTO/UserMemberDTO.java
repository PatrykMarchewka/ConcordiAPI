package com.patrykmarchewka.concordiapi.DTO.UserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserIdentity;

import java.util.Objects;

@JsonPropertyOrder({"ID", "Name", "Last name"})
public class UserMemberDTO implements UserDTO{
    private long id;
    private String name;
    private String lastName;


    public UserMemberDTO(UserIdentity user){
        this.id = user.getID();
        this.name = user.getName();
        this.lastName = user.getLastName();
    }

    public UserMemberDTO(){}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserMemberDTO userMemberDTO)) return false;
        return id == userMemberDTO.id &&
                Objects.equals(name, userMemberDTO.name) &&
                Objects.equals(lastName, userMemberDTO.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName);
    }


}
