package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Classroom;
import org.example.models.Course;
import org.example.models.Invigilator;
import org.example.models.Student;
import org.example.utils.ArraylistHelper;
import org.example.utils.HTMLHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Initialization {
    private static final Logger logger = LogManager.getLogger(Initialization.class);
    public static HashMap<String, ArrayList<?>> heuristicMapInvigilatorsWithCourses(ArrayList<Course> courses, ArrayList<Invigilator> invigilators) {
        // Step 4

        // if studentCapacity :
        // 0 - 75 : 2 invigilators
        // 75 - 150 : 3 invigilators
        // > 150 : 4 invigilators

        // set course attribute "availableInvigilators"
        // set invigilator attribute "monitoredCourses"

        ArrayList<Integer> studentCapacities = new ArrayList<>();
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

        logger.info("Course instances mapped with invigilators successfully :)");

        //logger.info(courses);
        //logger.info(invigilators);
        //HTMLHelper.generateCourseReport(courses, "graphs/course_report.html", "Course Report");
        HTMLHelper.generateInvigilatorReport(invigilators, "graphs/invigilator_report.html", "Invigilator Report");

        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("courses", courses);
        result.put("invigilators", invigilators);
        return result;
    }


    public static HashMap<String, ArrayList<?>> heuristicMapCoursesWithClassrooms(ArrayList<Course> courses, ArrayList<Classroom> classrooms) {
        // Step 5

        ArrayList<Integer> classroomCapacities = new ArrayList<>();
        for (Classroom classroom : classrooms) {
            int capacity = classroom.getCapacity();
            classroomCapacities.add(capacity);
        }
        int assignedCourses = 0;
        for(int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            int capacity = course.getStudentCapacity();
            boolean isPcExam = course.isPcExam();
            ArrayList<Classroom> filteredClassrooms = classrooms.stream()
                    .filter(classroom -> classroom.getCapacity() >= capacity)
                    .filter(classroom -> classroom.isPcLab() == isPcExam)
                    .filter(Classroom::isAvailable)
                    .collect(Collectors.toCollection(ArrayList::new));
            if (!filteredClassrooms.isEmpty()) {
                int classroomIndex = ArraylistHelper.getRandomElement(filteredClassrooms);
                Classroom classroom = filteredClassrooms.get(classroomIndex);
                classroom.setAvailable(false);
                classroom.setCourseCode(course.getCourseCode());
                course.setClassroomCode(classroom.getClassroomCode());
                courses.set(i, course);
                Classroom.updateClassroom(classrooms, classroom);
                assignedCourses++;
            }
        }
        logger.info("Assigned Courses: "+ assignedCourses);
        logger.info("Course instances mapped with classrooms successfully :)");
        HTMLHelper.generateHistogram(classroomCapacities, "graphs/classroomCapacityHistogram.html", "Clasroom Capacity");

        HTMLHelper.generateClassroomReport(classrooms, "graphs/classroom_report.html", "Classroom Report");
        //HTMLHelper.generateCourseReport(courses, "graphs/assigned_course_report.html", "Assigned Course Report");

        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("courses", courses);
        result.put("classrooms", classrooms);
        return result;
    }

    public static HashMap<String, ArrayList<?>> heuristicMapStudentsWithCourses(ArrayList<Course> courses, ArrayList<Student> students){
        // Step 6
        for(int i = 0; i < courses.size() ; i++){
            Course course = courses.get(i);
            ArrayList<String> registeredStudents = course.getRegisteredStudents();
            int remainingStudentCapacity = course.getRemainingStudentCapacity();
            int studentCapacity = course.getStudentCapacity();
            //logger.info("Student Capacity: " + studentCapacity);
            while(remainingStudentCapacity != 0 && studentCapacity < 100) {
                int studentIndex = ArraylistHelper.getRandomElement(students);
                Student student = students.get(studentIndex);
                ArrayList<String> registeredCourses = student.getRegisteredCourses();
                int remainingCourseCapacity = student.getRemainingCourseCapacity();
                if(remainingCourseCapacity != 0 ) {
                    registeredCourses.add(course.getCourseCode());
                    remainingCourseCapacity--;
                    student.setRegisteredCourses(registeredCourses);
                    student.setRemainingCourseCapacity(remainingCourseCapacity);
                    students.set(studentIndex, student);

                    registeredStudents.add(student.getID());
                    remainingStudentCapacity--;
                    course.setRemainingStudentCapacity(remainingStudentCapacity);
                    course.setRegisteredStudents(registeredStudents);
                }
            }
            Course.updateCourse(courses, course);
            //logger.info(course.getRegisteredStudents());
            //logger.info("Assigned students to the course");
        }


        HTMLHelper.generateStudentReport(students, "graphs/assigned_students_report.html", "Assigned Students Report");
        HTMLHelper.generateCourseReport(courses, "graphs/assigned_courses_report.html", "Assigned Courses Report");
        logger.info("Course instances mapped with students successfully :)");
        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("courses", courses);
        result.put("students", students);
        return result;
    }
}
