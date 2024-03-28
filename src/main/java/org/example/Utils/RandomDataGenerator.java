package org.example.Utils;

import java.io.FileInputStream;

public class RandomDataGenerator {
    /*
    * Step 1 : generate random courses with no students, professors and invigilators
    * randomCourse(courseName : String, courseCode : String,
    *               isPcExam : boolean, studentCapacity : int)
    * isPcExam -> this can be maybe optional and later decided by professor?
    *
    *
    * Step 2 : generate random professors, invigilators, students with no courses
    * professor : Professor(id, name, surname, maxCoursesGivenCount)
    * invigilator : Invigilator(id, name, surname, maxCoursesMonitoredCount)
    * student : Student(id, name, surname,maxCoursesTakenCount)
    *
    * Step 3 : Map professors with courses in range(1,maxCoursesGivenCount)
    * remove these courses from coursesWithNoProfessors list and continue
    * this step until there is no Course left in this list
    *
    * Step 4 : Do the same step with students
    *
    * Step 5 : Randomly assign 2 or 3 invigilators to the courses
    *
    * Step 6 : generate classrooms
    * classroom = Classroom(classroomCode, capacity, isLab)
    *
    * This process of data generating can also be done by a excel file
    * */

}
