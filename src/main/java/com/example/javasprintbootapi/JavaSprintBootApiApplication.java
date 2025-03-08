package com.example.javasprintbootapi;

import com.example.javasprintbootapi.DatabaseModel.User;
import com.example.javasprintbootapi.DatabaseModel.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class JavaSprintBootApiApplication {

	public static UserService userService = new UserService();


	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {


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

		CheckDatabaseConnection();

		if (!userService.checkIfAdminExists()){
			userService.createUser("admin","admin","admin","admin", PublicVariables.UserStatus.ADMIN);
			System.out.println("Created admin account with these credentials:");
			System.out.println("Login: admin");
			System.out.println("Password: admin");
			System.out.println("Status:" + PublicVariables.UserStatus.ADMIN.toString());
			System.out.println("It's advised to change these values as soon as possible!");
		}


		String[] userCredentials = LoggingIn();



		if (JSONWebToken.VerifyJWT(JSONWebToken.GenerateJWToken(userCredentials[0],userCredentials[1],userCredentials[2]))){
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
		System.out.println("Checking DB connection...");
		try{
			SpringApplication.run(JavaSprintBootApiApplication.class);
			System.out.println("Connected!");
		} catch (Exception e) {
			System.out.println("Cant connect to database");
			System.out.println("Verify the connection properties. Make sure that an instance of SQL Server is running on the host and accepting TCP/IP connections at the port. Make sure that TCP connections to the port are not blocked by a firewall");
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
