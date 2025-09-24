package edu.ccrm.io;

import edu.ccrm.domain.*;
import edu.ccrm.service.Services.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportExport {
    private final Path dataDir;
    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public ImportExport(Path dataDir, StudentService ss, CourseService cs, EnrollmentService es) {
        this.dataDir = dataDir;
        this.studentService = ss;
        this.courseService = cs;
        this.enrollmentService = es;
    }

    public void importStudentsCsv(Path csv) throws IOException {
        try (Stream<String> lines = Files.lines(csv)) {
            lines.skip(1).map(l -> l.split(",")).forEach(cols -> {
                String id = cols[0].trim();
                String reg = cols.length > 1 ? cols[1].trim() : "R-" + id;
                String name = cols.length > 2 ? cols[2].trim() : "Unknown";
                String email = cols.length > 3 ? cols[3].trim() : "none@example.com";
                studentService.addStudent(new Student(id, reg, name, email));
            });
        }
    }

    public void importCoursesCsv(Path csv) throws IOException {
        try (Stream<String> lines = Files.lines(csv)) {
            lines.skip(1).map(l -> l.split(",")).forEach(cols -> {
                String code = cols[0].trim();
                String title = cols.length > 1 ? cols[1].trim() : "Untitled";
                int credits = cols.length > 2 ? Integer.parseInt(cols[2].trim()) : 3;
                String instr = cols.length > 3 ? cols[3].trim() : "TBD";
                Semester sem = Semester.valueOf(cols.length > 4 ? cols[4].trim().toUpperCase() : "FALL");
                String dept = cols.length > 5 ? cols[5].trim() : "GEN";
                Course c = new Course.Builder(code, title).credits(credits).instructor(instr).semester(sem).department(dept).build();
                courseService.addCourse(c);
            });
        }
    }

    public void exportAll() throws IOException {
        Files.createDirectories(dataDir);
        Path studentsOut = dataDir.resolve("students_export.csv");
        List<String> sLines = studentService.listAll().stream()
                .map(s -> String.join(",", s.getId(), s.getRegNo(), s.getFullName(), s.getEmail()))
                .collect(Collectors.toList());
        Files.write(studentsOut, Stream.concat(Stream.of("id,regNo,fullName,email"), sLines.stream()).collect(Collectors.toList()));

        Path coursesOut = dataDir.resolve("courses_export.csv");
        List<String> cLines = courseService.listAll().stream()
                .map(c -> String.join(",", c.getCode(), c.getTitle(), String.valueOf(c.getCredits()), c.getInstructor(), c.getSemester().name(), c.getDepartment()))
                .collect(Collectors.toList());
        Files.write(coursesOut, Stream.concat(Stream.of("code,title,credits,instructor,semester,department"), cLines.stream()).collect(Collectors.toList()));

        Path enrollOut = dataDir.resolve("enrollments_export.csv");
        List<String> eLines = enrollmentService.listAllEnrollments().stream()
                .map(e -> String.join(",", e.getStudentId(), e.getCourseCode(), String.valueOf(e.getMarks() == null ? -1 : e.getMarks()), e.getGrade().name()))
                .collect(Collectors.toList());
        Files.write(enrollOut, Stream.concat(Stream.of("studentId,courseCode,marks,grade"), eLines.stream()).collect(Collectors.toList()));

        System.out.println("Exported CSVs to: " + dataDir.toAbsolutePath());
    }
}
