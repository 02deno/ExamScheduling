package org.example.dataPreprocessing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.*;
import org.example.utils.ConfigHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    * Step 5 : Randomly assign 2 or 3 invigilators to the courses
    *
    * Step 5 : Map courses with classrooms and generate exams
    *
    * Step 6 : Map students with courses in range(1,maxCoursesTakenCount)
    * remove these courses from coursesWithNoProfessors list and continue
    * this step until there is no Course left in this list
    *
    * Step 7 : Generate Exam Instances by
    *
    * This process of data generating can also be done by an Excel file
    * */

    private static final Logger logger = LogManager.getLogger(RandomDataGenerator.class);
    private static final Random random = new Random();
    public static HashMap<String, HashMap<String, ArrayList<Object>>> combineAllData() {
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
        logger.info("Course infos extracted successfully:)");

        HashMap<String, ArrayList<Object>> studentData = personDataParser.parseStudentData();
        //logger.info(studentData);
        logger.info("Student infos extracted successfully:)");

        HashMap<String, ArrayList<Object>> invigilatorData = personDataParser.parseInvigilatorData();
        //logger.info(invigilatorData);
        logger.info("Invigilator infos extracted successfully:)");

        HashMap<String, ArrayList<Object>> classroomData = classroomDataParser.parseClassroomData();
        //logger.info(classroomData);
        logger.info("Classroom infos extracted successfully:)");

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
            int studentCapacity = (int) entry.getValue().get(1);
            int beforeExamPrepTime = (int) entry.getValue().get(2);
            int examDuration = (int) entry.getValue().get(3);
            int afterExamPrepTime = (int) entry.getValue().get(4);

            double pcExamProbability = Double.parseDouble(ConfigHelper.getProperty("PC_EXAM")); // 30%
            double randomNumber = random.nextDouble();
            boolean isPcExam = randomNumber < pcExamProbability;

            Course course = new Course(courseCode, courseName, isPcExam, studentCapacity, beforeExamPrepTime, examDuration, afterExamPrepTime);
            courses.add(course);
        }
        //logger.info(courses);
        logger.info("Number of courses: " + courses.size());
        logger.info("Course instances created successfully:)");
        return courses;
    }

    public static ArrayList<Invigilator> generateInvigilatorInstances(HashMap<String, ArrayList<Object>> invigilatorData) {
        // create Invigilator instances
        ArrayList<Invigilator> invigilators = new ArrayList<>();
        for (HashMap.Entry<String, ArrayList<Object>> entry : invigilatorData.entrySet()) {
            String id = entry.getKey();
            String name = (String) entry.getValue().get(1);
            String surname = (String) entry.getValue().get(0);
            int maxCoursesMonitoredCount = random.nextInt(Integer.parseInt(ConfigHelper.getProperty("MAX_COURSES_MONITORED")));

            Invigilator invigilator = new Invigilator(id, name, surname, maxCoursesMonitoredCount);
            invigilators.add(invigilator);
        }
        //logger.info(invigilators);
        logger.info("Invigilator instances created successfully:)");
        logger.info("Number of invigilators: " + invigilators.size());
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
        //logger.info(classrooms);
        logger.info("Classroom instances created successfully:)");
        logger.info("Number of Classroom: " + classrooms.size());
        return classrooms;
    }

    public static ArrayList<Student> generateStudentInstances(HashMap<String, ArrayList<Object>> studentData) {
        // create Invigilator instances
        ArrayList<Student> students = new ArrayList<>();
        for (HashMap.Entry<String, ArrayList<Object>> entry : studentData.entrySet()) {
            String id = entry.getKey();
            String name = (String) entry.getValue().get(1);
            String surname = (String) entry.getValue().get(0);
            //int maxCoursesTakenCount = random.nextInt(6) + 1;
            int maxCoursesTakenCount = Integer.parseInt(ConfigHelper.getProperty("MAX_COURSES_TAKEN"));

            Student student = new Student(id, name, surname, maxCoursesTakenCount);
            students.add(student);
        }
        //logger.info(invigilators);
        logger.info("Student instances created successfully:)");
        logger.info("Number of Students: " + students.size());
        return students;
    }

    public static Schedule generateSchedule(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime startTime, LocalDateTime endTime){

        Schedule schedule = new Schedule(startDate, endDate, startTime, endTime);
        System.out.println("Max Time Slots: " + schedule.calculateMaxTimeSlots());
        // ArrayList<Timeslot> timeSlots = schedule.calculateTimeSlots();
//        for(Timeslot timeslot : timeSlots){
//            logger.info("Timeslot: " + timeslot);
//        }
        // logger.info(timeSlots.size());
        return schedule;
    }


}
