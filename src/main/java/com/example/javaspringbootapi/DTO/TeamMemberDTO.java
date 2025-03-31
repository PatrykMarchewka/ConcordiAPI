package com.example.javaspringbootapi.DTO;

import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;

import java.util.HashSet;
import java.util.Set;

public class TeamMemberDTO {
    private String name;
    private int teammateCount;
    private Set<TaskMemberDTO> tasks;

    public TeamMemberDTO(Team team, User user){
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
}
