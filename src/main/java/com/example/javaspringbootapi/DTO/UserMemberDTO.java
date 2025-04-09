package com.example.javaspringbootapi.DTO;
import com.example.javaspringbootapi.DatabaseModel.User;

public class UserMemberDTO {
    private long id;
    private String name;
    private String lastName;


    public UserMemberDTO(User user){
        this.id = user.getID();
        this.name = user.getName();
        this.lastName = user.getLastName();
    }

    @Override
    public String toString(){
        return id + ":" + name + " " + lastName;
    }

    public UserMemberDTO(){}

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getLastName(){return lastName;}
    public void setLastName(String lastName){this.lastName = lastName;}
}
