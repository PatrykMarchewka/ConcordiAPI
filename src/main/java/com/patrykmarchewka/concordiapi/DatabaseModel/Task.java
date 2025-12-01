package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.patrykmarchewka.concordiapi.HydrationContracts.Task.TaskFull;
import com.patrykmarchewka.concordiapi.OffsetDateTimeConverter;
import com.patrykmarchewka.concordiapi.TaskStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Tasks")
public class Task implements TaskFull {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus taskStatus;
    @Column(nullable = false)
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime creationDate;
    @Convert(converter = OffsetDateTimeConverter.class)
    private OffsetDateTime updateDate;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "task")
    @Column(nullable = false)
    private Set<Subtask> subtasks = new HashSet<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "assignedTask")
    @Column(nullable = false)
    private Set<UserTask> userTasks = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team assignedTeam;

    @Override
    public long getID() {
        return id;
    }

    @Override
    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }
    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public OffsetDateTime getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(OffsetDateTime creationDate) {this.creationDate = creationDate;}

    @Override
    public OffsetDateTime getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(OffsetDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public Set<Subtask> getSubtasks() {
        return subtasks;
    }
    public void setSubtasks(Set<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public Set<UserTask> getUserTasks() { return userTasks; }
    public void setUserTasks(Set<UserTask> userTasks) { this.userTasks = userTasks; }

    @Override
    public Team getAssignedTeam(){ return this.assignedTeam; }
    public void setAssignedTeam(Team assignedTeam){ this.assignedTeam = assignedTeam; }


    public Subtask addSubtask(Subtask subtask){
        subtask.setTask(this);
        this.subtasks.add(subtask);
        return subtask;
    }
    public void removeSubtask(Subtask subtask){
        subtask.setTask(null);
        this.subtasks.remove(subtask);
    }

    public UserTask addUserTask(User user){
        UserTask userTask = new UserTask(user,this);

        user.addUserTask(userTask);
        this.userTasks.add(userTask);

        return userTask;
    }

    public void removeUserTask(UserTask userTask){
        userTask.getAssignedUser().removeUserTask(userTask);
        this.userTasks.remove(userTask);

        userTask.setAssignedTask(null);
        userTask.setAssignedUser(null);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return id != null && id.equals(task.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }









}
