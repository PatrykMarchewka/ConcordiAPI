package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.patrykmarchewka.concordiapi.UserRole;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

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
    private UserRole role;
    @Column(nullable = true)
    private OffsetDateTime dueTime;

    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public Team getTeam(){return this.team;}
    public void setTeam(Team team){this.team = team;}

    public short getUses(){return uses;}
    public void setUses(short uses){this.uses = uses;}

    public UserRole getRole() {return role;}
    public void setRole(UserRole role) {this.role = role;}

    public OffsetDateTime getDueTime() {return dueTime;}
    public void setDueTime(OffsetDateTime dueTime) {this.dueTime = dueTime;}

    public void useOne() throws Exception {
        if ((this.getDueTime() != null && OffsetDateTime.now().isAfter(this.getDueTime())) || this.getUses() <= 0){
            throw new Exception("Invitation expired");
        }
        else{
            this.uses -= 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invitation)) return false;
        Invitation inv = (Invitation) o;
        return UUID != null && UUID.equals(inv.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(UUID);
    }
}
