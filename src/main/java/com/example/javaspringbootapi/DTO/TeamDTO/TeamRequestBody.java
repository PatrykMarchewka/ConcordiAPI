package com.example.javaspringbootapi.DTO.TeamDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties()
public class TeamRequestBody {
    @NotBlank
    private String name;

    public TeamRequestBody(String name){
        this.name = name;
    }

    public TeamRequestBody(){}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}
