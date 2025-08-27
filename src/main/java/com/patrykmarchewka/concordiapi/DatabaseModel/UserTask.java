package com.patrykmarchewka.concordiapi.DatabaseModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Objects;

@Entity
@Table(name = "Users_Tasks", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","task_id"}))
public class UserTask {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User assignedUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task assignedTask;


    public UserTask(){}

    public UserTask(User assignedUser, Task assignedTask){
        this.assignedUser = assignedUser;
        this.assignedTask = assignedTask;
    }



    public Long getID() { return id; }
    public void setID(Long id) { this.id = id; }

    public User getAssignedUser() { return assignedUser; }
    public void setAssignedUser(User assignedUser) { this.assignedUser = assignedUser; }

    public Task getAssignedTask() { return assignedTask; }
    public void setAssignedTask(Task assignedTask) { this.assignedTask = assignedTask; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserTask)) return false;
        UserTask userTask = (UserTask) o;
        return id != null && id.equals(userTask.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
