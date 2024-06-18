package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger logger = LogManager.getLogger(EncodedExam.class);

    public static Comparator<EncodedExam> sortExamsByCourseCode() {
        return (exam1, exam2) -> exam1.getCourseCode().compareTo(exam2.getCourseCode());
    }

    public static void updateEncodedExam(ArrayList<EncodedExam> encodedExams, EncodedExam updatedEncodedExam) {
        for (int i = 0; i < encodedExams.size(); i++) {
            EncodedExam encodedExam = encodedExams.get(i);
            if (encodedExam.getCourseCode().equals(updatedEncodedExam.getCourseCode())) {
                encodedExams.remove(i);
                encodedExams.add(i, updatedEncodedExam);
                return;
            }
        }
    }
}
