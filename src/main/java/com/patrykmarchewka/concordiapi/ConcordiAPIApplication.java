package com.patrykmarchewka.concordiapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ConcordiAPIApplication {

	public static void main(String[] args) {
		System.out.println("Checking DB connection...");
	 	SpringApplication.run(ConcordiAPIApplication.class);
	}


	@EventListener(ApplicationReadyEvent.class)
	private void ToRun(){
		System.out.println("Welcome to ConcordiAPI");
    }

}
