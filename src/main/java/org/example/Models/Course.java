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
    * professor : Professor
    * availableInvigilators : Array<Invigilator>
    * isPcExam : boolean
    * studentCapacity : int
    * */
    private String courseName;
    private String courseCode;
    private Professor professor;
    private ArrayList<Invigilator> availableInvigilators;
    private boolean isPcExam;
    private int studentCapacity;


}
