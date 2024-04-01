package org.example.DataPreprocessing;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class RandomDataGenerator {
    /*
    * This class should take a number of students or number of classes
    * and generate exam instances according to this number.
    *
    * Step 1 : generate random courses with no students and invigilators
    * randomCourse(courseName : String, courseCode : String,
    *               isPcExam : boolean, studentCapacity : int)
    * isPcExam -> this can be maybe optional and later decided by professor?
    *
    *
    * Step 2 : generate random invigilators, students with no courses
    * invigilator : Invigilator(id, name, surname, maxCoursesMonitoredCount)
    * student : Student(id, name, surname,maxCoursesTakenCount)
    *
    * Step 3 : generate classrooms
    * classroom = Classroom(classroomCode, capacity, isLab)
    *
    * Step 4 : Map students with courses in range(1,maxCoursesTakenCount)
    * remove these courses from coursesWithNoProfessors list and continue
    * this step until there is no Course left in this list
    *
    * Step 5 : Randomly assign 2 or 3 invigilators to the courses
    *
    * Step 6 : Map courses with classrooms and generate exams
    *
    * This process of data generating can also be done by an Excel file
    * */

    private static final Logger logger = LogManager.getLogger(RandomDataGenerator.class);
    public HashMap<String, HashMap<String, ArrayList<Object>>> combineAllData() {
        //Step 1,2,3

        String courseDataPath = "data/tum_dersler.xlsx";
        String studentDataPath = "data/students.xlsx";
        String invigilatorDataPath = "data/invigilators.xlsx";
        String classroomDataPath = "data/Classrooms_20211103.xlsx";
        CourseDataParser courseDataParser = new CourseDataParser(courseDataPath);
        PersonDataParser personDataParser = new PersonDataParser(studentDataPath, invigilatorDataPath);
        ClassroomDataParser classroomDataParser = new ClassroomDataParser(classroomDataPath);

        HashMap<String, ArrayList<Object>> courseData = courseDataParser.parseCourseData();
        //logger.info(courseData);
        logger.info("Course instances created successfully:)");

        HashMap<String, ArrayList<Object>> studentData = personDataParser.parseStudentData();
        //logger.info(studentData);
        logger.info("Student instances created successfully:)");

        HashMap<String, ArrayList<Object>> invigilatorData = personDataParser.parseInvigilatorData();
        logger.info(invigilatorData);
        logger.info("Invigilator instances created successfully:)");

        HashMap<String, ArrayList<Object>> classroomData = classroomDataParser.parseClassroomData();
        //logger.info(classroomData);
        logger.info("Classroom instances created successfully:)");

        HashMap<String, HashMap<String, ArrayList<Object>>> result = new HashMap<>();

        result.put("courseData", courseData);
        result.put("studentData", studentData);
        result.put("invigilatorData", invigilatorData);
        result.put("classroomData", classroomData);

        return result;
    }

    public void generateRandomlyMappedData() {
        //Step 4,5,6
    }
}
