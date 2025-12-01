package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;
import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.OnPut;
import com.patrykmarchewka.concordiapi.UserRole;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;


public class InvitationRequestBody {
    @NotNull(groups = {OnCreate.class, OnPut.class}, message = "{notnull.generic}")
    @Min(value = 1, message = "{min.generic}")
    @Max(value = 32767, message = "{max.generic}")
    private Short uses;

    @NotNull(groups = {OnCreate.class, OnPut.class}, message = "{notnull.generic}")
    private UserRole role;

    @Future(message = "{future.generic}")
    private OffsetDateTime dueDate;

    public InvitationRequestBody(Short uses, UserRole role, @Nullable OffsetDateTime dueDate){
        this.uses = uses;
        this.role = role;
        this.dueDate = dueDate;
    }


    public InvitationRequestBody(){}

    public Short getUses() {return uses;}
    public void setUses(Short uses) {this.uses = uses;}

    public UserRole getRole() {return role;}
    public void setRole(UserRole role) {this.role = role;}

    public OffsetDateTime getDueDate() {return dueDate;}
    public void setDueDate(OffsetDateTime dueDate) {this.dueDate = dueDate;}
}