
package com.university;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @Test
    void shouldRegisterStudentForValidCourse() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectInactiveCourse() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS301", "Data Structures", 4, 30, false, null
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(0, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectMissingPrerequisite() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS401", "Advanced Algorithms", 4, 30, true, "CS301"
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(0, course.getCurrentEnrollment());
    }

    @Test
    void shouldAllowRegistrationWhenPrerequisiteIsCompleted() {
        Student student = new Student("S101", "Anita");
        student.addCompletedPrerequisite("CS301");

        Course course = new Course(
                "CS401", "Advanced Algorithms", 4, 30, true, "CS301"
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS401"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        Student student = new Student("S101", "Anita");
        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null
        );

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));

        boolean secondAttempt = service.register(student, course);

        assertFalse(secondAttempt);
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldAllowRegistrationUpTo24Credits() {
        Student student = new Student("S101", "Anita");

        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(24, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationAbove24CreditsWithoutChangingState() {
        Student student = new Student("S102", "Rahul");

        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);
        student.addRegistration("CS301", 4);

        Course course = new Course(
                "CS401", "Advanced Algorithms", 4, 30, true, null
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
        assertEquals(24, student.getTotalCredits());
        assertEquals(0, course.getCurrentEnrollment());
    }

    @Test
    void shouldRejectRegistrationWhenCourseIsAtCapacity() {
        Student student = new Student("S201", "Rahul");
        Course course = new Course(
                "CS501", "Operating Systems", 4, 1, true, null
        );

        RegistrationService service = new RegistrationService();
        Student existingStudent = new Student("S202", "Priya");

        assertTrue(service.register(existingStudent, course));
        assertEquals(1, course.getCurrentEnrollment());

        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS501"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }

    @Test
    void shouldAllowRegistrationWhenExactlyOneSeatRemains() {
        Student student = new Student("S203", "Anita");
        Course course = new Course(
                "CS502", "Computer Networks", 4, 2, true, null
        );

        RegistrationService service = new RegistrationService();
        Student existingStudent = new Student("S204", "Kumar");

        assertTrue(service.register(existingStudent, course));
        assertEquals(1, course.getCurrentEnrollment());

        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS502"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(2, course.getCurrentEnrollment());
    }
}
