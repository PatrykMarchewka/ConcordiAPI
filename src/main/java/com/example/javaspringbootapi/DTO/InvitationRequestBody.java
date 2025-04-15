package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.PublicVariables;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;

public class InvitationRequestBody {
    private Short uses = 1;
    private PublicVariables.UserRole role = PublicVariables.UserRole.MEMBER;
    @Future
    private OffsetDateTime dueDate;


    public InvitationRequestBody(@Nullable Short uses, @Nullable PublicVariables.UserRole role, @Nullable OffsetDateTime dueDate){
        this.uses = (uses != null) ? uses : 1;
        this.role = (role != null) ? role : PublicVariables.UserRole.MEMBER;
        this.dueDate = dueDate;
    }


    public InvitationRequestBody(){}

    public Short getUses() {return uses;}
    public void setUses(Short uses) {this.uses = uses;}

    public PublicVariables.UserRole getRole() {return role;}
    public void setRole(PublicVariables.UserRole role) {this.role = role;}

    public OffsetDateTime getDueDate() {return dueDate;}
    public void setDueDate(OffsetDateTime dueDate) {this.dueDate = dueDate;}
}
