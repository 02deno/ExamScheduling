package org.example.models;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

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
    private ArrayList<Integer> monitoredExams = new ArrayList<>();
    private boolean isAvailable;
    private int maxCoursesMonitoredCount;
    private static final Logger logger = LogManager.getLogger(Invigilator.class);

    public Invigilator(String ID, String name, String surname, int maxCoursesMonitoredCount) {
        super(ID, name, surname);
        this.maxCoursesMonitoredCount = maxCoursesMonitoredCount;
        this.isAvailable = maxCoursesMonitoredCount != 0;
    }

    public static Invigilator findByInvigilatorId(ArrayList<Invigilator> invigilators, String invigilatorId) {
        for (Invigilator invigilator : invigilators) {
            if (invigilator.getID().equals(invigilatorId)) {
                return invigilator;
            }
        }
        logger.error("Could not find a invigilator with invigilator id: " + invigilatorId);
        return null;
    }
}
