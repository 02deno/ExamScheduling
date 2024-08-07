package org.example.dataPreprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.*;
import org.example.utils.ConfigHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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
     * TODO(Deniz) : Remove or update "outliers like courses that requires 7 timeslots
    *  or courses that hava a student capacity more than 300 there are no classrooms
    *  available in our school for this capacity. Instead of removing we can add artificial
    *  classrooms that can contain more than 400 students.
    *
    * TODO(Deniz) : Add constraints like number of classroom that can be chosen.
     *
     * TODO(Deniz) : Add credit information to the courses randomly
    * */

    private static final Logger logger = LogManager.getLogger(RandomDataGenerator.class);
    private static final Random random = new Random();
    public static HashMap<String, HashMap<String, ArrayList<Object>>> combineAllData() {
        //Step 1,2,3

        String courseDataPath = "data/tum_dersler.xlsx";
        String studentDataPath = "data/students_with_courses_v3.xlsx";
        String invigilatorDataPath = "data/invigilators.xlsx";
        String classroomDataPath = "data/Classrooms_v4 (1).xlsx";
        CourseDataParser courseDataParser = new CourseDataParser(courseDataPath);
        PersonDataParser personDataParser = new PersonDataParser(studentDataPath, invigilatorDataPath);
        ClassroomDataParser classroomDataParser = new ClassroomDataParser(classroomDataPath);

        HashMap<String, ArrayList<Object>> courseData = courseDataParser.parseCourseData();
        //logger.debug(courseData);
        logger.debug("Course debugs extracted successfully:)");

        HashMap<String, ArrayList<Object>> studentData = personDataParser.parseStudentData();
        //logger.debug(studentData);
        logger.debug("Student debugs extracted successfully:)");

        HashMap<String, ArrayList<Object>> invigilatorData = personDataParser.parseInvigilatorData();
        //logger.debug(invigilatorData);
        logger.debug("Invigilator debugs extracted successfully:)");

        HashMap<String, ArrayList<Object>> classroomData = classroomDataParser.parseClassroomData();
        //logger.info(classroomData);
        logger.debug("Classroom infos extracted successfully:)");

        HashMap<String, HashMap<String, ArrayList<Object>>> result = new HashMap<>();

        result.put("courseData", courseData);
        result.put("studentData", studentData);
        result.put("invigilatorData", invigilatorData);
        result.put("classroomData", classroomData);

        return result;
    }

    public static ArrayList<Course> generateCourseInstances(HashMap<String, ArrayList<Object>> courseData) {
        // create Course instances
        ArrayList<Course> courses = new ArrayList<>();
        for (HashMap.Entry<String, ArrayList<Object>> entry : courseData.entrySet()) {
            String courseCode = entry.getKey();
            String courseName = (String) entry.getValue().get(0);
            int beforeExamPrepTime = (int) entry.getValue().get(1);
            int examDuration = (int) entry.getValue().get(2);
            int afterExamPrepTime = (int) entry.getValue().get(3);

            double pcExamProbability = Double.parseDouble(ConfigHelper.getProperty("PC_EXAM")); // 30%
            double randomNumber = random.nextDouble();
            boolean isPcExam = randomNumber < pcExamProbability;

            Course course = new Course(courseCode, courseName, isPcExam, beforeExamPrepTime, examDuration, afterExamPrepTime);
            courses.add(course);
        }
        //logger.debug(courses);
        logger.debug("Course instances created successfully:)");
        return courses;
    }

    public static ArrayList<Invigilator> generateInvigilatorInstances(HashMap<String, ArrayList<Object>> invigilatorData) {
        // create Invigilator instances
        ArrayList<Invigilator> invigilators = new ArrayList<>();
        for (HashMap.Entry<String, ArrayList<Object>> entry : invigilatorData.entrySet()) {
            String id = entry.getKey();
            String name = (String) entry.getValue().get(1);
            String surname = (String) entry.getValue().get(0);
            int maxCoursesMonitoredCount = random.nextInt(Integer.parseInt(ConfigHelper.getProperty("MAX_COURSES_MONITORED"))) + 1;

            Invigilator invigilator = new Invigilator(id, name, surname, maxCoursesMonitoredCount);
            invigilators.add(invigilator);
        }
        //logger.debug(invigilators);
        logger.debug("Invigilator instances created successfully:)");
        return invigilators;
    }

    public static ArrayList<Classroom> generateClassroomInstances(HashMap<String, ArrayList<Object>> classroomData) {
        // create Classroom instances
        ArrayList<Classroom> classrooms = new ArrayList<>();
        for (HashMap.Entry<String, ArrayList<Object>> entry : classroomData.entrySet()) {
            String classroomCode = entry.getKey();
            String classroomName = (String) entry.getValue().get(2);
            int classroomCapacity = (int) entry.getValue().get(1);
            String classroomProperties = (String) entry.getValue().get(0);
            double pcLabProbability = Double.parseDouble(ConfigHelper.getProperty("PC_LAB")); // 35%
            double randomNumber = random.nextDouble();
            boolean isPcLab = randomNumber < pcLabProbability;
            Classroom classroom = new Classroom(classroomCode, classroomName, classroomCapacity, isPcLab, classroomProperties);
            classrooms.add(classroom);
        }
        //logger.debug(classrooms);
        logger.debug("Classroom instances created successfully:)");
        return classrooms;
    }

    public static ArrayList<Student> generateStudentInstances(HashMap<String, ArrayList<Object>> studentData) {
        // create Invigilator instances
        ArrayList<Student> students = new ArrayList<>();
        for (HashMap.Entry<String, ArrayList<Object>> entry : studentData.entrySet()) {

            String id = entry.getKey();
            String name = (String) entry.getValue().get(4);
            String surname = (String) entry.getValue().get(3);
            String department = (String) entry.getValue().get(0);
            int year = (int) entry.getValue().get(1);

            String courseData = (String) entry.getValue().get(2);
            String[] splittedCourses = courseData.split(";");
            ArrayList<String> registeredCourses = new ArrayList<>(Arrays.asList(splittedCourses));

            // int maxCoursesTakenCount = random.nextInt(6) + 1;
            // maxCoursesTakenCount between 6 and 12
            int minCoursesTaken = Integer.parseInt(ConfigHelper.getProperty("MIN_COURSES_TAKEN"));
            int maxCoursesTaken = Integer.parseInt(ConfigHelper.getProperty("MAX_COURSES_TAKEN"));
            int coursesTaken = random.nextInt(maxCoursesTaken - (minCoursesTaken - 1)) + (maxCoursesTaken - minCoursesTaken);

            Student student = new Student(id, name, surname, registeredCourses, department, year);
            students.add(student);
        }
        //logger.debug(invigilators);
        logger.debug("Student instances created successfully:)");
        return students;
    }

    public static Schedule generateSchedule(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, int interval) {
        return new Schedule(startDate, endDate, startTime, endTime, interval);
    }


}
