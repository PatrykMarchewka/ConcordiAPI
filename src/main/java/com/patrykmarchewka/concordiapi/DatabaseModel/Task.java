package com.patrykmarchewka.concordiapi.DatabaseModel;
import com.patrykmarchewka.concordiapi.TaskStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import java.util.stream.Collectors;

@Entity
@Table(name = "Tasks")
public class Task {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus taskStatus;
    @Column(nullable = false)
    private OffsetDateTime creationDate;
    private OffsetDateTime updateDate;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "task")
    @Column(nullable = false)
    private Set<Subtask> subtasks = new HashSet<>();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, mappedBy = "assignedTask")
    @Column(nullable = false)
    private Set<UserTask> userTasks = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team assignedTeam;

    public long getID() {
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(OffsetDateTime creationDate) {this.creationDate = creationDate;}

    public OffsetDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(OffsetDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Set<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Set<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public Set<UserTask> getUserTasks() { return userTasks; }
    public Set<User> getUsers() {return userTasks.stream().map(UserTask::getAssignedUser).collect(Collectors.toUnmodifiableSet());}
    public boolean hasUser(User user){return userTasks.stream().map(UserTask::getAssignedUser).anyMatch(u -> u.equals(user));}
    public void setUserTasks(Set<UserTask> userTasks) { this.userTasks = userTasks; }

    public Team getAssignedTeam(){ return this.assignedTeam; }
    public void setAssignedTeam(Team assignedTeam){ this.assignedTeam = assignedTeam; }





    public Subtask addSubtask(Subtask subtask){
        subtask.setTask(this);
        this.subtasks.add(subtask);
        return subtask;
    }
    public void removeSubtask(Subtask subtask){this.subtasks.remove(subtask);}

    public UserTask addUserTask(User user){
        UserTask userTask = new UserTask(user,this);

        user.addUserTask(userTask);
        this.userTasks.add(userTask);

        return userTask;
    }

    public void removeUserTask(UserTask userTask){
        userTask.getAssignedUser().removeUserTask(userTask);
        this.userTasks.remove(userTask);
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
