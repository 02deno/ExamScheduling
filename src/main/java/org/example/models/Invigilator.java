package org.example.models;

import lombok.*;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@ToString
public class Invigilator extends Person{
    /*
    * id : String/UUID/int
    * name : String
    * surname : String
    * monitoredCourses : Array<Course>
    * isAvailable : boolean
    * maxCoursesMonitoredCount : int
    * */
    private ArrayList<String> monitoredCourses = new ArrayList<>();
    private ArrayList<UUID> monitoredExams = new ArrayList<>();
    private boolean isAvailable;
    private int maxCoursesMonitoredCount;

    public Invigilator(String ID, String name, String surname, int maxCoursesMonitoredCount) {
        super(ID, name, surname);
        this.maxCoursesMonitoredCount = maxCoursesMonitoredCount;
        this.isAvailable = maxCoursesMonitoredCount != 0;
    }
}
