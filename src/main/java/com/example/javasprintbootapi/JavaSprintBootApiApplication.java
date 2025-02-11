package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.Task;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.SecretKey;

@SpringBootApplication
public class JavaSprintBootApiApplication {

	public static void main(String[] args) {

		SpringApplication.run(JavaSprintBootApiApplication.class, args);
		String login,password;
		System.out.println("Welcome to Java Sprint Boot API");
		System.out.println("Please enter your login and password");
		login = System.console().readLine();
		password = System.console().readLine();
		//Logowanie




	}

}
