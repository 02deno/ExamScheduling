package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.*;
import org.example.utils.ArraylistHelper;
import org.example.utils.HTMLHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class Initialization {

    /*
     * Step 1 : Randomly assign 2 or 3 invigilators to the courses
     *
     * Step 2 : Map courses with classrooms
     *
     * Step 3 : Map students with courses in range(1,maxCoursesTakenCount)
     * remove these courses from coursesWithNoProfessors list and continue
     * this step until there is no Course left in this list
     *
     * Step 4 : Map courses with timeslots
     *
     * TODO(Deniz): Define an abstract class for Initialization
     *  and make HeuristicInitialization, RandomInitialization and
     *  HybridInitialization inherit that.
     *
     * TODO(Deniz) : Add exam time slot
     * */
    private static final Logger logger = LogManager.getLogger(Initialization.class);

    public static HashMap<String, ArrayList<?>> heuristicMapCoursesWithStudents(ArrayList<Course> courses, ArrayList<Student> students) {
        // Step 1
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            ArrayList<String> registeredStudents = course.getRegisteredStudents();
            int remainingStudentCapacity = course.getRemainingStudentCapacity();
            int studentCapacity = course.getStudentCapacity();
            //logger.info("Student Capacity: " + studentCapacity);
            while (remainingStudentCapacity > studentCapacity / 2 && studentCapacity < 100) {
                int studentIndex = ArraylistHelper.getRandomElement(students);
                Student student = students.get(studentIndex);
                ArrayList<String> registeredCourses = student.getRegisteredCourses();
                int remainingCourseCapacity = student.getRemainingCourseCapacity();
                if (remainingCourseCapacity != 0) {
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
            // logger.info(course.getRegisteredStudents());
            // logger.info("Assigned students to the course");
        }


        HTMLHelper.generateStudentReport(students, "graphs/students_report.html", "Assigned Students Report");
        HTMLHelper.generateCourseReport(courses, "graphs/courses_report.html", "Assigned Courses Report");
        logger.info("Course instances mapped with students successfully :)");
        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("courses", courses);
        result.put("students", students);
        return result;
    }

    public static HashMap<String, ArrayList<?>> createExamInstances(ArrayList<Course> courses) {
        // Step 2
        ArrayList<Exam> exams = new ArrayList<>();
        for (Course course : courses) {
            if (!course.getRegisteredStudents().isEmpty()) {
                Exam exam = new Exam(UUID.randomUUID(), course);
                exams.add(exam);
            }
        }

        logger.info("Exam instances are created!!");
        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("exams", exams);
        return result;
    }


    public static HashMap<String, ArrayList<?>> heuristicMapExamsWithInvigilators(ArrayList<Exam> exams, ArrayList<Invigilator> invigilators) {
        // Step 3

        // if studentCapacity :
        // 0 - 75 : 2 invigilators
        // 75 - 150 : 3 invigilators
        // > 150 : 4 invigilators

        // set course attribute "availableInvigilators"
        // set invigilator attribute "monitoredCourses"

        ArrayList<Integer> studentCapacities = new ArrayList<>();
        logger.info(exams.size());
        for (Exam exam : exams) {
            Course course = exam.getCourse();
            int capacity = course.getRegisteredStudents().size();
            studentCapacities.add(capacity);
            ArrayList<String> availableInvigilators = exam.getExamInvigilators();
            int invigilatorCount = capacity < 20 ? 1 : capacity < 75 ? 2 : (capacity < 150 ? 3 : 4); // Determine the number of invigilators
            //logger.info("Capacity: " + capacity + " Invigilator Count: " + invigilatorCount);
            int counter = 0;
            while (counter < invigilatorCount) {
                int invigilatorIndex = ArraylistHelper.getRandomElement(invigilators);
                Invigilator invigilator = invigilators.get(invigilatorIndex);
                if (invigilator.isAvailable()) {
                    availableInvigilators.add(invigilator.getID());

                    ArrayList<String> monitoredCourses = invigilator.getMonitoredCourses();
                    monitoredCourses.add(course.getCourseCode());
                    invigilator.setMonitoredCourses(monitoredCourses);

                    ArrayList<UUID> monitoredExams = invigilator.getMonitoredExams();
                    monitoredExams.add(exam.getExamCode());
                    invigilator.setMonitoredExams(monitoredExams);

                    counter++;
                    if (invigilator.getMonitoredCourses().size() == invigilator.getMaxCoursesMonitoredCount()) {
                        invigilator.setAvailable(false);
                    }
                    invigilators.set(invigilatorIndex, invigilator);
                }
            }
            //logger.info("Invigilator are assigned to exam " + course.getCourseName());
            exam.setExamInvigilators(availableInvigilators);

        }

        // histogram is made to see distribution and decide invigilator numbers
        HTMLHelper.generateHistogram(studentCapacities, "graphs/studentCapacityHistogram.html", "Student Capacity");

        logger.info("Exam instances mapped with courses and invigilators successfully :)");
        HTMLHelper.generateInvigilatorReport(invigilators, "graphs/invigilator_report.html", "Invigilator Report");

        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("exams", exams);
        result.put("invigilators", invigilators);
        return result;
    }


    public static HashMap<String, ArrayList<?>> heuristicMapExamsWithClassrooms(ArrayList<Exam> exams, ArrayList<Classroom> classrooms) {
        // Step 2

        ArrayList<Integer> classroomCapacities = new ArrayList<>();
        for (Classroom classroom : classrooms) {
            int capacity = classroom.getCapacity();
            classroomCapacities.add(capacity);
        }

        int assignedCourses = 0;
        for (Exam exam : exams) {
            int capacity = exam.getCourse().getRegisteredStudents().size();
            boolean isPcExam = exam.getCourse().isPcExam();
            ArrayList<Classroom> filteredClassrooms = classrooms.stream()
                    .filter(classroom -> classroom.getCapacity() >= capacity)
                    .filter(classroom -> classroom.isPcLab() == isPcExam)
                    .collect(Collectors.toCollection(ArrayList::new));
            if (!filteredClassrooms.isEmpty()) {
                int classroomIndex = ArraylistHelper.getRandomElement(filteredClassrooms);
                Classroom classroom = filteredClassrooms.get(classroomIndex);

                ArrayList<String> courseCodes = classroom.getCourseCodes();
                courseCodes.add(exam.getCourse().getCourseCode());
                classroom.setCourseCodes(courseCodes);

                ArrayList<UUID> placedExams = classroom.getPlacedExams();
                placedExams.add(exam.getExamCode());
                classroom.setPlacedExams(placedExams);

                Classroom.updateClassroom(classrooms, classroom);
                exam.setClassroom(classroom);
                assignedCourses++;
                //logger.info("Found a classroom for course " + exam.getCourse().getCourseName() + " with exam capacity " + capacity);
            } else {
                logger.info("Could not find a classroom for course " + exam.getCourse().getCourseName() + " with exam capacity " + capacity);
            }
        }
        logger.info("Assigned Courses: "+ assignedCourses);
        logger.info("Course instances mapped with classrooms successfully :)");
        HTMLHelper.generateHistogram(classroomCapacities, "graphs/classroomCapacityHistogram.html", "Classroom Capacity");

        HTMLHelper.generateClassroomReport(classrooms, "graphs/classroom_report.html", "Classroom Report");

        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("exams", exams);
        result.put("classrooms", classrooms);
        return result;
    }


    public static HashMap<String, ArrayList<?>> heuristicMapExamsWithTimeslots(ArrayList<Exam> exams, ArrayList<Timeslot> timeslots) {
        // Step 4
        ArrayList<Integer> timeslotCounts = new ArrayList<>();
        for (Exam exam : exams) {
            boolean found = false;
            ArrayList<Timeslot> assignedTimeslots = new ArrayList<>();
            Course course = exam.getCourse();
            int requiredTimeslotCount = course.getBeforeExamPrepTime() + course.getExamDuration() + course.getAfterExamPrepTime();
            timeslotCounts.add(requiredTimeslotCount);
            int timeslotStartIndex = 0;
            while (!found) {
                timeslotStartIndex = ArraylistHelper.getRandomElement(timeslots);
                if (timeslotStartIndex - requiredTimeslotCount >= 0) {
                    timeslotStartIndex = timeslotStartIndex - requiredTimeslotCount;
                }
                boolean sameDay = Timeslot.checkSameDay(timeslots.get(timeslotStartIndex), timeslots.get(timeslotStartIndex + requiredTimeslotCount - 1));
                if (sameDay) {
                    found = true;
                } else {
                    //logger.info("Timeslots are not on the same day. Trying to find another random value...");
                }
            }
            assignedTimeslots.add(timeslots.get(timeslotStartIndex));
            for (int k = 1; k < requiredTimeslotCount; k++) {
                assignedTimeslots.add(timeslots.get(timeslotStartIndex + k));
            }
            exam.setTimeslots(assignedTimeslots);
            exam.setCombinedTimeslot(new Timeslot(assignedTimeslots.get(0).getStart(), assignedTimeslots.get(assignedTimeslots.size() - 1).getEnd()));
            exam.setExamTimeslot(new Timeslot(assignedTimeslots.get(course.getBeforeExamPrepTime()).getStart(), assignedTimeslots.get(assignedTimeslots.size() - 1 - course.getAfterExamPrepTime()).getEnd()));
        }

        exams.sort(Comparator.comparing(a -> a.getExamTimeslot().getStart()));
        HTMLHelper.generateHistogram(timeslotCounts, "graphs/requiredTimeslotHistogram.html", "Timeslot histogram");
        HTMLHelper.generateExamReport(exams, "graphs/exams.html", "Exam Schedule");
        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("exams", exams);
        return result;
    }
}
