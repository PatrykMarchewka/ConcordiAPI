package com.patrykmarchewka.concordiapi.DTO.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties()
public class UserRequestLogin {
    @NotBlank
    private String login;
    @NotBlank
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
