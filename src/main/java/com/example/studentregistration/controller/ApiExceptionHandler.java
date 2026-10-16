package com.example.studentregistration.controller; // Places API error handling beside the web controllers.

import java.util.LinkedHashMap; // Imports a map that keeps validation errors in their original order.
import java.util.Map; // Imports the key-value type used to return field errors.
import org.springframework.dao.DataIntegrityViolationException; // Imports database constraint errors such as duplicate emails.
import org.springframework.http.HttpStatus; // Imports standard HTTP response status codes.
import org.springframework.http.ResponseEntity; // Imports a type for building an HTTP response.
import org.springframework.http.converter.HttpMessageNotReadableException; // Imports the error raised when request JSON cannot be read.
import org.springframework.web.bind.MethodArgumentNotValidException; // Imports the error raised when request validation fails.
import org.springframework.web.bind.annotation.ExceptionHandler; // Imports the annotation that selects which errors a method handles.
import org.springframework.web.bind.annotation.RestControllerAdvice; // Imports the annotation that handles errors across REST controllers.
import org.springframework.web.server.ResponseStatusException; // Imports the status-carrying errors raised by the service.

@RestControllerAdvice // Lets these methods turn controller errors into clear JSON responses.
public class ApiExceptionHandler { // Defines shared error responses for the student API.

    @ExceptionHandler(MethodArgumentNotValidException.class) // Handles invalid request fields such as a bad email or age.
    public ResponseEntity<ApiError> handleValidationError(MethodArgumentNotValidException exception) { // Builds a helpful response for invalid form data.
        Map<String, String> fieldErrors = new LinkedHashMap<>(); // Creates a map from each field name to its first error message.
        exception.getBindingResult().getFieldErrors().forEach(error -> fieldErrors.putIfAbsent( // Reads every validation error and avoids repeating a field.
                error.getField(), // Uses the Java field name as the map key.
                error.getDefaultMessage() == null ? "Invalid value." : error.getDefaultMessage())); // Uses a safe fallback when no message exists.
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "Please correct the highlighted fields.", fieldErrors); // Creates the JSON error details.
        return ResponseEntity.badRequest().body(body); // Returns HTTP 400 with field-specific messages.
    } // Ends the validation handler.

    @ExceptionHandler(HttpMessageNotReadableException.class) // Handles malformed JSON and department values that are not allowed.
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException exception) { // Builds a helpful response when request JSON cannot become a Student.
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "Request data is invalid. Check the JSON and department value.", Map.of()); // Creates a safe explanation without exposing parser details.
        return ResponseEntity.badRequest().body(body); // Returns HTTP 400 for unreadable request data.
    } // Ends the unreadable-body handler.

    @ExceptionHandler(ResponseStatusException.class) // Handles service errors that already contain an HTTP status.
    public ResponseEntity<ApiError> handleStatusError(ResponseStatusException exception) { // Builds a JSON response for not-found and conflict errors.
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value()); // Converts the supplied status number to a standard status value.
        String message = exception.getReason() == null ? "The request could not be completed." : exception.getReason(); // Uses the reason or a safe fallback message.
        ApiError body = new ApiError(status.value(), message, Map.of()); // Creates the JSON error details without field errors.
        return ResponseEntity.status(status).body(body); // Returns the original status and its helpful message.
    } // Ends the status handler.

    @ExceptionHandler(DataIntegrityViolationException.class) // Handles a duplicate email caught by MySQL during a save.
    public ResponseEntity<ApiError> handleDatabaseConflict(DataIntegrityViolationException exception) { // Builds a useful response for a database uniqueness conflict.
        ApiError body = new ApiError(HttpStatus.CONFLICT.value(), "That email address is already registered.", Map.of()); // Creates a safe duplicate-email message.
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body); // Returns HTTP 409 Conflict.
    } // Ends the database conflict handler.

    public record ApiError(int status, String message, Map<String, String> fieldErrors) { // Defines the JSON shape shared by API error responses.
    } // Ends the error response record.
} // Ends the shared error handler.