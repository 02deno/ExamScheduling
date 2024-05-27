package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.*;
import org.example.utils.DataStructureHelper;

import java.time.Duration;
import java.util.*;
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
     * */
    private static final Logger logger = LogManager.getLogger(Initialization.class);

    public static HashMap<String, ArrayList<?>> heuristicMapCoursesWithStudents(ArrayList<Course> courses, ArrayList<Student> students) {
        // Step 1
        // TODO(Deniz) : update this for loop so that it run until students are done not courses
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            ArrayList<String> registeredStudents = course.getRegisteredStudents();
            int remainingStudentCapacity = course.getRemainingStudentCapacity();
            int studentCapacity = course.getStudentCapacity();
            double randomRatio = Math.random();
            while (registeredStudents.size() < studentCapacity * randomRatio) {
                int studentIndex = DataStructureHelper.getRandomElement(students);
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
        }

        logger.info("Course instances mapped with students successfully :)");
        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("courses", courses);
        result.put("students", students);
        return result;
    }

    public static HashMap<String, ArrayList<?>> createExamInstances(ArrayList<Course> courses) {
        // Step 2
        ArrayList<Exam> exams = new ArrayList<>();
        int counter = 1;
        for (Course course : courses) {
            if (!course.getRegisteredStudents().isEmpty()) {
                Exam exam = new Exam(counter, course);
                exams.add(exam);
                counter++;
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
        // 0 - 19 : 1 invigilator
        // 20 - 74 : 2 invigilators
        // 75 - 149 : 3 invigilators
        // > 149 : 4 invigilators

        // set course attribute "availableInvigilators"
        // set invigilator attribute "monitoredCourses"
        Random rand = new Random();
        Collections.shuffle(exams, new Random(rand.nextInt(10000)));
        for (Exam exam : exams) {
            Course course = exam.getCourse();
            int capacity = course.getRegisteredStudents().size();
            ArrayList<String> availableInvigilators = new ArrayList<>();
            int invigilatorCount = capacity < 20 ? 1 : capacity < 75 ? 2 : (capacity < 150 ? 3 : 4); // Determine the number of invigilators
            int counter = 0;
            while (counter < invigilatorCount) {
                ArrayList<Invigilator> filteredInvigilators = invigilators.stream()
                        .filter(Invigilator::isAvailable)
                        .collect(Collectors.toCollection(ArrayList::new));
                //logger.info("Invigilators available: " + course.getCourseName() + ": "+filteredInvigilators.size());
                if (!filteredInvigilators.isEmpty()) {
                    int invigilatorIndex = DataStructureHelper.getRandomElement(filteredInvigilators);
                    Invigilator invigilator = filteredInvigilators.get(invigilatorIndex);
                    availableInvigilators.add(invigilator.getID());

                    ArrayList<String> monitoredCourses = invigilator.getMonitoredCourses();
                    monitoredCourses.add(course.getCourseCode());
                    invigilator.setMonitoredCourses(monitoredCourses);

                    ArrayList<Integer> monitoredExams = invigilator.getMonitoredExams();
                    monitoredExams.add(exam.getExamCode());
                    invigilator.setMonitoredExams(monitoredExams);

                    if (invigilator.getMonitoredCourses().size() == invigilator.getMaxCoursesMonitoredCount()) {
                        invigilator.setAvailable(false);
                    }
                    invigilators.set(invigilatorIndex, invigilator);
                    counter++;
                } else {
                    break;
                }
            }
            exam.setExamInvigilators(availableInvigilators);

        }
        logger.info("Exam instances mapped with courses and invigilators successfully :)");

        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("exams", exams);
        result.put("invigilators", invigilators);
        return result;
    }


    public static HashMap<String, ArrayList<?>> heuristicMapExamsWithClassrooms(ArrayList<Exam> exams, ArrayList<Classroom> classrooms) {

        Random rand = new Random();
        Collections.shuffle(exams, new Random(rand.nextInt(10000)));

        int assignedCourses = 0;

        for (Exam exam : exams) {
            int capacity = exam.getCourse().getRegisteredStudents().size();
            boolean isPcExam = exam.getCourse().isPcExam();

            ArrayList<Classroom> filteredClassrooms = classrooms.stream()
                    .filter(classroom -> classroom.getCapacity() >= capacity)
                    .filter(classroom -> classroom.isPcLab() == isPcExam)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (!filteredClassrooms.isEmpty()) {
                int classroomIndex = DataStructureHelper.getRandomElement(filteredClassrooms);
                Classroom classroom = filteredClassrooms.get(classroomIndex);

                ArrayList<String> courseCodes = new ArrayList<>(classroom.getCourseCodes());
                courseCodes.add(exam.getCourse().getCourseCode());
                classroom.setCourseCodes(courseCodes);
                ArrayList<Integer> placedExams = new ArrayList<>(classroom.getPlacedExams());
                placedExams.add(exam.getExamCode());
                classroom.setPlacedExams(placedExams);
                Classroom.updateClassroom(classrooms, classroom);

                exam.setClassroom(classroom);
                assignedCourses++;

            } else {
                logger.info("Could not find a classroom for course " + exam.getCourse().getCourseCode() + " with exam capacity " + capacity);
            }
        }

        logger.info("Assigned Courses: " + assignedCourses);
        logger.info("Course instances mapped with classrooms successfully :)");

        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("exams", exams);
        result.put("classrooms", classrooms);
        return result;
    }

    public static HashMap<String, ArrayList<?>> heuristicMapExamsWithTimeslots(ArrayList<Exam> exams, ArrayList<Timeslot> timeslots) {
        // Step 5
        Random rand = new Random();
        Collections.shuffle(exams, new Random(rand.nextInt(10000)));
        int interval = (int) Duration.between(timeslots.get(0).getStart(), timeslots.get(0).getEnd()).toMinutes();
        for (Exam exam : exams) {
            boolean found = false;
            ArrayList<Timeslot> assignedTimeslots = new ArrayList<>();
            Course course = exam.getCourse();
            int requiredTimeslotCount = (course.getBeforeExamPrepTime() + course.getExamDuration() + course.getAfterExamPrepTime()) * 60 / interval;
            int timeslotStartIndex = 0;
            while (!found) {
                timeslotStartIndex = DataStructureHelper.getRandomElement(timeslots);
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
            exam.setExamTimeslot(new Timeslot(assignedTimeslots.get(course.getBeforeExamPrepTime() * 60 / interval).getStart(), assignedTimeslots.get(assignedTimeslots.size() - 1 - course.getAfterExamPrepTime() * 60 / interval).getEnd()));
        }

        exams.sort(Comparator.comparing(exam -> exam.getExamTimeslot().getStart()));
        HashMap<String, ArrayList<?>> result = new HashMap<>();
        result.put("exams", exams);
        return result;
    }
}
