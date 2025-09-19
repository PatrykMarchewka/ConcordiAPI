package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.patrykmarchewka.concordiapi.Exceptions.BadRequestException;
import com.patrykmarchewka.concordiapi.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "Invitations")
public class Invitation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String UUID;
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team invitingTeam;
    @Column(nullable = false)
    private short uses;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    private OffsetDateTime dueTime;

    public String getUUID(){return this.UUID;}
    public void setUUID(String UUID){this.UUID = UUID;}

    public Team getInvitingTeam(){return this.invitingTeam;}
    public void setInvitingTeam(Team invitingTeam){this.invitingTeam = invitingTeam;}

    public short getUses(){return uses;}
    public void setUses(short uses){this.uses = uses;}

    public UserRole getRole() {return role;}
    public void setRole(UserRole role) {this.role = role;}

    public OffsetDateTime getDueTime() {return dueTime;}
    public void setDueTime(OffsetDateTime dueTime) {this.dueTime = dueTime;}

    public void useOne() {
        if ((this.getDueTime() != null && OffsetDateTime.now().isAfter(this.getDueTime())) || this.getUses() <= 0){
            throw new BadRequestException("Invitation expired");
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
