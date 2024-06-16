package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncodedExam {

    private String courseCode;
    private String classroomCode;
    private Timeslot timeSlot;
    private ArrayList<String> invigilators;

    public static Comparator<EncodedExam> sortExamsByCourseCode() {
        return (exam1, exam2) -> exam1.getCourseCode().compareTo(exam2.getCourseCode());
    }
}
