package com.patrykmarchewka.concordiapi.DTO.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestLogin {
    @NotBlank(message = "{notblank.generic}")
    @Size(min = 1,max = 255, message = "{size.generic}")
    private String login;

    @NotBlank(message = "{notblank.generic}")
    @Size(min = 1,max = 255, message = "{size.generic}")
    private String password;


    public UserRequestLogin(String login,String password){
        this.login = login;
        this.password = password;
    }

    public UserRequestLogin(){}

    public String getLogin(){return this.login;}
    public void setLogin(String login){this.login = login;}

    public String getPassword(){return this.password;}
    public void setPassword(String password){this.password = password;}
}
