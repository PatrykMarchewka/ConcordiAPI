package com.example.javaspringbootapi.DTO.TeamDTO;

import com.example.javaspringbootapi.DTO.TaskDTO.TaskManagerDTO;
import com.example.javaspringbootapi.DTO.UserDTO.UserMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.Task;
import com.example.javaspringbootapi.DatabaseModel.Team;
import com.example.javaspringbootapi.DatabaseModel.User;
import com.example.javaspringbootapi.PublicVariables;
import com.example.javaspringbootapi.TeamUserRoleService;

import java.util.HashSet;
import java.util.Set;

public class TeamAdminDTO {

    private long id;
    private String name;
    private Set<UserMemberDTO> teammates = new HashSet<>();
    private Set<TaskManagerDTO> tasks = new HashSet<>();
    private Set<UserMemberDTO> admins = new HashSet<>();
    private Set<UserMemberDTO> managers = new HashSet<>();
    private Set<UserMemberDTO> owners = new HashSet<>();

    public TeamAdminDTO(Team team, TeamUserRoleService service){
        this.id = team.getId();
        this.name = team.getName();
        for (User teammate : team.getTeammates()){
            teammates.add(new UserMemberDTO(teammate));
        }
        for (Task task : team.getTasks()){
            tasks.add(new TaskManagerDTO(task));
        }
        for (User admin : service.getAllRole(team, PublicVariables.UserRole.ADMIN)){
            admins.add(new UserMemberDTO(admin));
        }
        for (User manager : service.getAllRole(team, PublicVariables.UserRole.MANAGER)){
            managers.add(new UserMemberDTO(manager));
        }
        for (User user1 : service.getAllRole(team, PublicVariables.UserRole.OWNER)){
            this.owners.add(new UserMemberDTO(user1));
        }
    }

    public TeamAdminDTO(){};

    public long getId() {return id;}
    public void setId(long id){this.id = id;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public Set<UserMemberDTO> getTeammates(){return teammates;}
    public void setTeammates(Set<UserMemberDTO> teammates){this.teammates = teammates;}

    public Set<TaskManagerDTO> getTasks(){return tasks;}
    public void setTasks(Set<TaskManagerDTO> tasks){this.tasks = tasks;}

    public Set<UserMemberDTO> getAdmins(){return admins;}
    public void setAdmins(Set<UserMemberDTO> admins){this.admins = admins;}

    public Set<UserMemberDTO> getManagers(){return managers;}
    public void setManagers(Set<UserMemberDTO> managers){this.managers = managers;}

    public Set<UserMemberDTO> getOwners(){return owners;}
    public void setOwners(Set<UserMemberDTO> owners){this.owners = owners;}
}
