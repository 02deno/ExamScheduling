package org.example.DataPreprocessing;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Models.Classroom;
import org.example.Models.Course;
import org.example.Models.Invigilator;
import org.example.Utils.ArraylistHelper;
import org.example.Utils.HTMLHelper;

import java.nio.channels.ClosedSelectorException;
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
        ArrayList<Course> courses = new ArrayList<Course>();
        for (HashMap.Entry<String, ArrayList<Object>> entry : courseData.entrySet()) {
            String courseCode = entry.getKey();
            String courseName = (String) entry.getValue().get(0);
            int studentCapacity = (int) entry.getValue().get(1);
            int beforeExamPrepTime = (int) entry.getValue().get(2);
            int examDuration = (int) entry.getValue().get(3);
            int afterExamPrepTime = (int) entry.getValue().get(4);

            double pcExamProbability = 0.3; // 30%
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
        ArrayList<Invigilator> invigilators = new ArrayList<Invigilator>();
        for (HashMap.Entry<String, ArrayList<Object>> entry : invigilatorData.entrySet()) {
            String id = entry.getKey();
            String name = (String) entry.getValue().get(1);
            String surname = (String) entry.getValue().get(0);
            int maxCoursesMonitoredCount = random.nextInt(4);

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
        ArrayList<Classroom> classrooms = new ArrayList<Classroom>();
        for (HashMap.Entry<String, ArrayList<Object>> entry : classroomData.entrySet()) {
            String classroomCode = entry.getKey();
            String classroomName = (String) entry.getValue().get(2);
            int classroomCapacity = (int) entry.getValue().get(1);
            String classroomProperties = (String) entry.getValue().get(0);
            double pcLabProbability = 0.35; // 10%
            double randomNumber = random.nextDouble();
            boolean isPcLab = randomNumber < pcLabProbability;
            Classroom classroom = new Classroom(classroomCode, classroomName, classroomCapacity, isPcLab, classroomProperties);
            classrooms.add(classroom);
        }
        //logger.info(invigilators);
        logger.info("Classroom instances created successfully:)");
        logger.info("Number of Classroom: " + classrooms.size());
        return classrooms;
    }

    public static HashMap<String, ArrayList<?>> mapInvigilatorsWithCourses(ArrayList<Course> courses, ArrayList<Invigilator> invigilators) {
        // Step 5

        // if studentCapacity :
        // 0 - 75 : 2 invigilators
        // 75 - 150 : 3 invigilators
        // > 150 : 4 invigilators

        // set course attribute "availableInvigilators"
        // set invigilator attribute "monitoredCourses"

        ArrayList<Integer> studentCapacities = new ArrayList<Integer>();
        for(int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            int capacity = course.getStudentCapacity();
            studentCapacities.add(capacity);
            ArrayList<String> availableInvigilators = course.getAvailableInvigilators();
            int invigilatorCount = capacity < 75 ? 2 : (capacity < 150 ? 3 : 4); // Determine the number of invigilators
            //logger.info("Capacity: " + capacity + " Invigilator Count: " + invigilatorCount);
            int counter = 0;
            while(counter < invigilatorCount) {
                int invigilatorIndex = ArraylistHelper.getRandomElement(invigilators);
                Invigilator invigilator = invigilators.get(invigilatorIndex);
                if(invigilator.isAvailable()) {
                    availableInvigilators.add(invigilator.getID());
                    ArrayList<String> monitoredCourses = invigilator.getMonitoredCourses();
                    monitoredCourses.add(course.getCourseCode());
                    invigilator.setMonitoredCourses(monitoredCourses);
                    counter++;
                    if(invigilator.getMonitoredCourses().size() == invigilator.getMaxCoursesMonitoredCount()) {
                        invigilator.setAvailable(false);
                    }
                    invigilators.set(invigilatorIndex, invigilator);
                }
            }
            course.setAvailableInvigilators(availableInvigilators);
            courses.set(i, course);

        }

        // histogram is made to see distribution and decide invigilator numbers
        HTMLHelper.generateHistogram(studentCapacities, "graphs/studentCapacityHistogram.html", "Student Capacity");

        // return two arraylist
        logger.info("Course instances mapped with invigilators successfully :)");

        logger.info(courses);
        logger.info(invigilators);
        HTMLHelper.generateReport(courses, "graphs/course_report.html", "Course Report",
                new String[]{"Course Code", "Course Name", "Is PC Exam", "Student Capacity", "Available Invigilator IDs"},
                new String[]{"courseCode", "courseName", "isPcExam", "studentCapacity", "availableInvigilators"});

        // Generate invigilator report
        HTMLHelper.generateReport(invigilators, "graphs/invigilator_report.html", "Invigilator Report",
                new String[]{"ID", "Name", "Surname", "Maximum Number of Courses to Monitor", "Monitored Class IDs", "Available"},
                new String[]{"ID", "name", "surname", "maxCoursesMonitoredCount", "monitoredCourses", "isAvailable"});

        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("courses", courses);
        result.put("invigilators", invigilators);
        return result;
    }

    public static void mapStudentsWithCourses(){
        // Step 4
    }

    public static HashMap<String, ArrayList<?>> mapCoursesWithClassrooms(ArrayList<Course> courses, ArrayList<Classroom> classrooms) {
        // Step 6

        ArrayList<Integer> classroomCapacities = new ArrayList<Integer>();
        for(int i = 0; i < classrooms.size(); i++) {
            Classroom classroom = classrooms.get(i);
            int capacity = classroom.getCapacity();
            classroomCapacities.add(capacity);
        }

        HTMLHelper.generateHistogram(classroomCapacities, "graphs/classroomCapacityHistogram.html", "Clasroom Capacity");

        HTMLHelper.generateReport(classrooms, "graphs/classroom_report.html", "Classroom Report",
                new String[]{"Code", "Name", "Capacity(#Studens)", "PC Lab", "Properties", "Available", "Course Code"},
                new String[]{"classroomCode", "classroomName", "capacity", "isPcLab", "classroomProperties", "isAvailable", "courseCode"});

        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("courses", courses);
        result.put("classrooms", classrooms);
        return result;
    }
}
