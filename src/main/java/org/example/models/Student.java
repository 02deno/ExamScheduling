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
public class Student extends Person{
    /*
     * attributes :
     * id : String or int in some format(190503029), required
     * name : String, required
     * surname : String, required
     * registeredCourses : Array, optional
     * maxCoursesTakenCount : int, range(1,11), required
     * remainingStudentCapacity : int
     *
     * methods :
     *
     * */
    private ArrayList<String> registeredCourses = new ArrayList<>();
    private int year;
    private String department;
    private int maxCoursesTakenCount;
    private int remainingCourseCapacity;
    private static final Logger logger = LogManager.getLogger(Student.class);

    public Student(String ID, String name, String surname, int maxCoursesTakenCount) {
        super(ID, name, surname);
        this.maxCoursesTakenCount = maxCoursesTakenCount;
        this.remainingCourseCapacity = maxCoursesTakenCount;
    }

    public Student(String ID, String name, String surname, ArrayList<String> courses, String department, int year) {
        super(ID, name, surname);
        this.department = department;
        this.registeredCourses = courses;
        this.year = year;
    }

    public static void updateStudent(ArrayList<Student> students, Student updatedStudent) {
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            if (student.getID().equals(updatedStudent.getID())) {
                students.remove(i);
                students.add(i, updatedStudent);
                return;
            }
        }
        logger.error("Student not found for update.");
    }


}
