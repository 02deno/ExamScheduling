package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncodedExam {

    private String courseCode;
    private String classroomCode;
    private Timeslot timeSlot;
    private ArrayList<String> invigilators;
}
