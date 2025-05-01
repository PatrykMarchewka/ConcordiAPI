package com.patrykmarchewka.concordiapi.DTO.UserDTO;

import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties()
public class UserRequestBody {
    @NotBlank(groups = OnCreate.class)
    private String login;
    @NotBlank(groups = OnCreate.class)
    private String password;

    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String lastName;


    public UserRequestBody(String login,String password, String name, String lastName){
        this.login = login;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
    }

    public UserRequestBody(){}

    public String getLogin(){return this.login;}
    public void setLogin(String login){this.login = login;}

    public String getPassword(){return this.password;}
    public void setPassword(String password){this.password = password;}

    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}

    public String getLastName(){return this.lastName;}
    public void setLastName(String lastName){this.lastName = lastName;}
}
