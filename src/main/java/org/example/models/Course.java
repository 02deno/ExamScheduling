package org.example.models;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@ToString
public class Course {

    /*
    * courseName : String
    * courseCode : String
    * availableInvigilators : Array<String>
    * isPcExam : boolean
    * studentCapacity : int
    * beforeExamPrepTime : int
    * examDuration : int
    * afterExamPrepTime : int
    * remainingStudentCapacity : int
    * */
    private String courseName;
    private String courseCode;
    private boolean isPcExam;
    private int studentCapacity;
    private int beforeExamPrepTime;
    private int examDuration;
    private int afterExamPrepTime;
    private int remainingStudentCapacity;
    private ArrayList<String> registeredStudents = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(Course.class);
    public Course(String courseCode, String courseName, boolean isPcExam, int studentCapacity, int beforeExamPrepTime, int examDuration, int afterExamPrepTime) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.isPcExam = isPcExam;
        this.studentCapacity = studentCapacity;
        this.beforeExamPrepTime = beforeExamPrepTime;
        this.examDuration = examDuration;
        this.afterExamPrepTime = afterExamPrepTime;
        this.remainingStudentCapacity = studentCapacity;
    }

    public static void updateCourse(ArrayList<Course> courses, Course updatedCourse) {
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            if (course.getCourseCode().equals(updatedCourse.getCourseCode())) {
                courses.remove(i);
                courses.add(i, updatedCourse);
                return;
            }
        }
        logger.error("Course not found for update.");
    }

    public static Course findByCourseCode(ArrayList<Course> courses, String courseCode) {
        for (Course course : courses) {
            if (course.getCourseCode().equals(courseCode)) {
                return course;
            }
        }
        logger.error("Could not find a course with course code: " + courseCode);
        return null;
    }
}
