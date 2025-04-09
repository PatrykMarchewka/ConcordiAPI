package com.example.javaspringbootapi.DatabaseModel;

import com.example.javaspringbootapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "Invitations")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String UUID;
    @ManyToOne
    @JsonBackReference
    private Team team;
    private short uses;
    @Enumerated(EnumType.STRING)
    private PublicVariables.UserRole role;
    @Column(nullable = true,columnDefinition = "DATETIME2")
    private ZonedDateTime dueTime;
    //TODO: Test ZonedDateTime and possibly revert other Dates to it

    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public Team getTeam(){return this.team;}
    public void setTeam(Team team){this.team = team;}

    public short getUses(){return uses;}
    public void setUses(short uses){this.uses = uses;}

    public PublicVariables.UserRole getRole() {return role;}
    public void setRole(PublicVariables.UserRole role) {this.role = role;}

    public ZonedDateTime getDueTime() {return dueTime;}
    public void setDueTime(ZonedDateTime dueTime) {this.dueTime = dueTime;}

    public void useOne() throws Exception {
        if ((this.getDueTime() != null && ZonedDateTime.now().isAfter(this.getDueTime())) || this.getUses() <= 0){
            throw new Exception("Invitation expired");
        }
        else{
            this.uses -= 1;
        }
    }
}
