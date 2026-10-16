package com.example.studentregistration; // Places this class in the project's main package.

import org.springframework.boot.SpringApplication; // Provides the method that starts the Spring application.
import org.springframework.boot.autoconfigure.SpringBootApplication; // Imports the annotation that enables Spring Boot setup.

@SpringBootApplication // Tells Spring Boot to configure and start this application.
public class StudentRegistrationApplication { // Defines the main class for the student registration project.

	public static void main(String[] args) { // Defines the starting point Java runs first.
		SpringApplication.run(StudentRegistrationApplication.class, args); // Starts Spring Boot and its web server.
	} // Ends the main method.

} // Ends the application class.
