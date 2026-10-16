package com.example.studentregistration; // Places this test in the application's main package.

import org.junit.jupiter.api.Test; // Imports the annotation that marks a JUnit test method.
import org.springframework.boot.test.context.SpringBootTest; // Imports the annotation that starts Spring Boot for this test.

@SpringBootTest // Tells the test to check whether the full Spring application context starts.
class StudentRegistrationApplicationTests { // Defines a basic application startup test.

	@Test // Marks this method as a test that JUnit should run.
	void contextLoads() { // Checks whether Spring can create the application context.
	} // Ends the context startup test.

} // Ends the test class.
