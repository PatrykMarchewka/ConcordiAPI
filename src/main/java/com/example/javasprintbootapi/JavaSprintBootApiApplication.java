package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.UserRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class JavaSprintBootApiApplication {



	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

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
		}
		else{
			keyFile.createNewFile();
			FileWriter writer = new FileWriter(keyFile);

			writer.write(JSONWebToken.SecureKeyGenerator()); //SECRET KEY
			writer.close();
		}
		System.out.println("Welcome to Java Sprint Boot API");
		System.out.println("Type quit at any time to close application");



		//Logowanie
		//
		String[] userCredentials = LoggingIn();



		//TODO: Query the database
		if (JSONWebToken.VerifyJWT(JSONWebToken.GenerateJWToken(userCredentials[0],userCredentials[1],userCredentials[2])) && true){
			System.out.println("Identity validated");
			JSONWebToken.setJWT(JSONWebToken.GenerateJWToken(userCredentials[0],userCredentials[1],userCredentials[2]));
		}
		else{
			System.out.println("Cant validate identity");
			System.exit(0);
		}
		MenuOptions.Menu();






	}




	private static void CheckDatabaseConnection(){
		//TODO: Check DB connection
		System.out.println("Checking DB connection...");
		if (true){
			System.out.println("Connected!");
		}else{
			System.out.println("Cant connect to database");
			System.out.println("Press ENTER to quit application");
			System.console().readLine();
			System.exit(0);
		}
	}

	private static String[] LoggingIn(){
		System.out.println("You need to log in first");
		System.out.println("Please enter your login");
		String login,password,role = "";
		login = AskUser();
		System.out.println("Now enter password:");
		password = AskUser();
		System.out.println("Now type your role. 1 for Admin, 2 for employee, 3 for user access");
		char answerRole = AskUser().charAt(0);
		if (answerRole == '1'){
			PublicVariables.loggedUserRole = PublicVariables.UserStatus.ADMIN;
			System.out.println("Logging in as admin....");
		}
		else if(answerRole == '2'){
			PublicVariables.loggedUserRole = PublicVariables.UserStatus.EMPLOYEE;
			System.out.println("Logging in as employee.....");
		}
		else if(answerRole == '3'){
			PublicVariables.loggedUserRole = PublicVariables.UserStatus.USER;
			System.out.println("Logging in as user....");
		}
		else{
			System.out.println("Can't understand what you meant, resetting");
			System.out.println("");
			LoggingIn();
		}
		role = PublicVariables.loggedUserRole.name();
		return new String[]{login, password, role};
	}

	public static String AskUser(){
		String ans = System.console().readLine();
		if (ans.equalsIgnoreCase("quit")){
			System.exit(0);
		}
		return ans;
	}




}
