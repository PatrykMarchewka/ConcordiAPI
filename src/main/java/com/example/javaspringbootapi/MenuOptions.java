package com.example.javaspringbootapi;


import com.example.javaspringbootapi.DTO.InvitationDTO.InvitationManagerDTO;
import com.example.javaspringbootapi.DTO.SubtaskDTO.SubtaskMemberDTO;
import com.example.javaspringbootapi.DTO.TaskDTO.TaskManagerDTO;
import com.example.javaspringbootapi.DTO.TaskDTO.TaskMemberDTO;
import com.example.javaspringbootapi.DTO.UserDTO.UserMemberDTO;
import com.example.javaspringbootapi.DatabaseModel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class MenuOptions {

    @Autowired
    private TeamService teamService;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SubtaskService subtaskService;
    @Autowired
    private TeamUserRoleService teamUserRoleService;
    private static Team loggedUserTeam;
    private static User loggedUser;
    @Autowired
    private InvitationService invitationService;


    public void Start() {
        System.out.println("Choose whether to login or create a new account");
        System.out.println("1. Log in");
        System.out.println("2. Create new account");
        try {
            String ans = AskUser();
            if (ans.contains("1")) {
                LoggingIn();
            } else if (ans.contains("2")) {
                CreatingUser();
            } else {
                System.out.println(CouldntUnderstand());
                System.out.println();
                Start();
            }
        } catch (Exception e) {
            System.out.println("Found problem:");
            System.out.println(e.toString());
            Start();
        }

    }

    private void CreatingUser() {
        System.out.println("Creating new account");
        System.out.println("Please enter your login");
        String[] userCredentials = new String[4];
        userCredentials[0] = AskUser();
        System.out.println("Now enter password:");
        userCredentials[1] = AskUser();
        if (userService.checkIfUserExistsByLogin(userCredentials[0])) {
            System.out.println("Sorry login is already taken");
            CreatingUser();
        } else {
            System.out.println("Type your name");
            userCredentials[2] = AskUser();
            System.out.println("Type your lastname");
            userCredentials[3] = AskUser();
            userService.createUser(userCredentials[0], userCredentials[1], userCredentials[2], userCredentials[3]);
            System.out.println("User created!");
            System.out.println("Now try to log in!");
            LoggingIn();
        }

    }

    private void LoggingIn() {
        System.out.println("Logging in");
        System.out.println("Please enter your login");
        String[] userCredentials = new String[2];
        userCredentials[0] = AskUser();
        System.out.println("Now enter password:");
        userCredentials[1] = AskUser();
        User user = userService.getUserByLoginAndPassword(userCredentials[0], userCredentials[1]);
        try {
            if (JSONWebToken.VerifyJWT(JSONWebToken.GenerateJWToken(userCredentials[0], userCredentials[1])) && userService.getUserByLogin(userCredentials[0]) != null && Passwords.CheckPasswordBCrypt(userCredentials[1], userService.getUserByLogin(userCredentials[0]).getPassword())) {
                System.out.println("Identity validated");
                loggedUser = userService.getUserByLogin(userCredentials[0]);
            } else {
                System.out.println("Cant validate identity, closing the application");
                System.exit(0);
            }

        } catch (Exception ex) {
            System.out.println("Cant validate identity, is JWT set up properly?");
            System.exit(0);
        }
        List<Team> list = new ArrayList<>();
        list.add(null);
        if (user != null) {
            System.out.println("0. CREATE NEW TEAM ");
            int i = 1;
            for (Team team : user.getTeams()) {
                System.out.println(i + ". " + team.getName() + " - " + teamUserRoleService.getRole(user, team).name());
                list.add(team);
                i++;
            }
            System.out.println(i+1 + ". JOIN TEAM USING INVITATION CODE" );
        }

        if (list.size() == 1){
            System.out.println("You have no teams, you need to create one now or join already existing one");
            System.out.println("Choose whether to create new team or join existing one");
            System.out.println("1. Create new team");
            System.out.println("2. Join existing one (requires valid invitation)");
            String ans = AskUser();
            if (ans.contains("1")){
                CreateTeam();
            }
            else if(ans.contains("2")){
                JoinTeam();
            }
            else{
                System.out.println(CouldntUnderstand());
                System.out.println();
                LoggingIn();
            }
        }
        else{
            try {
                System.out.println("Type option number to choose it");
                int choice = Integer.valueOf(AskUser());
                if (choice == 0){
                    CreateTeam();
                }
                else if(choice == list.size()+1){
                    JoinTeam();
                }
                else{
                    loggedUserTeam = (list.get(choice));
                }
            } catch (Exception ex) {
                System.out.println(CouldntUnderstand());
                System.out.println();
                LoggingIn();
            }
        }

        if (loggedUserTeam == null) {
            LoggingIn();
        } else {
            Menu();
        }

    }

    private String AskUser() {
        String ans = System.console().readLine();
        if (ans.equalsIgnoreCase("quit")) {
            System.exit(0);
        }
        if (ans.equalsIgnoreCase("start") || ans.equalsIgnoreCase("logout")) {
            Start();
        }
        return ans;
    }


    private void Menu() {
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Type the number to get into specific option:");
        System.out.println("0. Teams");
        System.out.println("1. Users");
        System.out.println("2. Tasks and subtasks");
        System.out.println("3. Logout");
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        String answer = AskUser();
        if (answer.contains("0")) {
            MenuTeams();
        } else if (answer.contains("1")) {
            MenuUsers();
        } else if (answer.contains("2")) {
            MenuTasks();
        } else if (answer.contains("3")) {
            LoggingIn();
        } else {
            System.out.println(CouldntUnderstand());
            System.out.println();
            Menu();
        }
    }

    private void MenuTeams() {
        System.out.println("1. Create team");
        System.out.println("2. Change team name");
        System.out.println("3. Leave team");
        System.out.println("4. Disband team");
        System.out.println("5. Manage invitations");
        System.out.println("0. Go back");
        String ans = AskUser();
        if (ans.contains("1")) {
            CreateTeam();
        } else if (ans.contains("2")) {
            ChangeTeamName();

        } else if (ans.contains("3")) {
            LeaveTeam();
        } else if (ans.contains("4")) {
            DisbandTeam();
        } else if (ans.contains("5")) {
            MenuInvitations();
        }
        else if(ans.contains("0")){
            Menu();
        }
        else{
            System.out.println(CouldntUnderstand());
            System.out.println();
            MenuTeams();
        }

        Menu();
    }

    private void MenuUsers() {
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Type the number to get into specific option:");
        System.out.println("1. View users");
        System.out.println("2. Remove user from team");
        System.out.println("3. Elevate user to administrator");
        System.out.println("4. Set user as manager");
        System.out.println("5. Demote user to member");
        System.out.println("6. Ban user");
        System.out.println("0. Go back");
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        String answer = AskUser();
        if (answer.contains("1")) {
            ViewUsers();
        } else if (answer.contains("2")) {
            RemoveUserFromTeam();
        } else if (answer.contains("3")) {
            SetAsAdmin();
        } else if (answer.contains("4")) {
            SetAsManager();
        } else if (answer.contains("5")) {
            SetAsUser();
        } else if (answer.contains("6")) {
            BanUser();
        } else if (answer.contains("0")) {
            Menu();

        } else {
            System.out.println(CouldntUnderstand());
            System.out.println();
            MenuUsers();
        }

    }

    private void MenuTasks() {
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Type the number to get into specific option:");
        System.out.println("1. View tasks");
        System.out.println("2. Create new task");
        System.out.println("3. Edit task");
        System.out.println("4. Delete task");
        System.out.println("0. Go back");
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        String answer = AskUser();
        if (answer.contains("1")){
            ViewTasks();
        }
        else if(answer.contains("2")){
            CreateTask();
        }
        else if(answer.contains("3")){
            EditTask(null);
        }
        else if(answer.contains("4")){
            DeleteTask();
        }
        else if (answer.contains("0")){
            Menu();
        }
        else{
            System.out.println(CouldntUnderstand());
            System.out.println();
            MenuTasks();
        }
        MenuTasks();

    }
    
    public static String NoPermissionsMessage(){
        return "You do not have permissions to do this action";
    }

    public static String CouldntCompleteOperation(){
        return "Couldnt complete operation, check for errors and try again";
    }

    public static String CouldntUnderstand(){
        return "Couldnt understand what you meant, resetting!";
    }




    //TEAMS
    //TODO: Add JavaDocs(///)
    private void CreateTeam(){
        System.out.println("Give a name to your new team");
        String answer = AskUser();
        loggedUserTeam = teamService.createTeam(answer, loggedUser);
    }

    private void JoinTeam(){
        System.out.println("Type your invitation code");
        String answer = AskUser();
        try {
            invitationService.useInvitation(invitationService.getInvitationByUUID(answer),loggedUser);
            System.out.println("Successfully joined team");
        } catch (Exception e) {
            System.out.println(CouldntCompleteOperation());
        }
    }

    private void ChangeTeamName(){
        System.out.println("Type new name of your team:");
        String answer = AskUser();
        loggedUserTeam.setName(answer);
        teamService.saveTeam(loggedUserTeam);
    }

    private void LeaveTeam(){
        System.out.println("Are you sure you want to leave team " + loggedUserTeam.getName() + "?");
        System.out.println("Type YES to leave");
        String answer = AskUser();
        if (answer.contains("YES")) {
            teamService.removeUser(loggedUserTeam, loggedUser);
            System.out.println("Successfully left the team!");
            loggedUserTeam = null;
            LoggingIn();
        } else {
            System.out.println("Going back");
            MenuTeams();
        }
    }

    private void DisbandTeam(){
        if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.ADMIN)) {
            System.out.println("Are you sure you want to permanently disband team " + loggedUserTeam.getName() + "?");
            System.out.println("Type YES to disband");
            String answer = AskUser();
            if (answer.contains("YES")) {
                for (User user : loggedUserTeam.getTeammates()){
                    teamService.removeUser(loggedUserTeam,user);
                }
                teamService.deleteTeam(loggedUserTeam);
                loggedUserTeam = null;
                LoggingIn();
            } else {
                System.out.println("Going back");
                MenuTeams();
            }
        } else {
            System.out.println("You can't disband that team!");
        }
    }




    //Users
    private void ViewUsers(){
        if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.ADMIN) || teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.MANAGER)) {
            Set<UserMemberDTO> users = new HashSet<>();
            for (User user : loggedUserTeam.getTeammates()) {
                users.add(new UserMemberDTO(user));
            }

            for (UserMemberDTO user : users) {
                System.out.println(user);
            }
        } else if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.MEMBER)) {
            System.out.println("Member count: " + loggedUserTeam.getTeammates().size());
        }
        MenuUsers();
    }

    private void RemoveUserFromTeam(){
        if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.ADMIN) || teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.MANAGER)) {
            System.out.println("Type ID of the user you want to delete");
            try {
                User user = userService.getUserByID(Long.valueOf(AskUser()));
                if (user != null){
                    PublicVariables.UserRole myRole = teamUserRoleService.getRole(loggedUser, loggedUserTeam);
                    PublicVariables.UserRole role = teamUserRoleService.getRole(user, loggedUserTeam);
                    if (role.compareTo(myRole) > 0) {
                        for (Task task : loggedUserTeam.getTasks()) {
                            if (task.getUsers().contains(user)) {
                                task.getUsers().remove(user);
                                taskService.saveTask(task);
                            }
                        }
                        loggedUserTeam.getTeammates().remove(user);
                        teamService.saveTeam(loggedUserTeam);
                        teamUserRoleService.deleteTMR(teamUserRoleService.getByUserAndTeam(user, loggedUserTeam));
                        System.out.println("User deleted from the team");
                    } else {
                        System.out.println(NoPermissionsMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println(CouldntCompleteOperation());
            }
        } else if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.MEMBER)) {
            System.out.println(NoPermissionsMessage());
        }
        MenuUsers();
    }

    private void SetAsAdmin(){
        if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.ADMIN)) {
            System.out.println("Type ID of the user you want to promote to admin role");
            try {
                User user = userService.getUserByID(Long.valueOf(AskUser()));
                if (user != null){
                    teamUserRoleService.setRole(user,loggedUserTeam, PublicVariables.UserRole.ADMIN);
                    System.out.println("User " + user.getName() + " is now ADMIN on your team!");
                }
            } catch (Exception e) {
                System.out.println(CouldntCompleteOperation());
            }
        } else {
            System.out.println(NoPermissionsMessage());
        }
        MenuUsers();
    }

    private void SetAsManager(){
        if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.ADMIN)) {
            System.out.println("Type ID of the user you want to set as manager role");
            try {
                User user = userService.getUserByID(Long.valueOf(AskUser()));
                if (user!= null){
                    teamUserRoleService.setRole(user,loggedUserTeam, PublicVariables.UserRole.MANAGER);
                    System.out.println("User " + user.getName() + " is now MANAGER on your team!");
                }
            } catch (Exception e) {
                System.out.println(CouldntCompleteOperation());
            }
        } else {
            System.out.println(NoPermissionsMessage());
        }
        MenuUsers();
    }

    private void SetAsUser(){
        if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.ADMIN)) {
            System.out.println("Type ID of the user you want to set as member role");
            try {
                User user = userService.getUserByID(Long.valueOf(AskUser()));
                if (user!= null){
                    teamUserRoleService.setRole(user,loggedUserTeam, PublicVariables.UserRole.MEMBER);
                    System.out.println("User " + user.getName() + " is now MEMBER on your team!");
                }
            } catch (Exception e) {
                System.out.println(CouldntCompleteOperation());
            }
        }
        else{
            System.out.println(NoPermissionsMessage());
        }
        MenuUsers();
    }

    private void BanUser(){
        if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.ADMIN)) {
            System.out.println("Type ID user you want to ban on your team");
            try{
                User user = userService.getUserByID(Long.valueOf(AskUser()));
                if (user != null){
                    PublicVariables.UserRole myRole = teamUserRoleService.getRole(loggedUser,loggedUserTeam);
                    PublicVariables.UserRole role = teamUserRoleService.getRole(user,loggedUserTeam);
                    if (role.compareTo(myRole) > 0){
                        teamUserRoleService.setRole(user,loggedUserTeam, PublicVariables.UserRole.BANNED);
                    }
                }
            } catch (Exception e) {
                System.out.println(CouldntCompleteOperation());
            }

        }
        else{
            System.out.println(NoPermissionsMessage());
        }
        MenuUsers();
    }

    //TASKS
    private void ViewTasks(){
        if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.ADMIN) || teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.MANAGER)) {
            Set<TaskManagerDTO> tasks = new HashSet<>();
            for (Task task : loggedUserTeam.getTasks()) {
                tasks.add(new TaskManagerDTO(task));
            }

            for (TaskManagerDTO task : tasks) {
                System.out.println(task);
            }
        } else if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.MEMBER)) {
            Set<TaskMemberDTO> tasks = new HashSet<>();
            for (Task task : loggedUser.getTasks()){
                tasks.add(new TaskMemberDTO(task));
            }

            for (TaskMemberDTO task : tasks){
                System.out.println(task);
            }
        }
    }

    private void CreateTask(){

        try {
            Task task = new Task();
            System.out.println("Give name for the new task");
            task.setName(AskUser());
            System.out.println("Give description for your task");
            task.setDescription(AskUser());
            task.setCreationDate(OffsetDateTime.now());
            task.setTaskStatus(PublicVariables.TaskStatus.NEW);
            task.getUsers().add(loggedUser);
            taskService.saveTask(task);

            loggedUser.getTasks().add(task);
            userService.saveUser(loggedUser);

            System.out.println("Task successfully created");
            System.out.println("Use edit task function to add users, subtasks and edit more information");
        } catch (Exception e) {
            System.out.println(CouldntCompleteOperation());
            System.out.println();
        }
    }

    private void EditTask(Task task){
        try {
            if (task == null){
                System.out.println("Type ID of the task you want to edit");
                long id = Long.valueOf(AskUser());
                task = taskService.getTaskByID(id,loggedUserTeam);
            }
            if (teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.ADMIN) || teamUserRoleService.getRole(loggedUser, loggedUserTeam).equals(PublicVariables.UserRole.MANAGER) || task.getUsers().contains(loggedUser)){
                System.out.println("Choose what you want to edit");
                System.out.println("1. Task Name");
                System.out.println("2. Task Description");
                System.out.println("3. Task Status");
                System.out.println("4. Users assigned to task");
                System.out.println("5. Subtasks assigned to task");
                System.out.println("0. DONE EDITING");
                String ans = AskUser();
                if (ans.contains("1")){
                    System.out.println("Type new name for the task");
                    String name = AskUser();
                    task.setName(name);
                    taskService.saveTask(task);
                    EditTask(task);
                }
                else if(ans.contains("2")){
                    System.out.println("Type new description for the task");
                    String desc = AskUser();
                    task.setDescription(desc);
                    EditTask(task);
                }
                else if(ans.contains("3")){
                    System.out.println("Type number of new task status for the task");
                    for (PublicVariables.TaskStatus ts : PublicVariables.TaskStatus.values()){
                        System.out.println(ts.ordinal() + " " + ts.name());
                    }
                    int choice = Integer.valueOf(AskUser());
                    task.setTaskStatus(PublicVariables.TaskStatus.values()[choice]);
                    taskService.saveTask(task);
                    EditTask(task);
                }
                else if(ans.contains("4")){
                    System.out.println("Do you want to add new user or remove existing one?");
                    System.out.println("1. Add users");
                    System.out.println("2. Remove users");
                    String answer = AskUser();
                    if (answer.contains("1")){
                        System.out.println("Type the ID of user you want to add to this task");
                        String userid = AskUser();
                        try{
                            long userID = Long.valueOf(userid);
                            if (teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.ADMIN) || teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.MANAGER) || loggedUser.getTasks().contains(task)){
                                User potentialNewUser = userService.getUserByID(userID);
                                if (loggedUserTeam.getTeammates().contains(potentialNewUser)){
                                    PublicVariables.UserRole myRole = teamUserRoleService.getRole(loggedUser,loggedUserTeam);
                                    PublicVariables.UserRole role = teamUserRoleService.getRole(potentialNewUser,loggedUserTeam);
                                    if (role.compareTo(myRole) >= 0){
                                        taskService.addUserToTask(loggedUserTeam, task.getID(), potentialNewUser);
                                        System.out.println("User added to task");
                                    }
                                    else{
                                        System.out.println(NoPermissionsMessage());
                                        System.out.println();
                                        MenuTasks();
                                    }
                                }
                                else{
                                    System.out.println(NoPermissionsMessage());
                                    System.out.println();
                                    MenuTasks();
                                }

                            }
                            else{
                                throw new IllegalArgumentException();
                            }
                        } catch (Exception e) {
                            System.out.println(CouldntCompleteOperation());
                            System.out.println();
                        }
                    }
                    else if(answer.contains("2")){
                        System.out.println("Type the ID of user you want to remove from this task");
                        String userid = AskUser();
                        try{
                            long userID = Long.valueOf(userid);
                            User todelete = userService.getUserByID(userID);
                            if (task.getUsers().contains(todelete)){
                                PublicVariables.UserRole myRole = teamUserRoleService.getRole(loggedUser,loggedUserTeam);
                                PublicVariables.UserRole role = teamUserRoleService.getRole(todelete,loggedUserTeam);
                                if (role.compareTo(myRole) >= 0){
                                    taskService.removeUserFromTask(loggedUserTeam,task.getID(),todelete);
                                    System.out.println("User removed from task");
                                }
                                else{
                                    System.out.println(NoPermissionsMessage());
                                    System.out.println();
                                }
                            }
                            else{
                                System.out.println("User is not assigned to this task");
                                System.out.println();
                            }


                        } catch (Exception e) {
                            System.out.println(CouldntCompleteOperation());
                            System.out.println();
                        }
                    }
                    else{
                        System.out.println(CouldntUnderstand());
                        System.out.println();
                    }
                    EditTask(task);

                }
                else if(ans.contains("5")){
                    //Subtasks
                    System.out.println("Do you want to add or remove subtasks?");
                    System.out.println("1. Add new subtask");
                    System.out.println("2. Remove subtask");
                    String answer = AskUser();
                    if (answer.contains("1")){
                        CreateSubtask(task);
                    }
                    else if (answer.contains("2")){
                        DeleteSubtask(task);
                    }
                    else{
                        System.out.println(CouldntUnderstand());
                        System.out.println();
                    }
                    EditTask(task);
                }
                else if(ans.contains("0")){
                    MenuTasks();
                }
                else{
                    System.out.println(CouldntUnderstand());
                    System.out.println();
                }
            }
            else{
                System.out.println(NoPermissionsMessage());
                System.out.println();
            }

        } catch (Exception e) {
            System.out.println(CouldntCompleteOperation());
            System.out.println();
        }


    }

    private void DeleteTask(){
        if (teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.ADMIN)){
            System.out.println("Type the ID of the task you want to delete");
            try{
                long taskID = Long.valueOf(AskUser());
                taskService.deleteTaskByID(taskID,loggedUserTeam);
                System.out.println("Task deleted!");
                System.out.println();
                MenuTasks();
            } catch (Exception e) {
                System.out.println(CouldntCompleteOperation());
                System.out.println();
            }
        }
        else{
            System.out.println(NoPermissionsMessage());
            System.out.println();
        }
    }









    //Subtasks
    private void CreateSubtask(Task task){
        try {
            Subtask subtask = new Subtask();
            subtask.setTask(task);
            subtask.setTaskStatus(PublicVariables.TaskStatus.NEW);
            System.out.println("Type name for the subtask");
            subtask.setName(AskUser());
            System.out.println("Type description for the subtask");
            subtask.setDescription(AskUser());
            subtaskService.saveSubtask(subtask);
            task.getSubtasks().add(subtask);
            taskService.saveTask(task);
        } catch (Exception e) {
            System.out.println(CouldntCompleteOperation());
            System.out.println();
        }
    }

    private void DeleteSubtask(Task task){
        if (teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.ADMIN)){
            System.out.println("Type ID of the subtask you want to delete");
            for (Subtask sub : task.getSubtasks()){
                System.out.println(new SubtaskMemberDTO(sub).toString());
            }
            try{
                long choice = Long.valueOf(AskUser());
                subtaskService.deleteSubtask(task.getID(),choice);
                System.out.println("Subtask deleted");
            } catch (Exception e) {
                System.out.println(CouldntUnderstand());
            }
        }
        else{
            System.out.println(NoPermissionsMessage());
        }
    }

    //Invitations
    private void MenuInvitations(){
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Type the number to get into specific option:");
        System.out.println("1. See all invitations");
        System.out.println("2. Create new invitation");
        System.out.println("3. Edit existing invitation");
        System.out.println("4. Delete invitation");
        System.out.println("0. Go back");
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        String answer = AskUser();
        if (answer.contains("1")){
            GetInvitations();
        }
        else if(answer.contains("2")){
            CreateInvitation();
        }
        else if(answer.contains("3")){
            EditInvitation(null);
        }
        else if(answer.contains("4")){
            DeleteInvitation();
        }
        else if(answer.contains("0")){
            Menu();
        }
        else{
            System.out.println(CouldntUnderstand());
            System.out.println();
            MenuInvitations();
        }
        MenuInvitations();
    }

    private void GetInvitations(){
        if (teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.ADMIN) || teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.MANAGER)){
            System.out.println("All invitations for the team");
            for (Invitation invitation : invitationService.getAllInvitations(loggedUserTeam)){
                System.out.println(new InvitationManagerDTO(invitation));
            }
        }
        else{
            System.out.println(NoPermissionsMessage());
        }
    }

    private void CreateInvitation(){
        if (teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.ADMIN) || teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.MANAGER)){
            try {
                Invitation invitation = new Invitation();
                invitation.setTeam(loggedUserTeam);
                System.out.println("Type the amount of uses you want to assign to the invitation with the minimum being one or type MAX if you want it to be maximum value (32737)");
                String usesString = AskUser();
                if (usesString.toUpperCase().contains("MAX")){
                    invitation.setUses(Short.MAX_VALUE);
                }
                else{
                    BigInteger uses = new BigInteger(usesString);
                    if (uses.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0){
                        invitation.setUses(Short.MAX_VALUE);
                    }
                    else if(uses.compareTo(BigInteger.ONE) < 0){
                        invitation.setUses((short) 1);
                    }
                    else{
                        invitation.setUses(uses.shortValue());
                    }
                }
                System.out.println("Type the role attached to the invitation (ADMIN, MANAGER, MEMBER)");
                invitation.setRole(PublicVariables.UserRole.fromString(AskUser()));
                System.out.println("Type the date when the invitation will expire in ISO8601 format (example: 2025-04-15T15:30:00+02:00 for 15th april 2025 15:30+02:00 timezone) or type NULL if you don't want it to expire");
                String dateString = AskUser();
                if (!dateString.toUpperCase().contains("NULL")){
                    invitation.setDueTime(OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                }
                invitationService.saveInvitation(invitation);
                System.out.println("Invitation successfully saved!");

            } catch (Exception e) {
                System.out.println(CouldntUnderstand());
                MenuInvitations();
            }

        }
        else{
            System.out.println(NoPermissionsMessage());
        }
    }

    private void EditInvitation(Invitation invitation){
        if (teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.ADMIN) || teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.MANAGER)){
            try{
                if (invitation == null){
                    System.out.println("Type the UUID of the invitation you want to edit");
                    String uuid = AskUser();
                    if (invitationService.getInvitationByUUID(uuid) != null) {
                        invitation = invitationService.getInvitationByUUID(uuid);
                    }
                    else{
                            System.out.println("Couldnt find the invitation with provided UUID");
                    }
                }
                    System.out.println("Choose what you want to edit");
                    System.out.println("1. Invitation uses");
                    System.out.println("2. User role");
                    System.out.println("3. Expiration date");
                    System.out.println("0. DONE EDITING");
                    String ans = AskUser();
                    if (ans.contains("1")){
                        System.out.println(String.format("Invitation currently has %d uses",invitation.getUses()));
                        System.out.println("Type the amount of uses you want to assign to the invitation with the minimum being one or type MAX if you want it to be maximum value (32737)");
                        String usesString = AskUser();
                        if (usesString.toUpperCase().contains("MAX")){
                            invitation.setUses(Short.MAX_VALUE);
                        }
                        else{
                            BigInteger uses = new BigInteger(usesString);
                            if (uses.compareTo(BigInteger.valueOf(Short.MAX_VALUE)) > 0){
                                invitation.setUses(Short.MAX_VALUE);
                            }
                            else if(uses.compareTo(BigInteger.ONE) < 0){
                                invitation.setUses((short) 1);
                            }
                            else{
                                invitation.setUses(uses.shortValue());
                            }
                        }
                        EditInvitation(invitation);
                    }
                    else if(ans.contains("2")){
                        System.out.println(String.format("Invitation currently has %s role assigned",invitation.getRole().name()));
                        System.out.println("Type the role attached to the invitation (ADMIN, MANAGER, MEMBER)");
                        invitation.setRole(PublicVariables.UserRole.fromString(AskUser()));
                        EditInvitation(invitation);
                    }
                    else if(ans.contains("3")){
                        if (invitation.getDueTime() == null) {
                            System.out.println("Invitation currently has no expiry date set");
                        } else {
                            System.out.println(String.format("Invitation is currently set to expire at %s",invitation.getDueTime()));
                        }
                        System.out.println("Type the date when the invitation will expire in ISO8601 format (example: 2025-04-15T15:30:00+02:00 for 15th april 2025 15:30+02:00 timezone) or type NULL if you don't want it to expire");
                        String dateString = AskUser();
                        if (!dateString.toUpperCase().contains("NULL")){
                            invitation.setDueTime(OffsetDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                        }
                        EditInvitation(invitation);
                    }
                    else if(ans.contains("0")){
                        MenuInvitations();
                    }

            } catch (Exception e) {
                System.out.println(CouldntUnderstand());
                MenuInvitations();
            }
        }
        else{
            System.out.println(NoPermissionsMessage());
        }
    }

    private void DeleteInvitation(){
        if (teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.ADMIN) || teamUserRoleService.getRole(loggedUser,loggedUserTeam).equals(PublicVariables.UserRole.MANAGER)){
            try {
                System.out.println("Type the UUID of the invitation you want to delete");
                String uuid = AskUser();
                if (invitationService.getInvitationByUUID(uuid) != null) {
                   invitationService.deleteInvitation(invitationService.getInvitationByUUID(uuid));
                }
                else{
                    System.out.println("Couldnt find the invitation with provided UUID");
                }
            } catch (Exception e) {
                System.out.println(CouldntUnderstand());
                MenuInvitations();
            }
        }
        else{
            System.out.println(NoPermissionsMessage());
        }
    }

}