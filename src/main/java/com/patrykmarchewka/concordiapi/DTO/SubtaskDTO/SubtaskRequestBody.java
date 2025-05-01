package com.patrykmarchewka.concordiapi.DTO.SubtaskDTO;

import com.patrykmarchewka.concordiapi.DTO.OnCreate;
import com.patrykmarchewka.concordiapi.DTO.TaskDTO.TaskMemberDTO;
import com.patrykmarchewka.concordiapi.PublicVariables;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

@JsonIgnoreProperties()
public class SubtaskRequestBody {
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String description;
    @NotNull
    private TaskMemberDTO taskMemberDTO;
    private PublicVariables.TaskStatus taskStatus;

    public SubtaskRequestBody(@Nullable String name,@Nullable String description, TaskMemberDTO taskMemberDTO,@Nullable PublicVariables.TaskStatus taskStatus){
        this.name = name;
        this.description = description;
        this.taskMemberDTO = taskMemberDTO;
        this.taskStatus = taskStatus;
    }

    public SubtaskRequestBody(){}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public TaskMemberDTO getTask(){return taskMemberDTO;}
    public void setTask(TaskMemberDTO taskMemberDTO){this.taskMemberDTO = taskMemberDTO;}

    public PublicVariables.TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(PublicVariables.TaskStatus taskStatus){this.taskStatus = taskStatus;}
}
