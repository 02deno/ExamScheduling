package org.example.models;

import lombok.*;

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
    private int maxCoursesTakenCount;
    private int remainingCourseCapacity;

    public Student(String ID, String name, String surname, int maxCoursesTakenCount) {
        super(ID, name, surname);
        this.maxCoursesTakenCount = maxCoursesTakenCount;
        this.remainingCourseCapacity = maxCoursesTakenCount;
    }


}
