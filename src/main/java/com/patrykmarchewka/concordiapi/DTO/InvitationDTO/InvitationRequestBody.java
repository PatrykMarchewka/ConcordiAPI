package com.patrykmarchewka.concordiapi.DTO.InvitationDTO;

import com.patrykmarchewka.concordiapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Future;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;

@JsonIgnoreProperties()
public class InvitationRequestBody {
    private Short uses = 1;
    private PublicVariables.UserRole role = PublicVariables.UserRole.MEMBER;
    private long teamID;
    @Future
    private OffsetDateTime dueDate;


    public InvitationRequestBody(@Nullable Short uses, @Nullable PublicVariables.UserRole role, long teamID, @Nullable OffsetDateTime dueDate){
        this.uses = (uses != null) ? uses : 1;
        this.role = (role != null) ? role : PublicVariables.UserRole.MEMBER;
        this.teamID = teamID;
        this.dueDate = dueDate;
    }


    public InvitationRequestBody(){}

    public Short getUses() {return uses;}
    public void setUses(Short uses) {this.uses = uses;}

    public PublicVariables.UserRole getRole() {return role;}
    public void setRole(PublicVariables.UserRole role) {this.role = role;}

    public long getTeamID() {return teamID;}
    public void setTeamID(long teamID) {this.teamID = teamID;}

    public OffsetDateTime getDueDate() {return dueDate;}
    public void setDueDate(OffsetDateTime dueDate) {this.dueDate = dueDate;}
}
