package com.example.javasprintbootapi;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@SpringBootApplication
public class JavaSprintBootApiApplication {

	public static void main(String[] args) throws IOException {

		//SpringApplication.run(JavaSprintBootApiApplication.class, args);
		File keyFile = new File(System.getProperty("user.dir") + "\\SECRET_KEY.txt");
		if (keyFile.exists() && keyFile.isFile()){
			FileReader reader = new FileReader(keyFile);
			String temp = "";
			int ch;
			while ((ch=reader.read()) != -1){
				temp += ((char) ch);
			}
			reader.close();
			JSONWebToken.setSecretKey(temp);
			System.out.println(JSONWebToken.getSecretKey());
		}
		else{
			keyFile.createNewFile();
			FileWriter writer = new FileWriter(keyFile);

			writer.write(JSONWebToken.SecureKeyGenerator()); //SECRET KEY
			writer.close();
		}
		String login,password;
		System.out.println("Welcome to Java Sprint Boot API");
		System.out.println("Please enter your login and password");
		//login = System.console().readLine();
		password = System.console().readLine();
		//Logowanie
		//
	}

}
