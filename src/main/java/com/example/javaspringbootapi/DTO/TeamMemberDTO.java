package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;

import java.util.HashSet;
import java.util.Set;

public class TeamMemberDTO {
    private long id;
    private String name;
    private int teammateCount;
    private Set<TaskMemberDTO> tasks = new HashSet<>();

    public TeamMemberDTO(Team team, User user){
        this.id = team.getId();
        this.name = team.getName();
        this.teammateCount = team.getTeammates().size();
        Set<TaskMemberDTO> filteredTasks = new HashSet<>();
        for (Task task : team.getTasks()){
            if (task.getUsers().contains(user)){
                filteredTasks.add(new TaskMemberDTO(task));
            }
        }
        this.tasks = filteredTasks;
    }

    public TeamMemberDTO(){}

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public int getTeammateCount(){return teammateCount;}
    public void setTeammateCount(int teammateCount){this.teammateCount = teammateCount;}

    public Set<TaskMemberDTO> getTasks(){return tasks;}
    public void setTasks(Set<TaskMemberDTO> tasks){this.tasks = tasks;}
}
