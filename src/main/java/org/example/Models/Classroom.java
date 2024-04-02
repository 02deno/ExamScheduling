package org.example.Models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Data
public class Classroom {

    /*
    * classroomCode : String
    * classroomName : String
    * capacity : int
    * isAvailable : bool
    * isPcLab : bool, has computers?
    * courseCode : String
    * classroomProperties : String
    * */
    private String classroomCode;
    private String classroomName;
    private int capacity;
    private boolean isAvailable;
    private boolean isPcLab;
    private String courseCode;
    private String classroomProperties;

    public Classroom(String classroomCode, String classroomName, int capacity, boolean isPcLab, String classroomProperties) {
        this.classroomCode = classroomCode;
        this.classroomName = classroomName;
        this.capacity = capacity;
        this.isPcLab = isPcLab;
        this.classroomProperties = classroomProperties;
        this.isAvailable = true;
    }
}
