const apiUrl = "/api/students"; // Stores the shared address of the student REST API.
const studentForm = document.querySelector("#studentForm"); // Finds the registration form on the page.
const studentTableBody = document.querySelector("#studentsTableBody"); // Finds the table area where student rows are added.
const studentCount = document.querySelector("#studentCount"); // Finds the number display above the form.
const emptyState = document.querySelector("#emptyState"); // Finds the message shown when there are no saved students.
const statusMessage = document.querySelector("#statusMessage"); // Finds the accessible area for success and error messages.
const formTitle = document.querySelector("#form-title"); // Finds the form heading so it can change during editing.
const submitButton = document.querySelector("#submitButton"); // Finds the submit button so its label can change during editing.
const cancelEditButton = document.querySelector("#cancelEditButton"); // Finds the button that leaves edit mode.
let editingStudentId = null; // Remembers which student is being changed, or null when adding a student.

studentForm.addEventListener("submit", saveStudent); // Sends the form to the API when the user submits it.
cancelEditButton.addEventListener("click", resetForm); // Restores the empty registration form when editing is cancelled.
loadStudents(); // Requests the saved students as soon as the page is ready.

async function loadStudents() { // Downloads all student records and draws them in the table.
    setStatus("Loading student records..."); // Tells the user that the records are being fetched.

    try { // Starts a block that can handle network and server errors.
        const response = await fetch(apiUrl); // Sends an HTTP GET request to Spring Boot.
        const students = await readJson(response); // Reads the JSON response or turns an error response into a message.
        renderStudents(students); // Places each returned student into a table row.
        setStatus("Student records loaded."); // Confirms that the table is up to date.
    } catch (error) { // Runs when the browser cannot reach the server or the request fails.
        setStatus(error.message, true); // Shows the reason in the status area using the error style.
    } // Ends the request error handling block.
} // Ends the student-list function.

async function saveStudent(event) { // Creates a student or saves the edits to an existing student.
    event.preventDefault(); // Stops the browser from reloading the whole page after form submission.

    if (!studentForm.reportValidity()) { // Uses the browser's required, email, number, and pattern checks.
        return; // Stops here when any browser-side field check fails.
    } // Ends the browser validation check.

    const formData = new FormData(studentForm); // Collects the values entered in the form fields.
    const student = { // Builds the student object that the API expects as JSON.
        studentName: formData.get("studentName").trim(), // Sends the cleaned student name.
        email: formData.get("email").trim(), // Sends the cleaned email address.
        age: Number(formData.get("age")), // Converts the age text into a JSON number.
        department: formData.get("department"), // Sends the chosen department name.
        phoneNumber: formData.get("phoneNumber").trim() // Sends the cleaned phone number.
    }; // Ends the student data object.
    const wasEditing = editingStudentId !== null; // Remembers whether this request is creating or updating.
    const requestUrl = wasEditing ? `${apiUrl}/${editingStudentId}` : apiUrl; // Adds the ID to the URL only for an update.
    const requestMethod = wasEditing ? "PUT" : "POST"; // Chooses the HTTP method for the selected operation.

    try { // Starts a block that can handle API errors.
        const response = await fetch(requestUrl, { // Sends the student information to Spring Boot.
            method: requestMethod, // Sets POST for a new record or PUT for an edit.
            headers: { "Content-Type": "application/json" }, // Tells Spring Boot the request body contains JSON.
            body: JSON.stringify(student) // Converts the JavaScript object into JSON text.
        }); // Ends the Fetch API options.

        await readJson(response); // Checks the HTTP result and reads any error message.
        resetForm(); // Clears the fields and exits edit mode after a successful save.
        await loadStudents(); // Reloads the table so it shows the saved version.
        setStatus(wasEditing ? "Student details updated." : "Student registered successfully."); // Confirms which operation completed.
    } catch (error) { // Runs when validation, duplicate checking, or networking fails.
        setStatus(error.message, true); // Shows the API's explanation to the user.
    } // Ends the save error handling block.
} // Ends the create and update function.

function renderStudents(students) { // Rebuilds the visible table from the latest student list.
    studentTableBody.replaceChildren(); // Removes table rows from the previous response.
    studentCount.textContent = String(students.length); // Updates the total student count.
    emptyState.hidden = students.length !== 0; // Shows the empty message only when the list has no students.

    students.forEach((student) => { // Makes one table row for every returned student.
        const row = document.createElement("tr"); // Creates a new row without inserting unsafe HTML text.
        const values = [student.id, student.studentName, student.email, student.age, student.department, student.phoneNumber]; // Collects values in the same order as the table headings.

        values.forEach((value) => { // Creates one cell for each student detail.
            const cell = document.createElement("td"); // Creates a safe table cell element.
            cell.textContent = String(value); // Displays the value as text instead of interpreting it as HTML.
            row.append(cell); // Adds this detail cell to the current student row.
        }); // Ends the student-detail cell loop.

        const actionsCell = document.createElement("td"); // Creates the cell that holds edit and delete controls.
        const actions = document.createElement("div"); // Creates a container for the row action buttons.
        actions.className = "row-actions"; // Applies the spacing style for row buttons.
        actions.append(createActionButton("Edit", "row-button", () => startEditing(student))); // Adds a button that loads this student into the form.
        actions.append(createActionButton("Delete", "row-button row-button-delete", () => deleteStudent(student))); // Adds a button that removes this student.
        actionsCell.append(actions); // Places the action buttons inside their table cell.
        row.append(actionsCell); // Adds the action cell as the final cell in the row.
        studentTableBody.append(row); // Adds the completed row to the table.
    }); // Ends the student row loop.
} // Ends the table rendering function.

function createActionButton(label, className, action) { // Makes a safe button and connects it to one student action.
    const button = document.createElement("button"); // Creates a button element for a table row.
    button.type = "button"; // Prevents the row button from submitting the registration form.
    button.className = className; // Applies the correct edit or delete appearance.
    button.textContent = label; // Sets the visible button label as plain text.
    button.addEventListener("click", action); // Runs the supplied action when this button is clicked.
    return button; // Gives the finished button back to the caller.
} // Ends the row-button helper.

function startEditing(student) { // Copies one row's details into the form for editing.
    editingStudentId = student.id; // Remembers which database record will be updated.
    studentForm.elements.studentName.value = student.studentName; // Fills in the saved student name.
    studentForm.elements.email.value = student.email; // Fills in the saved email address.
    studentForm.elements.age.value = student.age; // Fills in the saved age.
    studentForm.elements.department.value = student.department; // Selects the saved department.
    studentForm.elements.phoneNumber.value = student.phoneNumber; // Fills in the saved phone number.
    formTitle.textContent = `Edit student ${student.id}`; // Makes it clear which record is being edited.
    submitButton.textContent = "Save changes"; // Changes the button label to match the edit action.
    cancelEditButton.hidden = false; // Shows the button for leaving edit mode.
    studentForm.elements.studentName.focus(); // Moves keyboard focus to the first editable field.
    window.scrollTo({ top: 0, behavior: "smooth" }); // Brings the form into view on a long page.
} // Ends the edit-form helper.

async function deleteStudent(student) { // Deletes one student after asking the user to confirm.
    const confirmed = window.confirm(`Delete ${student.studentName} from the register?`); // Checks that the user really wants to delete this record.

    if (!confirmed) { // Checks whether the user cancelled the confirmation.
        return; // Keeps the student unchanged when deletion is cancelled.
    } // Ends the confirmation check.

    try { // Starts a block that can handle delete errors.
        const response = await fetch(`${apiUrl}/${student.id}`, { method: "DELETE" }); // Sends an HTTP DELETE request for this student ID.
        if (!response.ok) { // Checks for a server error because successful DELETE has no JSON body.
            await readJson(response); // Reads the server error and throws a helpful message.
        } // Ends the failed-delete check.
        if (editingStudentId === student.id) { // Checks whether this record is currently open in the form.
            resetForm(); // Clears the form so it does not keep editing a deleted record.
        } // Ends the current-record check.
        await loadStudents(); // Reloads the table after deleting the student.
        setStatus("Student deleted."); // Confirms that the record was removed.
    } catch (error) { // Runs when the API cannot delete the student.
        setStatus(error.message, true); // Shows the API's explanation to the user.
    } // Ends the delete error handling block.
} // Ends the delete function.

function resetForm() { // Clears the form and restores its new-registration state.
    studentForm.reset(); // Clears every input and restores the department prompt.
    editingStudentId = null; // Forgets the student that was being edited.
    formTitle.textContent = "Register a student"; // Restores the registration heading.
    submitButton.textContent = "Register student"; // Restores the registration button label.
    cancelEditButton.hidden = true; // Hides the cancel button outside edit mode.
} // Ends the form-reset helper.

async function readJson(response) { // Reads an API response and turns errors into useful messages.
    let result = {}; // Starts with an empty object in case the response has no JSON body.

    try { // Starts a block that can handle an empty or non-JSON response.
        result = await response.json(); // Converts a JSON response body into a JavaScript object.
    } catch { // Runs when the response body is empty or is not JSON.
        result = {}; // Keeps the empty object so the HTTP status can still be reported.
    } // Ends the response parsing block.

    if (!response.ok) { // Checks whether the server returned a non-success status.
        const fieldMessages = Object.values(result.fieldErrors || {}).join(" "); // Collects individual validation messages if present.
        const message = fieldMessages || result.message || `Request failed with status ${response.status}.`; // Chooses the most helpful available error text.
        throw new Error(message); // Sends the message to the matching error handler.
    } // Ends the unsuccessful-response check.

    return result; // Gives the parsed response body to the caller.
} // Ends the response-reading helper.

function setStatus(message, isError = false) { // Updates the live message shown below the page heading.
    statusMessage.textContent = message; // Displays the message safely as plain text.
    statusMessage.dataset.tone = isError ? "error" : "success"; // Applies the error color only when something failed.
} // Ends the status-message helper.