package com.example.studentregistration.entity; // Places this database entity in the entity package.

import jakarta.persistence.Column; // Imports the annotation that configures a database column.
import jakarta.persistence.Entity; // Imports the annotation that makes this class a database entity.
import jakarta.persistence.EnumType; // Imports the setting used to choose how an enum is stored.
import jakarta.persistence.Enumerated; // Imports the annotation that maps an enum to a database column.
import jakarta.persistence.GeneratedValue; // Imports the annotation that lets the database create an ID.
import jakarta.persistence.GenerationType; // Imports the strategy used to create the ID.
import jakarta.persistence.Id; // Imports the annotation that marks the primary key.
import jakarta.persistence.Table; // Imports the annotation that chooses the database table name.
import jakarta.validation.constraints.Email; // Imports the rule that checks email formatting.
import jakarta.validation.constraints.Max; // Imports the rule that sets the largest allowed value.
import jakarta.validation.constraints.Min; // Imports the rule that sets the smallest allowed value.
import jakarta.validation.constraints.NotBlank; // Imports the rule that rejects empty text.
import jakarta.validation.constraints.NotNull; // Imports the rule that rejects missing values.
import jakarta.validation.constraints.Pattern; // Imports the rule that checks text against a pattern.
import jakarta.validation.constraints.Size; // Imports the rule that limits text length.

@Entity // Tells JPA that each Student object represents one database row.
@Table(name = "students") // Gives the database table the clear name students.
public class Student { // Defines the Java object used to hold one student's details.

    @Id // Marks id as the unique primary key for each student.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Asks MySQL to generate each new ID automatically.
    private Long id; // Stores the automatically generated student ID.

    @NotBlank(message = "Student name is required.") // Rejects a missing name or a name made only of spaces.
    @Size(max = 100, message = "Student name must be 100 characters or fewer.") // Limits the name to fit its database column.
    @Column(nullable = false, length = 100) // Makes the name required and limits its database size.
    private String studentName; // Stores the student's name as text.

    @NotBlank(message = "Email address is required.") // Rejects a missing email address.
    @Email(message = "Enter a valid email address.") // Checks that the email looks like a real email address.
    @Size(max = 254, message = "Email address must be 254 characters or fewer.") // Limits the email to a practical maximum length.
    @Column(nullable = false, unique = true, length = 254) // Requires each database email to be unique.
    private String email; // Stores the student's email address.

    @NotNull(message = "Age is required.") // Rejects a missing age.
    @Min(value = 5, message = "Age must be at least 5.") // Rejects ages below 5.
    @Max(value = 100, message = "Age must be 100 or less.") // Rejects ages above 100.
    @Column(nullable = false) // Makes age a required database value.
    private Integer age; // Stores the student's age as a whole number.

    @NotNull(message = "Choose a department.") // Rejects a missing department.
    @Enumerated(EnumType.STRING) // Stores the department name instead of a fragile number.
    @Column(nullable = false, length = 10) // Makes the department required and reserves enough space.
    private Department department; // Stores one of the allowed department choices.

    @NotBlank(message = "Phone number is required.") // Rejects a missing phone number.
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Enter 7 to 15 digits, with an optional leading +.") // Allows a simple international phone format.
    @Column(nullable = false, length = 16) // Makes the phone required and reserves room for a plus sign.
    private String phoneNumber; // Stores the phone as text so a leading zero is preserved.

    public Student() { // Provides the empty constructor JPA needs when it reads database rows.
    } // Ends the empty constructor.

    public Student(String studentName, String email, Integer age, Department department, String phoneNumber) { // Creates a student using all details except the generated ID.
        this.studentName = studentName; // Copies the supplied name into this student.
        this.email = email; // Copies the supplied email into this student.
        this.age = age; // Copies the supplied age into this student.
        this.department = department; // Copies the selected department into this student.
        this.phoneNumber = phoneNumber; // Copies the supplied phone number into this student.
    } // Ends the details constructor.

    public Long getId() { // Provides read access to the generated student ID.
        return id; // Gives the caller the current ID.
    } // Ends the ID getter.

    public void setId(Long id) { // Provides write access so the service can protect the generated ID.
        this.id = id; // Stores the supplied ID value.
    } // Ends the ID setter.

    public String getStudentName() { // Provides read access to the student's name.
        return studentName; // Gives the caller the current name.
    } // Ends the name getter.

    public void setStudentName(String studentName) { // Provides write access to the student's name.
        this.studentName = studentName; // Stores the supplied name.
    } // Ends the name setter.

    public String getEmail() { // Provides read access to the student's email.
        return email; // Gives the caller the current email.
    } // Ends the email getter.

    public void setEmail(String email) { // Provides write access to the student's email.
        this.email = email; // Stores the supplied email.
    } // Ends the email setter.

    public Integer getAge() { // Provides read access to the student's age.
        return age; // Gives the caller the current age.
    } // Ends the age getter.

    public void setAge(Integer age) { // Provides write access to the student's age.
        this.age = age; // Stores the supplied age.
    } // Ends the age setter.

    public Department getDepartment() { // Provides read access to the student's department.
        return department; // Gives the caller the current department.
    } // Ends the department getter.

    public void setDepartment(Department department) { // Provides write access to the student's department.
        this.department = department; // Stores the supplied department.
    } // Ends the department setter.

    public String getPhoneNumber() { // Provides read access to the student's phone number.
        return phoneNumber; // Gives the caller the current phone number.
    } // Ends the phone getter.

    public void setPhoneNumber(String phoneNumber) { // Provides write access to the student's phone number.
        this.phoneNumber = phoneNumber; // Stores the supplied phone number.
    } // Ends the phone setter.

    public enum Department { // Lists every department a student may choose.
        CSE, // Represents Computer Science and Engineering.
        ECE, // Represents Electronics and Communication Engineering.
        EEE, // Represents Electrical and Electronics Engineering.
        MECH, // Represents Mechanical Engineering.
        CIVIL // Represents Civil Engineering.
    } // Ends the department choices.
} // Ends the Student entity class.