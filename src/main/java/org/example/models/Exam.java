package org.example.models;

import lombok.*;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@ToString
public class Exam {
    /*
     *
     *
     * */
    private UUID examCode;
    private Course course;
    private ArrayList<String> examInvigilators = new ArrayList<>();
    private ArrayList<Timeslot> timeslots = new ArrayList<>();
    private Timeslot combinedTimeslot;
    private Timeslot examTimeslot;
    private Classroom classroom;

    public Exam(UUID examCode, Course course) {
        this.examCode = examCode;
        this.course = course;
    }
}
