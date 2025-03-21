package com.example.javasprintbootapi;


public class MenuOptions {
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
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        String answer = JavaSprintBootApiApplication.AskUser();
        if (answer.contains("1")){
            MenuUsers();
        }
        else if(answer.contains("2")){
            MenuTasks();
        }
        else if(answer.contains("3")){
            MenuSubstasks();
        }
        else{
            System.out.println("Can't understand what you meant, resetting");
            System.out.println("");
            Menu();
        }
    }

    public static void MenuUsers(){
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        System.out.println("Type the number to get into specific option:");
        System.out.println("1. View users");
        System.out.println("2. Add user");
        System.out.println("3. Remove user");
        System.out.println("4. Elevate user to administrator");
        System.out.println("5. Set user as employee");
        System.out.println("6. Demote user to regular user");
        System.out.println("7. Ban user");
        System.out.println("0. Go back");
        System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\");
        String answer = JavaSprintBootApiApplication.AskUser();
        if (answer.contains("1")){
            if (PublicVariables.loggedUserRole == PublicVariables.UserRole.ADMIN){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.EMPLOYEE){

            }
            else if(PublicVariables.loggedUserRole == PublicVariables.UserRole.USER){

            }

        } else if(answer.contains("2")){


        } else if(answer.contains("3")){

        } else if (answer.contains("4")) {

        } else if (answer.contains("5")) {

        } else if (answer.contains("6")) {

        } else if (answer.contains("7")) {

        } else if (answer.contains("0")) {

        } else {

        }

    }
}
