package org.example.Models;

import lombok.*;

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
    * */
    private String courseName;
    private String courseCode;
    private ArrayList<String> availableInvigilators = new ArrayList<>();
    private boolean isPcExam;
    private int studentCapacity;
    private int beforeExamPrepTime;
    private int examDuration;
    private int afterExamPrepTime;

    public Course(String courseCode, String courseName, boolean isPcExam, int studentCapacity, int beforeExamPrepTime, int examDuration, int afterExamPrepTime) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.isPcExam = isPcExam;
        this.studentCapacity = studentCapacity;
        this.beforeExamPrepTime = beforeExamPrepTime;
        this.examDuration = examDuration;
        this.afterExamPrepTime = afterExamPrepTime;
    }
}
