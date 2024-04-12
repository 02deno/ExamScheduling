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
    private ArrayList<String> availableInvigilators = new ArrayList<>();
    private boolean isPcExam;
    private int studentCapacity;
    private int beforeExamPrepTime;
    private int examDuration;
    private int afterExamPrepTime;
    private String classroomCode;
    private int remainingStudentCapacity;
    private ArrayList<String> registeredStudents = new ArrayList<>();
    private ArrayList<Timeslot> timeslots = new ArrayList<>();
    private Timeslot combinedTimeslot;
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
}
