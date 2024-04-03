package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dataPreprocessing.RandomDataGenerator;
import org.example.models.Classroom;
import org.example.models.Course;
import org.example.models.Invigilator;
import org.example.models.Student;
import org.example.utils.ArraylistHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class App
{
    public static void main( String[] args )
    {
        final Logger logger = LogManager.getLogger(App.class);
        logger.info("Application started...");
        HashMap<String, HashMap<String, ArrayList<Object>>> randomData = RandomDataGenerator.combineAllData();
        ArrayList<Course> courses = RandomDataGenerator.generateCourseInstances(randomData.get("courseData"));
        ArrayList<Invigilator> invigilators = RandomDataGenerator.generateInvigilatorInstances(randomData.get("invigilatorData"));
        ArrayList<Classroom> classrooms = RandomDataGenerator.generateClassroomInstances(randomData.get("classroomData"));
        ArrayList<Student> students = RandomDataGenerator.generateStudentInstances(randomData.get("studentData"));

        HashMap<String, ArrayList<?>> resultCoursesInvigilators = RandomDataGenerator.mapInvigilatorsWithCourses(courses, invigilators);
        courses = ArraylistHelper.castArrayList(resultCoursesInvigilators.get("courses"), Course.class);
        invigilators = ArraylistHelper.castArrayList(resultCoursesInvigilators.get("invigilators"), Invigilator.class);

        HashMap<String, ArrayList<?>> resultCoursesClassrooms = RandomDataGenerator.mapCoursesWithClassrooms(courses, classrooms);
        courses = ArraylistHelper.castArrayList(resultCoursesClassrooms.get("courses"), Course.class);
        classrooms = ArraylistHelper.castArrayList(resultCoursesClassrooms.get("classrooms"), Classroom.class);

        HashMap<String, ArrayList<?>> resultCoursesStudents = RandomDataGenerator.mapStudentsWithCourses(courses, students);

        logger.info("Application finished!");
    }

}
