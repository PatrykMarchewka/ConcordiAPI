package com.example.javasprintbootapi.DTO;

import com.example.javasprintbootapi.DatabaseModel.Task;
import com.example.javasprintbootapi.DatabaseModel.Team;
import com.example.javasprintbootapi.DatabaseModel.User;

import java.util.HashSet;
import java.util.Set;

public class TeamMemberDTO {
    private String name;
    private int teammateCount;
    private Set<TaskMemberDTO> tasks;

    public TeamMemberDTO(Team team, User user){
        this.name = team.getName();
        this.teammateCount = team.getTeammates().size();
        Set<Task> filteredTasks = new HashSet<>();
        for (Task task : team.getTasks()){
            if (task.getUsers().contains(user)){
                filteredTasks.add(task);
            }
        }
    }
}
