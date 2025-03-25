package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.User;
import com.example.javasprintbootapi.DatabaseModel.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class JavaSprintBootApiApplication {

	@Autowired
	private UserService userService;


	public static void main(String[] args) {

		//TODO: Fix bug, console displays "Connected! Cant connect to database" sometimes when closing application
		System.out.println("Checking DB connection...");
		try{
			SpringApplication.run(JavaSprintBootApiApplication.class);
			System.out.println("Connected!");
		} catch (Exception e) {
			System.out.println("Cant connect to database");
			System.out.println("Verify the connection properties. Make sure that an instance of SQL Server is running on the host and accepting TCP/IP connections at the port. Make sure that TCP connections to the port are not blocked by a firewall");
			System.out.println(e.toString());
			System.out.println("Press ENTER to quit application");
			System.console().readLine();
			System.exit(0);
		}
	}


	@EventListener(ApplicationReadyEvent.class)
	private void ToRun(){
		System.out.println("Welcome to Java Sprint Boot API");
		System.out.println("Type quit at any time to close application");
		CheckUsers();
		LoggingIn(userService);
    }

	public static void LoggingIn(UserService userService){
		System.out.println("You need to log in first");
		System.out.println("Please enter your login");
		String[]userCredentials = new String[3];
		userCredentials[0] = AskUser();
		System.out.println("Now enter password:");
		userCredentials[1] = AskUser();
		System.out.println("Now type your role. 1 for Admin, 2 for employee, 3 for user access");
		char answerRole = AskUser().charAt(0);
		if (answerRole == '1'){
			PublicVariables.loggedUserRole = PublicVariables.UserRole.ADMIN;
			System.out.println("Logging in as admin....");
		}
		else if(answerRole == '2'){
			PublicVariables.loggedUserRole = PublicVariables.UserRole.EMPLOYEE;
			System.out.println("Logging in as employee.....");
		}
		else if(answerRole == '3'){
			PublicVariables.loggedUserRole = PublicVariables.UserRole.USER;
			System.out.println("Logging in as user....");
		}
		else{
			System.out.println("Can't understand what you meant, resetting");
			System.out.println();
			LoggingIn(userService);
		}
		userCredentials[2] = PublicVariables.loggedUserRole.name();
		try{
			if (JSONWebToken.VerifyJWT(JSONWebToken.GenerateJWToken(userCredentials[0],userCredentials[1],userCredentials[2]))&& userService.getUserByLogin(userCredentials[0]) != null && Passwords.CheckPasswordBCrypt(userCredentials[1],userService.getUserByLogin(userCredentials[0]).getPassword()) && userService.getUserByLogin(userCredentials[0]).getRole().equals(PublicVariables.UserRole.fromString(userCredentials[2]))){
				System.out.println("Identity validated");
			}
			else{
				System.out.println("Cant validate identity, closing the application");
				System.exit(0);
			}
			MenuOptions.Menu();
		}
		catch (Exception ex){
			System.out.println("Cant validate identity, is JWT set up properly?");
			System.exit(0);
		}
	}

	public static String AskUser(){
		String ans = System.console().readLine();
		if (ans.equalsIgnoreCase("quit")){
			System.exit(0);
		}
		return ans;
	}

	private void CheckUsers(){
		if (!userService.checkIfUserExistsByLogin("admin")){
			userService.createUser("admin","admin","admin","admin", PublicVariables.UserRole.ADMIN);
			System.out.println("Created admin account with these credentials:");
			System.out.println("Login: admin");
			System.out.println("Password: admin");
			System.out.println("Status:" + PublicVariables.UserRole.ADMIN.toString());
			System.out.println("It's advised to change these values as soon as possible!");
		}
		if (!userService.checkIfUserExistsByLogin("employee")){
			userService.createUser("employee","employee","employee","employee", PublicVariables.UserRole.EMPLOYEE);
			System.out.println("Created employee account with these credentials:");
			System.out.println("Login: employee");
			System.out.println("Password: employee");
			System.out.println("Status:" + PublicVariables.UserRole.EMPLOYEE.toString());
			System.out.println("It's advised to change these values as soon as possible!");
		}
		if (!userService.checkIfUserExistsByLogin("user")){
			userService.createUser("user","user","user,","user", PublicVariables.UserRole.USER);
			System.out.println("Created user account with these credentials:");
			System.out.println("Login: user");
			System.out.println("Password: user");
			System.out.println("Status:" + PublicVariables.UserRole.USER.toString());
			System.out.println("It's advised to change these values as soon as possible!");
		}

	}




}
