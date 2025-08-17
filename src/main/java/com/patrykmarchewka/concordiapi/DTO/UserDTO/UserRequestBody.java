package com.patrykmarchewka.concordiapi.DTO.UserDTO;

import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.OnPut;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequestBody {
    @NotNull(groups = {OnCreate.class, OnPut.class}, message = "{notnull.login.generic}")
    @NotBlank(groups = {OnCreate.class, OnPut.class}, message = "{notblank.login.generic}")
    @Size(min = 1,max = 255, message = "{size.generic}")
    private String login;

    @NotNull(groups = {OnCreate.class, OnPut.class}, message = "{notnull.password.generic}")
    @NotBlank(groups = {OnCreate.class, OnPut.class}, message = "{notblank.password.generic}")
    @Size(min = 1,max = 255, message = "{size.generic}")
    private String password;

    @NotNull(groups = {OnCreate.class, OnPut.class},message = "{notnull.name.generic}")
    @NotBlank(groups = {OnCreate.class, OnPut.class},message = "{notblank.name.generic}")
    @Size(min = 1,max = 255, message = "{size.generic}")
    private String name;

    @NotNull(groups = {OnCreate.class, OnPut.class}, message = "{user.lastName.notnull}")
    @NotBlank(groups = {OnCreate.class, OnPut.class}, message = "{user.lastName.notblank}")
    @Size(min = 1,max = 255, message = "{size.generic}")
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
