package com.patrykmarchewka.concordiapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "ConcordiAPI",version = "v1.0.0",description = "REST API for managing tasks,users and teams"))
public class ConcordiAPIApplication {

	@Autowired
	private UserService userService;

	@Autowired
	private MenuOptions menuOptions;

	public static void main(String[] args) {
		System.out.println("Checking DB connection...");
	 	SpringApplication.run(ConcordiAPIApplication.class);
		System.out.println("Connected!");
	}


	@EventListener(ApplicationReadyEvent.class)
	private void ToRun(){
		System.out.println("Welcome to Java Sprint Boot API");
		System.out.println("Type QUIT at any time to close application");
		System.out.println("Type START or LOGOUT at any time to go back to beginning");
		CheckUsers();
		menuOptions.Start();
    }



	private void CheckUsers(){
		if (!userService.checkIfUserExistsByLogin("admin")){
			userService.createUser("admin","admin","admin","admin");
			System.out.println("Created admin account with these credentials:");
			System.out.println("Login: admin");
			System.out.println("Password: admin");
			System.out.println("It's advised to change these values as soon as possible!");
		}
		if (!userService.checkIfUserExistsByLogin("manager")){
			userService.createUser("manager","manager","manager","manager");
			System.out.println("Created manager account with these credentials:");
			System.out.println("Login: manager");
			System.out.println("Password: manager");
			System.out.println("It's advised to change these values as soon as possible!");
		}
		if (!userService.checkIfUserExistsByLogin("member")){
			userService.createUser("member","member","member,","member");
			System.out.println("Created member account with these credentials:");
			System.out.println("Login: member");
			System.out.println("Password: member");
			System.out.println("It's advised to change these values as soon as possible!");
		}
	}




}
