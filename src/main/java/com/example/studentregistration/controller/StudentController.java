package com.example.studentregistration.controller; // Places web request methods in the controller package.

import com.example.studentregistration.entity.Student; // Imports the student object sent to and from the browser.
import com.example.studentregistration.service.StudentService; // Imports the service that applies student rules.
import jakarta.validation.Valid; // Imports the annotation that turns on validation for request data.
import java.net.URI; // Imports the type used to create the new student's location address.
import java.util.List; // Imports the list type used by the all-students endpoint.
import org.springframework.http.ResponseEntity; // Imports a type that lets endpoints choose HTTP status and headers.
import org.springframework.web.bind.annotation.DeleteMapping; // Imports the annotation for DELETE endpoints.
import org.springframework.web.bind.annotation.GetMapping; // Imports the annotation for GET endpoints.
import org.springframework.web.bind.annotation.PathVariable; // Imports the annotation that reads a value from the URL path.
import org.springframework.web.bind.annotation.PostMapping; // Imports the annotation for POST endpoints.
import org.springframework.web.bind.annotation.PutMapping; // Imports the annotation for PUT endpoints.
import org.springframework.web.bind.annotation.RequestBody; // Imports the annotation that reads JSON from the request body.
import org.springframework.web.bind.annotation.RequestMapping; // Imports the annotation that sets a shared URL prefix.
import org.springframework.web.bind.annotation.RestController; // Imports the annotation that makes this class a JSON web controller.

@RestController // Tells Spring to return Java results as JSON responses.
@RequestMapping("/api/students") // Gives every endpoint in this class the /api/students URL prefix.
public class StudentController { // Defines the HTTP endpoints used by the frontend and Postman.

    private final StudentService studentService; // Keeps the service this controller calls.

    public StudentController(StudentService studentService) { // Lets Spring provide the service through constructor injection.
        this.studentService = studentService; // Saves the provided service for endpoint methods.
    } // Ends the constructor.

    @PostMapping // Connects HTTP POST requests to /api/students to this method.
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) { // Validates a JSON student and registers it.
        Student savedStudent = studentService.registerStudent(student); // Passes the checked student to the business layer.
        URI location = URI.create("/api/students/" + savedStudent.getId()); // Builds the URL where this new student can be read.
        return ResponseEntity.created(location).body(savedStudent); // Returns HTTP 201, the location header, and the saved student.
    } // Ends the create endpoint.

    @GetMapping // Connects HTTP GET requests to /api/students to this method.
    public List<Student> getAllStudents() { // Handles requests for the complete student list.
        return studentService.getAllStudents(); // Returns all students as a JSON array.
    } // Ends the list endpoint.

    @GetMapping("/{id}") // Connects HTTP GET requests with an ID such as /api/students/7.
    public Student getStudentById(@PathVariable Long id) { // Reads the ID from the URL and returns one student.
        return studentService.getStudentById(id); // Asks the service to find that student or return a 404 error.
    } // Ends the single-student endpoint.

    @PutMapping("/{id}") // Connects HTTP PUT requests with an ID such as /api/students/7.
    public Student updateStudent(@PathVariable Long id, @Valid @RequestBody Student student) { // Reads the URL ID and validates the replacement details.
        return studentService.updateStudent(id, student); // Asks the service to update the matching saved student.
    } // Ends the update endpoint.

    @DeleteMapping("/{id}") // Connects HTTP DELETE requests with an ID such as /api/students/7.
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) { // Reads the ID of the student to remove.
        studentService.deleteStudent(id); // Asks the service to remove the student or return a 404 error.
        return ResponseEntity.noContent().build(); // Returns HTTP 204 to show the deletion succeeded without a response body.
    } // Ends the delete endpoint.
} // Ends the controller class.