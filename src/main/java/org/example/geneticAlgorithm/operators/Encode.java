package org.example.geneticAlgorithm.operators;

import org.example.models.EncodedExam;
import org.example.models.Exam;
import org.example.models.Timeslot;

import java.util.ArrayList;

public class Encode {

    public ArrayList<EncodedExam> encodeOperator(ArrayList<Exam> examsList){

        ArrayList<EncodedExam> encodedExamList = new ArrayList<>();
        for (Exam exam: examsList) {
            String courseCode = exam.getCourse().getCourseCode();
            String classroomCode = exam.getClassroom().getClassroomCode();
            ArrayList<String> invigilators = exam.getExamInvigilators();
            Timeslot timeslot = exam.getCombinedTimeslot();

            EncodedExam encodedExam = new EncodedExam();
            encodedExam.setCourseCode(courseCode);
            encodedExam.setClassroomCode(classroomCode);
            encodedExam.setInvigilators(invigilators);
            encodedExam.setTimeSlot(timeslot);

            encodedExamList.add(encodedExam);
        }

        return encodedExamList;
    }
}

