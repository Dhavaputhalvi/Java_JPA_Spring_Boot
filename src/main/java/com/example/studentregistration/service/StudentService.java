package com.example.studentregistration.service; // Places the business rules in the service package.

import com.example.studentregistration.entity.Student; // Imports the student data object used by these rules.
import com.example.studentregistration.repository.StudentRepository; // Imports the database operations needed by this service.
import java.util.List; // Imports the list type used to return many students.
import java.util.Locale; // Imports a language-neutral way to lowercase email addresses.
import org.springframework.http.HttpStatus; // Imports standard HTTP status codes for errors.
import org.springframework.stereotype.Service; // Imports the annotation that registers this business class with Spring.
import org.springframework.transaction.annotation.Transactional; // Imports the annotation that groups database work into transactions.
import org.springframework.web.server.ResponseStatusException; // Imports a clear exception that carries an HTTP status.

@Service // Tells Spring this class contains application business rules.
public class StudentService { // Defines the service used by the controller.

    private final StudentRepository studentRepository; // Keeps the repository this service needs to access the database.

    public StudentService(StudentRepository studentRepository) { // Lets Spring provide the repository through constructor injection.
        this.studentRepository = studentRepository; // Saves the provided repository for later methods.
    } // Ends the constructor.

    @Transactional // Makes the save and duplicate check part of one database transaction.
    public Student registerStudent(Student student) { // Applies registration rules before creating a student.
        student.setId(null); // Ignores any ID sent by a browser so MySQL creates the ID.
        student.setStudentName(student.getStudentName().trim()); // Removes accidental spaces around the student's name.
        student.setEmail(normalizeEmail(student.getEmail())); // Trims and lowercases the email consistently.
        student.setPhoneNumber(student.getPhoneNumber().trim()); // Removes accidental spaces around the phone number.

        if (studentRepository.existsByEmailIgnoreCase(student.getEmail())) { // Checks for an email already used by another student.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That email address is already registered."); // Returns HTTP 409 for a duplicate email.
        } // Ends the duplicate-email check.

        return studentRepository.save(student); // Asks JPA to save the new student and return its generated ID.
    } // Ends the registration method.

    @Transactional(readOnly = true) // Marks this database transaction as read-only.
    public List<Student> getAllStudents() { // Gets every registered student.
        return studentRepository.findAll(); // Uses JpaRepository to read all student rows.
    } // Ends the list method.

    @Transactional(readOnly = true) // Marks this database transaction as read-only.
    public Student getStudentById(Long id) { // Finds one student by the supplied ID.
        return studentRepository.findById(id) // Asks the repository to search for that ID.
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student with ID " + id + " was not found.")); // Returns HTTP 404 when there is no matching student.
    } // Ends the single-student method.

    @Transactional // Makes the find, check, and update work part of one transaction.
    public Student updateStudent(Long id, Student updatedDetails) { // Changes the details of an existing student.
        Student existingStudent = getStudentById(id); // Finds the saved student or stops with a 404 error.
        String normalizedEmail = normalizeEmail(updatedDetails.getEmail()); // Prepares the new email for a case-insensitive check.

        if (studentRepository.existsByEmailIgnoreCaseAndIdNot(normalizedEmail, id)) { // Checks whether a different student owns this email.
            throw new ResponseStatusException(HttpStatus.CONFLICT, "That email address is already registered to another student."); // Returns HTTP 409 when another student owns it.
        } // Ends the duplicate-email check.

        existingStudent.setStudentName(updatedDetails.getStudentName().trim()); // Replaces the saved name with the cleaned new name.
        existingStudent.setEmail(normalizedEmail); // Replaces the saved email with its lowercase form.
        existingStudent.setAge(updatedDetails.getAge()); // Replaces the saved age.
        existingStudent.setDepartment(updatedDetails.getDepartment()); // Replaces the saved department.
        existingStudent.setPhoneNumber(updatedDetails.getPhoneNumber().trim()); // Replaces the saved phone number with its cleaned form.

        return studentRepository.save(existingStudent); // Saves the changes while keeping the original student ID.
    } // Ends the update method.

    @Transactional // Makes the find and delete work part of one transaction.
    public void deleteStudent(Long id) { // Removes one student by ID.
        Student student = getStudentById(id); // Confirms the student exists before trying to delete it.
        studentRepository.delete(student); // Asks JPA to remove the student's database row.
    } // Ends the delete method.

    private String normalizeEmail(String email) { // Keeps email comparisons consistent regardless of letter case.
        return email.trim().toLowerCase(Locale.ROOT); // Removes surrounding spaces and uses a stable lowercase rule.
    } // Ends the email helper method.
} // Ends the service class.