package com.patrykmarchewka.concordiapi.DTO.TeamDTO;

import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.OnPut;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TeamRequestBody {
    @NotNull(groups = {OnCreate.class, OnPut.class},message = "{notnull.generic}")
    @NotBlank(groups = {OnCreate.class, OnPut.class},message = "{notblank.generic}")
    @Size(min = 1, max = 255, message = "{size.generic}")
    private String name;

    public TeamRequestBody(String name){
        this.name = name;
    }

    public TeamRequestBody(){}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
}
