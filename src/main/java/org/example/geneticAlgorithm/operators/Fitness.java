package org.example.geneticAlgorithm.operators;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.*;
import org.example.utils.FileHelper;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@ToString
public class Fitness {
    /*
     * Overall Check of Schedule
     * More than 10 schedules must be generated to test this operator
     * These schedules should have different number of courses
     * Small Schedule with 2 Courses with 20 Student(Some of these student should have more than 1 Exam)
     * Medium Schedule
     * Large Schedule
     * Fitness Function should also work with encoded schedule by
     * decoding schedules or processing them encoded somehow
     * */

    /*
     * Constraint Check and Penalty
     * Hard Constraints
     * All lessons that have registered students must have exams assigned to them
     * No classroom can be assigned to more than one exam at the same moment.
     * No student can be assigned to more than one exam at the same moment.
     * No invigilator can be assigned to more than one exam at the same moment.
     * No exam can be held before or after the defined time frame
     * One lesson must have one exam.
     *
     * Soft Constraints
     * No student should enter more than 2 exams in the same day.
     * If a student has 2 exam in the same day, they should have a 0.5/1 hour between them.
     * If an invigilator has more than one exam in the same day, they should have a 0.5/1 hour between them.
     *
     * Penalty Methods
     * score = (1 / (1 + number_of_constraint_violations))
     * score can be at most 1 if everything is perfect
     *
     * TODO(Deniz) : return also a checklist of constraints
     *
     * TODO(Deniz) : add graphs for fitness function, like their
     *  progress in different generations, or their avarage in one
     *  population, ..
     *
     * TODO(Deniz) : Return true/false array for constraints
     *  or if score is not one == violation
     *
     * */

    private static final Logger logger = LogManager.getLogger(Fitness.class);
    private ArrayList<Course> courses;
    private ArrayList<Student> students;
    private ArrayList<Classroom> classrooms;
    private ArrayList<Invigilator> invigilators;
    private HashMap<String, ArrayList<EncodedExam>> classroomExams = new HashMap<>();
    private HashMap<String, ArrayList<EncodedExam>> invigilatorExams = new HashMap<>();
    private HashMap<String, ArrayList<EncodedExam>> studentExams = new HashMap<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public Fitness(ArrayList<Course> courses, ArrayList<Student> students, ArrayList<Classroom> classrooms, ArrayList<Invigilator> invigilators, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.courses = courses;
        this.students = students;
        this.classrooms = classrooms;
        this.invigilators = invigilators;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public double[] fitnessScore(ArrayList<EncodedExam> encodedExams) {

        prepareDataForFitness(encodedExams);

        double allExamsHaveRequiredTime = (double) 1 / (1 + allExamsHaveRequiredTime(encodedExams));
        double allExamHaveRequiredInvigilatorCount = (double) 1 / (1 + allExamHaveRequiredInvigilatorCount(encodedExams));
        double classroomOverlapped = (double) 1 / (1 + classroomOverlapped());
        double allExamsHaveClassrooms = (double) 1 / (1 + allExamsHaveClassrooms(encodedExams));
        double classroomsHasCapacity = (double) 1 / (1 + classroomsHasCapacity(encodedExams));
        double invigilatorOverlapped = (double) 1 / (1 + invigilatorOverlapped());
        double studentOverlapped = (double) 1 / (1 + studentOverlapped());
        double invigilatorAvailable = (double) 1 / (1 + invigilatorAvailable());
        double startAndEndTimeDateViolated = (double) 1 / (1 + startAndEndTimeDateViolated(encodedExams));
        double allExamsHaveRequiredEquipments = (double) 1 / (1 + allExamsHaveRequiredEquipments(encodedExams));
        double noExamsWeekendAndHolidays = (double) 1 / (1 + noExamsWeekendAndHolidays(encodedExams));
        double examStartAndEndDateSame = (double) 1 / (1 + examStartAndEndDateSame(encodedExams));

        // use f1 score to calculate average, harmonic average
        // we can also use weight/priority based average calculation
        // n = fitness function count
        int n = 12;
        double fitnessScore = n / (
                1 / allExamsHaveRequiredTime +
                        1 / allExamHaveRequiredInvigilatorCount +
                        1 / classroomOverlapped +
                        1 / allExamsHaveClassrooms +
                        1 / classroomsHasCapacity +
                        1 / invigilatorOverlapped +
                        1 / studentOverlapped +
                        1 / invigilatorAvailable +
                        1 / startAndEndTimeDateViolated +
                        1 / allExamsHaveRequiredEquipments +
                        1 / noExamsWeekendAndHolidays +
                        1 / examStartAndEndDateSame
        );
        return new double[]{allExamsHaveRequiredTime, allExamHaveRequiredInvigilatorCount, classroomOverlapped,
                allExamsHaveClassrooms, classroomsHasCapacity, invigilatorOverlapped,
                studentOverlapped, invigilatorAvailable, startAndEndTimeDateViolated,
                allExamsHaveRequiredEquipments, noExamsWeekendAndHolidays, examStartAndEndDateSame,
                fitnessScore};
    }

    private void prepareDataForFitness(ArrayList<EncodedExam> chromosome) {

        // hashmap : classroom code - assigned exams
        HashMap<String, ArrayList<EncodedExam>> classroomExams = new HashMap<>();

        // hashmap : invigilator id - assigned exams
        HashMap<String, ArrayList<EncodedExam>> invigilatorExams = new HashMap<>();

        // hashmap : student id - assigned exams
        HashMap<String, ArrayList<EncodedExam>> studentExams = new HashMap<>();

        for (EncodedExam encodedExam : chromosome) {
            String classroomCode = encodedExam.getClassroomCode();
            generateHashmap(classroomExams, encodedExam, classroomCode);

            String courseCode = encodedExam.getCourseCode();
            Course course = Course.findByCourseCode(courses, courseCode);
            if (course != null) {
                ArrayList<String> studentIds = course.getRegisteredStudents();
                for (String studentId : studentIds) {
                    generateHashmap(studentExams, encodedExam, studentId);
                }
            }

            ArrayList<String> invigilatorIds = encodedExam.getInvigilators();
            for (String invigilatorId : invigilatorIds) {
                generateHashmap(invigilatorExams, encodedExam, invigilatorId);
            }
        }
        this.invigilatorExams = invigilatorExams;
        this.classroomExams = classroomExams;
        this.studentExams = studentExams;
    }

    private void generateHashmap(HashMap<String, ArrayList<EncodedExam>> mappedExams, EncodedExam encodedExam, String code) {
        if (mappedExams.containsKey(code)) {
            ArrayList<EncodedExam> exams = mappedExams.get(code);
            exams.add(encodedExam);
            mappedExams.put(code, exams);
        } else {
            ArrayList<EncodedExam> initialExams = new ArrayList<>();
            initialExams.add(encodedExam);
            mappedExams.put(code, initialExams);
        }
    }

    // Hard Constraints
    public double allExamsHaveRequiredTime(ArrayList<EncodedExam> chromosome) {
        // all exams have the required timeslot for both invigilators and students
        int requiredTimeslotPunishment = 0;
        for (EncodedExam exam : chromosome) {
            String courseCode = exam.getCourseCode();
            Course course = Course.findByCourseCode(courses, courseCode);
            if (course != null) {
                Timeslot timeslots = exam.getTimeSlot();
                int examTimeslotCount = (int) Duration.between(timeslots.getStart(), timeslots.getEnd()).toHours();
                int timeslotCountForInvigilator = course.getBeforeExamPrepTime() + course.getExamDuration() + course.getAfterExamPrepTime();
                int timeslotCountForStudent = course.getExamDuration();

                // all courses have the required timeslot for both invigilators and students
                // punish it proportional to the difference
                int differenceInvigilator = Math.abs(examTimeslotCount - timeslotCountForInvigilator);
                int differenceStudent = Math.abs((examTimeslotCount - (course.getBeforeExamPrepTime() + course.getAfterExamPrepTime())) - timeslotCountForStudent);
                requiredTimeslotPunishment += differenceInvigilator;
                requiredTimeslotPunishment += differenceStudent;
                if (differenceInvigilator != 0) {
                    //logger.info("This is not the required time for invigilator");
                }
                if (differenceStudent != 0) {
                    //logger.info("This is not the required time for student");
                }
            }
        }
        return requiredTimeslotPunishment;
    }

    public double allExamHaveRequiredInvigilatorCount(ArrayList<EncodedExam> chromosome) {
        // all exams have the required number of invigilators to observe the exam
        int invigilatorCountPunishment = 0;
        for (EncodedExam exam : chromosome) {
            String courseCode = exam.getCourseCode();
            Course course = Course.findByCourseCode(courses, courseCode);
            if (course != null) {
                int invigilatorCount = exam.getInvigilators().size();

                // all courses have the required number of invigilators to observe the exam
                // if there are more invigilator than it is supposed to be is that okay ?
                int capacity = course.getRegisteredStudents().size();
                int requiredInvigilator = capacity < 20 ? 1 : capacity < 75 ? 2 : (capacity < 150 ? 3 : 4);
                int difference = Math.abs(requiredInvigilator - invigilatorCount);
                invigilatorCountPunishment += difference;
                if (difference != 0) {
//                    logger.info("The invigilator count is missing :(");
//                    logger.info("Course: " + course);
//                    logger.info("Required invigilator count: " + requiredInvigilator);
//                    logger.info("Current invigilator count: " + invigilatorCount);
                }
            }
        }
        return invigilatorCountPunishment;
    }

    public double classroomOverlapped() {
        // No classroom can be assigned to more than one exam at the same moment.
        double classroomPunishment = 0;
        for (String classroom : classroomExams.keySet()) {
            ArrayList<Timeslot> timeslots = new ArrayList<>();
            ArrayList<EncodedExam> assignedExams = classroomExams.get(classroom);
            // save timeslots of each exam and compare them
            for (EncodedExam exam : assignedExams) {
                Timeslot timeslot = exam.getTimeSlot();
                timeslots.add(timeslot);
            }

            int length = timeslots.size();
            for (int i = 0; i < length; i++) {
                for (int j = i + 1; j < length; j++) {
                    long minutes = timeslots.get(i).getOverlapMinutes(timeslots.get(j));
                    if (minutes != 0) {
//                        logger.info("Timeslots overlap for classroom!!!!!!!!!!");
//                        logger.info("Classroom Code: " + classroom);
//                        logger.info("Exams: " + assignedExams);
//                        logger.info(timeslots.get(i));
//                        logger.info(timeslots.get(j));
//                        logger.info("Overlapped minutes: " + minutes);
                        classroomPunishment += (double) minutes / 60;
                    }
                }
            }
        }

        return classroomPunishment;
    }

    public double studentOverlapped() {
        // No student can be assigned to more than one exam at the same moment.
        double studentOverlappedPunishment = 0;

        // timeslots must be adjusted for student
        // before and after exam time must be removed
        for (String studentId : studentExams.keySet()) {
            ArrayList<EncodedExam> assignedExams = studentExams.get(studentId);
            ArrayList<Timeslot> timeslots = new ArrayList<>();
            for (EncodedExam exam : assignedExams) {
                Course course = Course.findByCourseCode(courses, exam.getCourseCode());
                if (course != null) {
                    int beforeExamPrep = course.getBeforeExamPrepTime();
                    int afterExamPrep = course.getAfterExamPrepTime();
                    Timeslot timeslot = exam.getTimeSlot();
                    timeslots.add(new Timeslot(timeslot.getStart().plusHours(beforeExamPrep), timeslot.getEnd().minusHours(afterExamPrep)));
                }
            }
            int length = timeslots.size();
            for (int i = 0; i < length; i++) {
                for (int j = i + 1; j < length; j++) {
                    long minutes = timeslots.get(i).getOverlapMinutes(timeslots.get(j));
                    if (minutes != 0) {
//                        logger.info("Timeslots overlap for student!!!!!!!!!!");
//                        logger.info("Student Id: " + studentId);
//                        logger.info("Exams: " + assignedExams);
//                        logger.info(timeslots.get(i));
//                        logger.info(timeslots.get(j));
//                        logger.info("Overlapped minutes: " + minutes);
                        studentOverlappedPunishment += (double) minutes / 60;
                    }
                }
            }
        }
        return studentOverlappedPunishment;
    }

    public double invigilatorOverlapped() {
        // No invigilator can be assigned to more than one exam at the same moment.
        double invigilatorOverlappedPunishment = 0;

        for (String invigilatorId : invigilatorExams.keySet()) {
            ArrayList<EncodedExam> assignedExams = invigilatorExams.get(invigilatorId);
            ArrayList<Timeslot> timeslots = new ArrayList<>();
            for (EncodedExam exam : assignedExams) {
                Timeslot timeslot = exam.getTimeSlot();
                timeslots.add(timeslot);
            }

            int length = timeslots.size();
            for (int i = 0; i < length; i++) {
                for (int j = i + 1; j < length; j++) {
                    long minutes = timeslots.get(i).getOverlapMinutes(timeslots.get(j));
                    if (minutes != 0) {
//                        logger.info("Timeslots overlap for invigilator!!!!!!!!!!");
//                        logger.info("Invigilator Id: " + invigilatorId);
//                        logger.info("Exams: " + assignedExams);
//                        logger.info(timeslots.get(i));
//                        logger.info(timeslots.get(j));
//                        logger.info("Overlapped minutes: " + minutes);
                        invigilatorOverlappedPunishment += (double) minutes / 60;
                    }
                }
            }

        }

        return invigilatorOverlappedPunishment;
    }

    public double invigilatorAvailable() {
        // No invigilator can be assigned to more than her/his capacity.
        int invigilatorAvailablePunishment = 0;

        for (String invigilatorId : invigilatorExams.keySet()) {
            Invigilator invigilator = Invigilator.findByInvigilatorId(invigilators, invigilatorId);
            if (invigilator != null) {
                int maxMonitoredExamCount = invigilator.getMaxCoursesMonitoredCount();
                ArrayList<EncodedExam> monitoredExams = invigilatorExams.get(invigilatorId);
                int monitoredExamCount = monitoredExams.size();
                if (maxMonitoredExamCount < monitoredExamCount) {
//                    logger.info("Invigilator is over her/his capacity!!!!!!!!!!");
//                    logger.info("Invigilator Id: " + invigilatorId);
//                    logger.info("Max Capacity:" + maxMonitoredExamCount);
//                    logger.info("Monitored Exam count:" + monitoredExamCount);
//                    logger.info("Exams: " + monitoredExams);
                    invigilatorAvailablePunishment++;
                }
            }
        }

        return invigilatorAvailablePunishment;
    }


    public double allExamsHaveClassrooms(ArrayList<EncodedExam> chromosome) {
        // All exams must have classrooms assigned to them
        int allExamsHaveClassroomsPunishment = 0;
        for (EncodedExam exam : chromosome) {
            if (exam.getClassroomCode() == null) {
                //logger.info("Exam " + exam.getCourseCode() + " has no classroom assigned!!!!!!!");
                allExamsHaveClassroomsPunishment++;
            }
        }

        return allExamsHaveClassroomsPunishment;
    }

    public double classroomsHasCapacity(ArrayList<EncodedExam> chromosome) {
        // classroom has the capacity to hold all the students
        int classroomsHasCapacityPunishment = 0;
        for (EncodedExam exam : chromosome) {
            Classroom classroom = Classroom.findByClassroomCode(classrooms, exam.getClassroomCode());
            Course course = Course.findByCourseCode(courses, exam.getCourseCode());

            if (classroom == null) {
                //logger.error("Classroom with code " + exam.getClassroomCode() + " not found.");
                continue;
            }

            if (course == null) {
                //logger.error("Course with code " + exam.getCourseCode() + " not found.");
                continue;
            }

            if (classroom.getCapacity() < course.getRegisteredStudents().size()) {
                //logger.info("Classroom " + classroom.getClassroomCode() + " does not have the required capacity!!!!!");
                classroomsHasCapacityPunishment++;
            }
        }

        return classroomsHasCapacityPunishment;
    }

    public double startAndEndTimeDateViolated(ArrayList<EncodedExam> chromosome) {
        // No exam can be held before or after the defined time frame
        int startAndEndTimeDatePunishment = 0;
        for (EncodedExam exam : chromosome) {
            Timeslot timeslot = exam.getTimeSlot();
            LocalDateTime start = timeslot.getStart();
            LocalDateTime end = timeslot.getEnd();
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            if (start.isBefore(startDateTime) || start.isAfter(endDateTime) ||
                    end.isBefore(startDateTime) || end.isAfter(endDateTime)) {
//                logger.info("Exam timeslot is not in the specified range!!");
//                logger.info("Start: " + start);
//                logger.info("End: " + end);
                startAndEndTimeDatePunishment++;
            }
        }
        return startAndEndTimeDatePunishment;
    }


    public double allExamsHaveRequiredEquipments(ArrayList<EncodedExam> chromosome) {
        // The classroom has the required equipments(computer) if necessary like
        int allExamsHaveRequiredEquipmentsPunishment = 0;
        for (EncodedExam exam : chromosome) {
            Classroom classroom = Classroom.findByClassroomCode(classrooms, exam.getClassroomCode());
            Course course = Course.findByCourseCode(courses, exam.getCourseCode());
            if (course != null && classroom != null) {
                boolean pcLab = classroom.isPcLab();
                boolean pcRequired = course.isPcExam();
                if (pcLab != pcRequired) {
                    allExamsHaveRequiredEquipmentsPunishment++;
                }
            }
        }
        return allExamsHaveRequiredEquipmentsPunishment;
    }

    public double noExamsWeekendAndHolidays(ArrayList<EncodedExam> chromosome) {
        // No exam at the weekend or holidays
        int noExamsWeekendAndHolidaysPunishment = 0;
        Set<LocalDate> holidays = FileHelper.loadHolidaysFromFile();

        for (EncodedExam exam : chromosome) {
            LocalDateTime startDateTime = exam.getTimeSlot().getStart();
            LocalDate examDate = startDateTime.toLocalDate();
            DayOfWeek dayOfWeek = examDate.getDayOfWeek();

            // Check if the exam is on a weekend
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
//                logger.info("Exam Date: "+ examDate);
//                logger.info("Exam can not be placed in the weekends");
                noExamsWeekendAndHolidaysPunishment++;
            }

            // Check if the exam is on a holiday
            if (holidays.contains(examDate)) {
//                logger.info("Exam Date: "+ examDate);
//                logger.info("Exam can not be placed in the holidays");
                noExamsWeekendAndHolidaysPunishment++;
            }
        }

        return noExamsWeekendAndHolidaysPunishment;
    }

    public double examStartAndEndDateSame(ArrayList<EncodedExam> chromosome) {
        // Exam should start and end at the same day
        int examStartAndEndDateSamePunishment = 0;
        for (EncodedExam exam : chromosome) {
            if (!exam.getTimeSlot().getStart().toLocalDate().isEqual(exam.getTimeSlot().getEnd().toLocalDate())) {
                examStartAndEndDateSamePunishment++;
            }
        }
        return examStartAndEndDateSamePunishment;
    }


    // Soft Constraints
    // No invigilator should monitor her/his max capacity
    // No student should enter more than two exam in one day
    // If student has more than one exam, they should have at least 1 hour between
    // No invigilator should monitor more than three exam in one day


}
