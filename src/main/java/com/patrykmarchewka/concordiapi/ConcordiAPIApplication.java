package com.patrykmarchewka.concordiapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.stream.Stream;

@SpringBootApplication
public class ConcordiAPIApplication {

	public static void main(String[] args) {
        try {
            loadEnvFile();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        System.out.println("Checking DB connection...");
	 	SpringApplication.run(ConcordiAPIApplication.class);
	}

	/**
	 * Loads ConcordiAPI.env file in jar directory and saves it's contents as System properties allowing for each user to modify their settings even when using compiled jar
	 */
	private static void loadEnvFile(){
		Path envFile = findFileIgnoreCase("ConcordiAPI.env");
		try(Stream<String> lines = Files.lines(envFile)){
			lines.map(String::trim)
					.filter(line -> !line.isBlank() && !line.startsWith("#"))
					.forEach(line -> {String[] parts = line.split("=",2);
					if (parts.length == 2 && !parts[0].isBlank() && !parts[1].isBlank()){
						System.setProperty(parts[0].trim(),parts[1].trim());
						System.out.println("Loaded env variable " + parts[0].trim());
					}
					});


		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read env file: " + envFile, e);
		}
	}


	/**
	 * Finds file with specified non-case-sensitive name inside jar directory
	 * @param targetName name to search for with file extension
	 * @return Path to the file
	 * @throws IOException Thrown when file cannot be found in the directory
	 */
	private static Path findFileIgnoreCase(String targetName){
		Path jarDir = getJarDirectory();

		try (Stream<Path> files = Files.list(jarDir)) {
			return files
					.filter(Files::isRegularFile)
					.filter(path -> path.getFileName().toString().equalsIgnoreCase(targetName))
					.findFirst()
					.orElseThrow(() -> new FileNotFoundException("No matching env file found"));
		} catch (IOException e) {
			throw new UncheckedIOException(String.format("Failed to find file %s in directory %s", targetName, jarDir), e);
		}
	}

	/**
	 * Gets the Path of the jar directory, used in {@link #findFileIgnoreCase(String)}
	 * @return Path for the jar
	 * @throws InvalidPathException Thrown when running from debugger directly or some rare environments, causes app to use different method to get the Path
	 * @throws URISyntaxException Thrown when both ways to get path not work
	 */
	private static Path getJarDirectory(){
		Path jarPath;
		try{
			jarPath = Path.of(System.getProperty("java.class.path")).toAbsolutePath();
		}
		catch (InvalidPathException ex){
            try {
				//Fallback for debuggers and unusual environments where java.class.path dont work
                jarPath = Path.of(ConcordiAPIApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
		return Files.isDirectory(jarPath) ? jarPath : jarPath.getParent();
	}


	@EventListener(ApplicationReadyEvent.class)
	private void ToRun(){
		System.out.println("Welcome to ConcordiAPI");
    }

}
