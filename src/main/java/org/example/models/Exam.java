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
public class Exam {
    private static final Logger logger = LogManager.getLogger(Exam.class);
    /*
     *
     *
     * */
    private int examCode;
    private Course course;
    private ArrayList<String> examInvigilators = new ArrayList<>();
    private ArrayList<Timeslot> timeslots = new ArrayList<>();
    private Timeslot combinedTimeslot;
    private Timeslot examTimeslot;
    private Classroom classroom;

    public Exam(int examCode, Course course) {
        this.examCode = examCode;
        this.course = course;
    }

    public static void updateExams(ArrayList<Exam> exams, Exam updatedExam) {
        for (int i = 0; i < exams.size(); i++) {
            Exam exam = exams.get(i);
            if (exam.getExamCode() == updatedExam.getExamCode()) {
                exams.remove(i);
                exams.add(i, updatedExam);
                return;
            }
        }
        logger.error("Exam not found for update.");
    }
}
