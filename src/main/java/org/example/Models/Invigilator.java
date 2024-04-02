package org.example.Models;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class Invigilator {
    /*
    * id : String/UUID/int
    * name : String
    * surname : String
    * monitoredCourses : Array<Course>
    * isAvailable : boolean
    * maxCoursesMonitoredCount : int
    * */
    private String ID;
    private String name;
    private String surname;
    private ArrayList<String> monitoredCourses = new ArrayList<>();
    private boolean isAvailable;
    private int maxCoursesMonitoredCount;

    public Invigilator(String ID, String name, String surname, int maxCoursesMonitoredCount) {
        this.ID = ID;
        this.name = name;
        this.surname = surname;
        this.maxCoursesMonitoredCount = maxCoursesMonitoredCount;
        if(maxCoursesMonitoredCount == 0) {
            this.isAvailable = false;
        }else {
            this.isAvailable = true;
        }
    }
}
