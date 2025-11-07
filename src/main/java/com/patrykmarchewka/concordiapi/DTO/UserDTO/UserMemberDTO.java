package com.patrykmarchewka.concordiapi.DTO.UserDTO;
import com.patrykmarchewka.concordiapi.HydrationContracts.User.UserIdentity;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserMemberDTO userMemberDTO)) return false;
        return Objects.equals(id, userMemberDTO.getID()) &&
                Objects.equals(name, userMemberDTO.getName()) &&
                Objects.equals(lastName, userMemberDTO.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lastName);
    }


}
