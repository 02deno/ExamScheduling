package org.example.models;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@ToString
public class Classroom {
    private static final Logger logger = LogManager.getLogger(Classroom.class);

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
    private boolean isPcLab;
    private ArrayList<String> courseCodes = new ArrayList<>();
    private ArrayList<UUID> placedExams = new ArrayList<>();
    private String classroomProperties;

    public Classroom(String classroomCode, String classroomName, int capacity, boolean isPcLab, String classroomProperties) {
        this.classroomCode = classroomCode;
        this.classroomName = classroomName;
        this.capacity = capacity;
        this.isPcLab = isPcLab;
        this.classroomProperties = classroomProperties;
    }

    public static void updateClassroom(ArrayList<Classroom> classrooms, Classroom updatedClassroom) {
        for (int i = 0; i < classrooms.size(); i++) {
            Classroom classroom = classrooms.get(i);
            if (classroom.getClassroomCode().equals(updatedClassroom.getClassroomCode())) {
                classrooms.remove(i);
                classrooms.add(i, updatedClassroom);
                return;
            }
        }
        logger.error("Classroom not found for update.");
    }
}
