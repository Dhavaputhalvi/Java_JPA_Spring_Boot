package com.example.studentregistration.repository; // Places the database access interface in the repository package.

import com.example.studentregistration.entity.Student; // Imports the entity this repository stores.
import org.springframework.data.jpa.repository.JpaRepository; // Imports Spring Data's ready-made database operations.

public interface StudentRepository extends JpaRepository<Student, Long> { // Creates student database operations using Student rows and Long IDs.

    boolean existsByEmailIgnoreCase(String email); // Checks whether any student already uses this email, ignoring letter case.

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id); // Checks whether another student uses this email during an update.
} // Ends the repository interface.