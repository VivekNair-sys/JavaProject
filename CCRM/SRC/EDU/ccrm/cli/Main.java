package edu.ccrm.cli;

import edu.ccrm.AppConfig;
import edu.ccrm.domain.*;
import edu.ccrm.service.Services;
import edu.ccrm.service.Services.*;
import edu.ccrm.io.ImportExport;
import edu.ccrm.util.FileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AppConfig cfg = AppConfig.getInstance();
        Services.StudentService studentService = new Services.StudentServiceImpl();
        Services.CourseService courseService = new Services.CourseServiceImpl();
        Services.EnrollmentService enrollmentService = new Services.EnrollmentServiceImpl(studentService, courseService);
        ImportExport importer = new ImportExport(cfg.getDataDir(), studentService, courseService, enrollmentService);

        Scanner sc = new Scanner(System.in);
        outer:
        while (true) {
            printMenu();
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1": manageStudents(sc, studentService); break;
                case "2": manageCourses(sc, courseService); break;
                case "3": enrollFlow(sc, studentService, courseService, enrollmentService); break;
                case "4": recordMarksFlow(sc, enrollmentService); break;
                case "5": transcriptFlow(sc, studentService, courseService, enrollmentService); break;
                case "6":
                    try {
                        Path td = Paths.get("test-data");
                        importer.importStudentsCsv(td.resolve("students.csv"));
                        importer.importCoursesCsv(td.resolve("courses.csv"));
                        System.out.println("Imported test-data CSVs.");
                    } catch (Exception e) {
                        System.err.println("Import failed: " + e.getMessage());
                    }
                    break;
                case "7":
                    try { importer.exportAll(); } catch (Exception e) { System.err.println("Export failed: " + e.getMessage()); }
                    break;
                case "8":
                    try {
                        var backup = FileUtils.backupFolder(cfg.getDataDir());
                        System.out.println("Backup created: " + backup.toAbsolutePath());
                        System.out.println("Backup size (bytes): " + FileUtils.recursiveSize(backup));
                    } catch (Exception e) { System.err.println("Backup failed: " + e.getMessage()); }
                    break;
                case "9":
                    List<Student> studs = studentService.listAll();
                    for (Student s : studs) {
                        double gpa = enrollmentService.computeGPA(s.getId(), courseService);
                        System.out.printf("%s (%s) => GPA: %.2f%n", s.getId(), s.getFullName(), gpa);
                    }
                    break;
                case "0": System.out.println("Exiting."); break outer;
                default: System.out.println("Unknown option.");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("\n=== CCRM ===");
        System.out.println("1 Manage Students");
        System.out.println("2 Manage Courses");
        System.out.println("3 Enroll Student");
        System.out.println("4 Record Marks");
        System.out.println("5 Print Transcript");
        System.out.println("6 Import test-data CSVs");
        System.out.println("7 Export Data");
        System.out.println("8 Backup Data");
        System.out.println("9 Reports (GPA)");
        System.out.println("0 Exit");
        System.out.print("Choice> ");
    }

    private static void manageStudents(Scanner sc, Services.StudentService ss) {
        System.out.println("a: add, l: list, d: deactivate, b: back");
        String c = sc.nextLine().trim();
        if (c.equals("a")) {
            System.out.print("id: "); String id = sc.nextLine().trim();
            System.out.print("regNo: "); String reg = sc.nextLine().trim();
            System.out.print("name: "); String name = sc.nextLine().trim();
            System.out.print("email: "); String email = sc.nextLine().trim();
            ss.addStudent(new Student(id, reg, name, email));
            System.out.println("Added.");
        } else if (c.equals("l")) {
            ss.listAll().forEach(System.out::println);
        } else if (c.equals("d")) {
            System.out.print("id: "); String id = sc.nextLine().trim();
            ss.findStudentById(id).ifPresent(Student::deactivate);
            System.out.println("Deactivated if existed.");
        }
    }

    private static void manageCourses(Scanner sc, Services.CourseService cs) {
        System.out.println("a: add, l: list, b: back");
        String c = sc.nextLine().trim();
        if (c.equals("a")) {
            System.out.print("code: "); String code = sc.nextLine().trim();
            System.out.print("title: "); String title = sc.nextLine().trim();
            System.out.print("credits: "); int credits = Integer.parseInt(sc.nextLine().trim());
            System.out.print("instructor: "); String instr = sc.nextLine().trim();
            System.out.print("semester (SPRING/SUMMER/FALL): ");
            Semester sem = Semester.valueOf(sc.nextLine().trim().toUpperCase());
            System.out.print("department: "); String dept = sc.nextLine().trim();
            Course course = new Course.Builder(code, title).credits(credits).instructor(instr).semester(sem).department(dept).build();
            cs.addCourse(course);
            System.out.println("Added.");
        } else if (c.equals("l")) {
            cs.listAll().forEach(System.out::println);
        }
    }

    private static void enrollFlow(Scanner sc, Services.StudentService ss, Services.CourseService cs, Services.EnrollmentService es) {
        System.out.print("studentId: "); String sid = sc.nextLine().trim();
        System.out.print("courseCode: "); String ccode = sc.nextLine().trim();
        try {
            es.enroll(sid, ccode);
            System.out.println("Enrolled.");
        } catch (Exception e) {
            System.err.println("Enroll failed: " + e.getMessage());
        }
    }

    private static void recordMarksFlow(Scanner sc, Services.EnrollmentService es) {
        System.out.print("studentId: "); String sid = sc.nextLine().trim();
        System.out.print("courseCode: "); String ccode = sc.nextLine().trim();
        System.out.print("marks: "); int marks = Integer.parseInt(sc.nextLine().trim());
        es.recordMarks(sid, ccode, marks);
        System.out.println("Recorded.");
    }

    private static void transcriptFlow(Scanner sc, Services.StudentService ss, Services.CourseService cs, Services.EnrollmentService es) {
        System.out.print("studentId: "); String sid = sc.nextLine().trim();
        ss.findStudentById(sid).ifPresent(s -> {
            System.out.println("Transcript for: " + s.getFullName());
            List<Enrollment> list = es.listEnrollmentsFor(sid);
            for (Enrollment e : list) {
                Course c = cs.findCourseByCode(e.getCourseCode()).orElse(null);
                System.out.println((c!=null?c.getCode()+" "+c.getTitle():"") + " marks=" + (e.getMarks()==null?"N/A":e.getMarks()) + " grade=" + e.getGrade());
            }
            System.out.printf("GPA: %.2f%n", es.computeGPA(sid, cs));
        });
    }
}
