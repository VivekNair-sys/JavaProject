package edu.ccrm.service;

import edu.ccrm.domain.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Compact single-file container for service interfaces + simple impls and custom exceptions.
 */
public class Services {

    public interface StudentService {
        void addStudent(Student s);
        Optional<Student> findStudentById(String id);
        List<Student> listAll();
    }

    public interface CourseService {
        void addCourse(Course c);
        Optional<Course> findCourseByCode(String code);
        List<Course> listAll();
    }

    public interface EnrollmentService {
        void enroll(String studentId, String courseCode) throws DuplicateEnrollmentException, MaxCreditLimitExceededException;
        void unenroll(String studentId, String courseCode);
        void recordMarks(String studentId, String courseCode, int marks);
        List<Enrollment> listEnrollmentsFor(String studentId);
        double computeGPA(String studentId, CourseService courseService);
        List<Enrollment> listAllEnrollments();
    }

    public static class DuplicateEnrollmentException extends Exception {
        public DuplicateEnrollmentException(String m) { super(m); }
    }

    public static class MaxCreditLimitExceededException extends Exception {
        public MaxCreditLimitExceededException(String m) { super(m); }
    }

    public static class StudentServiceImpl implements StudentService {
        private final Map<String, Student> students = new LinkedHashMap<>();
        @Override public void addStudent(Student s) { students.put(s.getId(), s); }
        @Override public Optional<Student> findStudentById(String id) { return Optional.ofNullable(students.get(id)); }
        @Override public List<Student> listAll() { return new ArrayList<>(students.values()); }
    }

    public static class CourseServiceImpl implements CourseService {
        private final Map<String, Course> courses = new LinkedHashMap<>();
        @Override public void addCourse(Course c) { courses.put(c.getCode(), c); }
        @Override public Optional<Course> findCourseByCode(String code) { return Optional.ofNullable(courses.get(code)); }
        @Override public List<Course> listAll() { return new ArrayList<>(courses.values()); }
    }

    public static class EnrollmentServiceImpl implements EnrollmentService {
        private final List<Enrollment> enrollments = new ArrayList<>();
        private final StudentService studentService;
        private final CourseService courseService;
        private final int MAX_CREDITS = 20;

        public EnrollmentServiceImpl(StudentService ss, CourseService cs) {
            this.studentService = ss;
            this.courseService = cs;
        }

        @Override
        public synchronized void enroll(String studentId, String courseCode) throws DuplicateEnrollmentException, MaxCreditLimitExceededException {
            boolean dup = enrollments.stream().anyMatch(e -> e.getStudentId().equals(studentId) && e.getCourseCode().equals(courseCode));
            if (dup) throw new DuplicateEnrollmentException("Already enrolled");
            int currentCredits = enrollments.stream().filter(e -> e.getStudentId().equals(studentId))
                    .mapToInt(e -> courseService.findCourseByCode(e.getCourseCode()).map(Course::getCredits).orElse(0)).sum();
            int addCredits = courseService.findCourseByCode(courseCode).map(Course::getCredits).orElse(0);
            if (currentCredits + addCredits > MAX_CREDITS) throw new MaxCreditLimitExceededException("Max credits exceeded");
            enrollments.add(new Enrollment(studentId, courseCode));
        }

        @Override public void unenroll(String studentId, String courseCode) {
            enrollments.removeIf(e -> e.getStudentId().equals(studentId) && e.getCourseCode().equals(courseCode));
        }

        @Override public void recordMarks(String studentId, String courseCode, int marks) {
            enrollments.stream().filter(e -> e.getStudentId().equals(studentId) && e.getCourseCode().equals(courseCode)).findFirst()
                    .ifPresent(e -> e.setMarks(marks));
        }

        @Override public List<Enrollment> listEnrollmentsFor(String studentId) {
            return enrollments.stream().filter(e -> e.getStudentId().equals(studentId)).collect(Collectors.toList());
        }

        @Override
        public double computeGPA(String studentId, CourseService cs) {
            List<Enrollment> list = listEnrollmentsFor(studentId);
            int totalCredits = 0;
            int totalPointsTimesCredits = 0;
            for (Enrollment e : list) {
                Course c = cs.findCourseByCode(e.getCourseCode()).orElse(null);
                if (c != null && e.getGrade() != null) {
                    int credits = c.getCredits();
                    totalCredits += credits;
                    totalPointsTimesCredits += e.getGrade().getPoints() * credits;
                }
            }
            return totalCredits == 0 ? 0.0 : ((double) totalPointsTimesCredits) / totalCredits;
        }

        @Override public List<Enrollment> listAllEnrollments() { return new ArrayList<>(enrollments); }
    }
}
