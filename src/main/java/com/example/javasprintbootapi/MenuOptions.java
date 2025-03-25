package com.example.javasprintbootapi;


import com.example.javasprintbootapi.DatabaseModel.*;

import static com.example.javasprintbootapi.JavaSprintBootApiApplication.AskUser;

public class MenuOptions {

    private static UserRepository userRepository;
    private static TaskRepository taskRepository;
    private static SubtaskRepository subtaskRepository;

    private static UserService userService;
    private static TaskService taskService;
    private static SubtaskService subtaskService;


    public static void MenuTasks(){
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Type the number to get into specific option:");
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");

    }
    public static void MenuSubstasks(){
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Type the number to get into specific option:");
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");

    }

    public static void Menu(){
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Type the number to get into specific option:");
        System.out.println("1. Users");
        System.out.println("2. Tasks");
        System.out.println("3. Subtasks");
        System.out.println("4. Logout");
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        String answer = AskUser();
        if (answer.contains("1")){
            MenuUsers();
        }
        else if(answer.contains("2")){
            MenuTasks();
        }
        else if(answer.contains("3")){
            MenuSubstasks();
        }
        else if(answer.contains("4")){
            JavaSprintBootApiApplication.LoggingIn(userService);
        }
        else{
            System.out.println("Can't understand what you meant, resetting");
            System.out.println();
            Menu();
        }
    }

    public static void MenuUsers(){
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Type the number to get into specific option:");
        System.out.println("1. View users");
        System.out.println("2. Add user");
        System.out.println("3. Delete user");
        System.out.println("4. Elevate user to administrator");
        System.out.println("5. Set user as employee");
        System.out.println("6. Demote user to regular user");
        System.out.println("7. Ban user");
        System.out.println("0. Go back");
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        String answer = AskUser();
        if (answer.contains("1")){
            if (PublicVariables.loggedUserRole == PublicVariables.UserRole.ADMIN){
                for (User user : userRepository.findAll()){
                    System.out.println(user.getID());
                    System.out.println(user.getName() + " " + user.getLastName());
                    System.out.println(user.getLogin() + " " + user.getRole().name());
                    System.out.println("Owner of tasks:");
                    for (Task task : user.getOwnership()){
                        System.out.println(String.format("Task ID: %d, %s, %s",task.getId(),task.getName(),task.getDescription()));
                    }
                    System.out.println();
                }
                MenuUsers();
            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.EMPLOYEE){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.USER){

            }

        } else if(answer.contains("2")){
            if (PublicVariables.loggedUserRole == PublicVariables.UserRole.ADMIN){
                String[] userInfo = new String[5];
                System.out.println("Enter the login for new user");
                userInfo[0] = AskUser();
                System.out.println("Now enter password:");
                userInfo[1] = AskUser();
                System.out.println("Now type the role. 1 for Admin, 2 for employee, 3 for user access");
                char answerRole = AskUser().charAt(0);
                if (answerRole == '1'){
                    userInfo[2] = PublicVariables.UserRole.ADMIN.name();
                    System.out.println("Chose admin");
                }
                else if(answerRole == '2'){
                    userInfo[2] = PublicVariables.UserRole.EMPLOYEE.name();
                    System.out.println("Chose employee");
                }
                else if(answerRole == '3'){
                    userInfo[2] = PublicVariables.UserRole.USER.name();
                    System.out.println("Chose user");
                }
                else{
                    System.out.println("Can't understand what you meant, resetting");
                    System.out.println();
                    MenuUsers();
                }
                System.out.println("Type their name");
                userInfo[3] = AskUser();
                System.out.println("Type their lastname");
                userInfo[4] = AskUser();
                userService.createUser(userInfo[0], userInfo[1], userInfo[3], userInfo[4], PublicVariables.UserRole.fromString(userInfo[2]));
                System.out.println("User created!");
                MenuUsers();
            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.EMPLOYEE){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.USER){

            }


        } else if(answer.contains("3")){
            if (PublicVariables.loggedUserRole == PublicVariables.UserRole.ADMIN){
                System.out.println("Type ID or Login of the user you want to delete");
                String res = AskUser();
                Long id = null;
                try{
                    id = Long.parseLong(res);
                }
                finally {
                    if (id == null && userRepository.existsByLogin(res)){
                        User user = userRepository.findByLogin(res);
                        userService.deleteUser(user);
                    }
                    else if(userRepository.findById(id).isPresent()){
                        userService.deleteUserByID(id);
                    }
                    else{
                        System.out.println("Can't parse the input, resetting");
                        System.out.println();
                    }

                }
                MenuUsers();
            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.EMPLOYEE){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.USER){

            }

        } else if (answer.contains("4")) {
            if (PublicVariables.loggedUserRole == PublicVariables.UserRole.ADMIN){
                System.out.println("Type ID or Login of the user you want to promote to admin role");
                String res = AskUser();
                Long id = null;
                try{
                    id = Long.parseLong(res);
                }
                finally {
                    if (id == null && userRepository.existsByLogin(res)){
                        User user = userRepository.findByLogin(res);
                        user.setRole(PublicVariables.UserRole.ADMIN);
                        userRepository.save(user);
                    }
                    else if(userRepository.findById(id).isPresent()){
                        User user = userService.getUserByID(id);
                        user.setRole(PublicVariables.UserRole.ADMIN);
                        userRepository.save(user);
                    }
                    else{
                        System.out.println("Can't parse the input, resetting");
                        System.out.println();
                    }
                }
                MenuUsers();
            }
            else {
                System.out.println("You dont have enough privileges to do that!");
            }

        } else if (answer.contains("5")) {
            if (PublicVariables.loggedUserRole == PublicVariables.UserRole.ADMIN){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.EMPLOYEE){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.USER){

            }

        } else if (answer.contains("6")) {
            if (PublicVariables.loggedUserRole == PublicVariables.UserRole.ADMIN){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.EMPLOYEE){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.USER){

            }

        } else if (answer.contains("7")) {
            if (PublicVariables.loggedUserRole == PublicVariables.UserRole.ADMIN){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.EMPLOYEE){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.USER){

            }

        } else if (answer.contains("0")) {
            Menu();

        } else {
            System.out.println("Can't understand what you meant, resetting");
            System.out.println();
            MenuUsers();
        }

    }
}
