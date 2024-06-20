package org.example.geneticAlgorithm.operators;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.*;
import org.example.utils.ConfigHelper;
import org.example.utils.FileHelper;

import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
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
     *
     *
     * TODO(Deniz) : Add weights to constraints by calculating
     *  fitness scores
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
    private final double hardWeight = Double.parseDouble(ConfigHelper.getProperty("HARD_CONSTRAINT_WEIGHT"));
    private final double softWeight = Double.parseDouble(ConfigHelper.getProperty("SOFT_CONSTRAINT_WEIGHT"));

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

    public double[][] fitnessScore(Chromosome chromosome) {

        ArrayList<EncodedExam> encodedExams = chromosome.getEncodedExams();
        prepareDataForFitness(encodedExams);

        double[] hardConstraintScores = hardConstraintScores(chromosome);
        double[] softConstraintScores = softConstraintScores(chromosome);
        double fitnessScore = hardWeight * hardConstraintScores[hardConstraintScores.length - 1] +
                softWeight * softConstraintScores[softConstraintScores.length - 1];

        return new double[][]{hardConstraintScores, softConstraintScores, new double[]{chromosome.getChromosomeId(), fitnessScore}};
    }

    public static ArrayList<Chromosome> fitnessShare(ArrayList<Chromosome> population) {
        // compare each chromosom with each other
        // calculate similarity or diversity
        // reduce fitness score when similarity is high
        // or increase fintess score when diversity is high
        // hamming distance : range [0,1]
        // new fitness values = weight1*actual fitness + weight2*diversity
        double sharingThreshold = (double) population.get(0).getEncodedExams().size() / 2; // how far apart chromosomes should be
        double shapeParameter = 1;

        for (int i = 0; i < population.size(); i++) {
            Chromosome chromosome = population.get(i);
            double actualFitness = chromosome.getFitnessScore();
            double distanceSum = 0; // we want this to be max for diversity

            for (int j = 0; j < population.size(); j++) {
                if (i == j) {
                    continue;
                }
                Chromosome anotherChromosome = population.get(j);
                double hammingDistance = hammingDistance(chromosome, anotherChromosome);
                // hamming - max : encodedExam.size, min : 0

                double distance = Math.pow(hammingDistance / chromosome.getEncodedExams().size(), shapeParameter);
                distanceSum += distance;


            }

            // update fitness
            if (distanceSum != 0) {
                chromosome.setFitnessScore(0.9 * actualFitness + 0.1 * (distanceSum / (population.size() - 1)));
            }
        }


        return population;
    }

    public double[] softConstraintScores(Chromosome chromosome) {

        ArrayList<EncodedExam> encodedExams = chromosome.getEncodedExams();
        double studentMoreThanTwoExamSameDay = (double) 1 / (1 + studentMoreThanTwoExamSameDay());
        double minimumGapBetweenExamsStudent = (double) 1 / (1 + minimumGapBetweenExamsStudent());
        double invigilatorMoreThanThreeExamSameDay = (double) 1 / (1 + invigilatorMoreThanThreeExamSameDay());
        double minimumGapBetweenExamsInvigilator = (double) 1 / (1 + minimumGapBetweenExamsInvigilator());
        double noExamsAtWeekends = (double) 1 / (1 + noExamsAtWeekends(encodedExams));
        double examsNotInAfternoon = (double) 1 / (1 + examsNotInAfternoon());
        double popularExamsAtBeginning = (double) 1 / (1 + popularExamsAtBeginning(encodedExams));

        int n = 7;
        double fitnessScore = n / (
                1 / studentMoreThanTwoExamSameDay +
                        1 / minimumGapBetweenExamsStudent +
                        1 / invigilatorMoreThanThreeExamSameDay +
                        1 / minimumGapBetweenExamsInvigilator +
                        1 / noExamsAtWeekends +
                        1 / examsNotInAfternoon +
                        1 / popularExamsAtBeginning

        );

        return new double[]{chromosome.getChromosomeId(), studentMoreThanTwoExamSameDay, minimumGapBetweenExamsStudent, invigilatorMoreThanThreeExamSameDay,
                minimumGapBetweenExamsInvigilator, noExamsAtWeekends, examsNotInAfternoon, popularExamsAtBeginning,
                fitnessScore};
    }

    public static double hammingDistance(Chromosome chromosome1, Chromosome chromosome2) {
        int distance = 0;
        ArrayList<EncodedExam> encodedExams1 = chromosome1.getEncodedExams();
        ArrayList<EncodedExam> encodedExams2 = chromosome2.getEncodedExams();
        for (int i = 0; i < encodedExams1.size(); i++) {
            EncodedExam exam = encodedExams1.get(i);
            String course = exam.getCourseCode();
            String classrooom = exam.getClassroomCode();
            ArrayList<String> invigilators = exam.getInvigilators();
            Timeslot timeslot = exam.getTimeSlot();

            for (EncodedExam exam2 : encodedExams2) {
                if (exam2.getCourseCode().equals(course)) {
                    if (classrooom.equals(exam2.getClassroomCode()) &&
                            !timeslot.getStart().isEqual(exam2.getTimeSlot().getStart()) &&
                            !timeslot.getEnd().isEqual(exam2.getTimeSlot().getEnd()) &&
                            !invigilators.containsAll(exam2.getInvigilators()) &&
                            !exam2.getInvigilators().containsAll(invigilators)) {

                        distance++;
                    }
                }
            }
        }
        return distance;
    }

    public double[] hardConstraintScores(Chromosome chromosome) {

        ArrayList<EncodedExam> encodedExams = chromosome.getEncodedExams();
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
        double noExamsHolidays = (double) 1 / (1 + noExamsInHolidays(encodedExams));
        double examStartAndEndDateSame = (double) 1 / (1 + examStartAndEndDateSame(encodedExams));

        // use f1 score to calculate average, harmonic average
        // we can also use weight/priority based average calculation
        // n = fitness function count
        int n = 11;
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
                        1 / noExamsHolidays +
                        1 / examStartAndEndDateSame
        );
        return new double[]{chromosome.getChromosomeId(), allExamsHaveRequiredTime, allExamHaveRequiredInvigilatorCount, classroomOverlapped,
                allExamsHaveClassrooms, classroomsHasCapacity, invigilatorOverlapped,
                studentOverlapped, invigilatorAvailable, startAndEndTimeDateViolated,
                allExamsHaveRequiredEquipments, noExamsHolidays, examStartAndEndDateSame,
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
                    logger.debug("This is not the required time for invigilator");
                }
                if (differenceStudent != 0) {
                    logger.debug("This is not the required time for student");
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
                    logger.debug("The invigilator count is missing :(");
                    logger.debug("Course: " + course);
                    logger.debug("Required invigilator count: " + requiredInvigilator);
                    logger.debug("Current invigilator count: " + invigilatorCount);
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

            classroomPunishment = getOverlappedPunishment(classroomPunishment, timeslots);
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
            studentOverlappedPunishment = getOverlappedPunishment(studentOverlappedPunishment, timeslots);
        }
        return studentOverlappedPunishment;
    }

    private double getOverlappedPunishment(double overlappedPunishment, ArrayList<Timeslot> timeslots) {
        int length = timeslots.size();
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                long minutes = timeslots.get(i).getOverlapMinutes(timeslots.get(j));
                if (minutes != 0) {
                    logger.debug("Timeslots overlap !!!!!!!!!!");
                    logger.debug(timeslots.get(i));
                    logger.debug(timeslots.get(j));
                    logger.debug("Overlapped minutes: " + minutes);
                    //overlappedPunishment += (double) minutes / 60;
                    overlappedPunishment += 1;
                }
            }
        }
        return overlappedPunishment;
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

            invigilatorOverlappedPunishment = getOverlappedPunishment(invigilatorOverlappedPunishment, timeslots);

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
                    logger.debug("Invigilator is over her/his capacity!!!!!!!!!!");
                    logger.debug("Invigilator Id: " + invigilatorId);
                    logger.debug("Max Capacity:" + maxMonitoredExamCount);
                    logger.debug("Monitored Exam count:" + monitoredExamCount);
                    logger.debug("Exams: " + monitoredExams);
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
                logger.debug("Exam " + exam.getCourseCode() + " has no classroom assigned!!!!!!!");
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
                logger.error("Classroom with code " + exam.getClassroomCode() + " not found.");
                continue;
            }

            if (course == null) {
                logger.error("Course with code " + exam.getCourseCode() + " not found.");
                continue;
            }

            if (classroom.getCapacity() < course.getRegisteredStudents().size()) {
                logger.debug("Classroom " + classroom.getClassroomCode() + " does not have the required capacity!!!!!");
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
                logger.debug("Exam timeslot is not in the specified range!!");
                logger.debug("Start: " + start);
                logger.debug("End: " + end);
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

    public double noExamsInHolidays(ArrayList<EncodedExam> chromosome) {
        // No exam at the weekend or holidays
        int noExamsHolidaysPunishment = 0;
        Set<LocalDate> holidays = FileHelper.loadHolidaysFromFile();

        for (EncodedExam exam : chromosome) {
            LocalDateTime startDateTime = exam.getTimeSlot().getStart();
            LocalDate examDate = startDateTime.toLocalDate();

            // Check if the exam is on a holiday
            if (holidays.contains(examDate)) {
                logger.debug("Exam Date: " + examDate);
                logger.debug("Exam can not be placed in the holidays");
                noExamsHolidaysPunishment++;
            }
        }

        return noExamsHolidaysPunishment;
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
    public double studentMoreThanTwoExamSameDay() {
        // No student should enter more than two exam in one day
        int studentMoreThanTwoExamSameDayPunishment = 0;

        for (String studentId : studentExams.keySet()) {
            // new hashmap : date - exam count
            HashMap<LocalDate, Integer> examCountPerDay = new HashMap<>();
            ArrayList<EncodedExam> assignedExams = studentExams.get(studentId);

            for (EncodedExam exam : assignedExams) {
                Course course = Course.findByCourseCode(courses, exam.getCourseCode());
                if (course != null) {
                    Timeslot timeslot = exam.getTimeSlot();
                    LocalDate examDay = timeslot.getStart().toLocalDate();

                    if (examCountPerDay.containsKey(examDay)) {
                        examCountPerDay.put(examDay, examCountPerDay.get(examDay) + 1);
                    } else {
                        examCountPerDay.put(examDay, 1);
                    }

                }
            }
            for (int count : examCountPerDay.values()) {
                if (count > 2) {
                    studentMoreThanTwoExamSameDayPunishment += count - 2;
                    logger.debug("Student:" + studentId + " Count:" + count);
                }
            }

        }
        return studentMoreThanTwoExamSameDayPunishment;
    }

    public double minimumGapBetweenExamsStudent() {
        // If student has more than one exam in the same day , they should have at least 1 hour between
        int minimumGapBetweenExamsStudentPunishment = 0;
        for (String studentId : studentExams.keySet()) {
            ArrayList<EncodedExam> assignedExams = studentExams.get(studentId);
            ArrayList<LocalDateTime> examEndTimes = new ArrayList<>();

            for (EncodedExam exam : assignedExams) {
                Course course = Course.findByCourseCode(courses, exam.getCourseCode());
                if (course != null) {
                    int afterExamPrep = course.getAfterExamPrepTime();
                    Timeslot timeslot = exam.getTimeSlot();
                    examEndTimes.add(timeslot.getEnd().minusHours(afterExamPrep));
                }
            }

            // Sort exam end times
            Collections.sort(examEndTimes);

            // Check the gaps between consecutive exams
            for (int i = 1; i < examEndTimes.size(); i++) {
                LocalDateTime previousExamEnd = examEndTimes.get(i - 1);
                LocalDateTime currentExamStart = examEndTimes.get(i);

                // Calculate the gap in hours
                long gapInMinutes = Duration.between(previousExamEnd, currentExamStart).toMinutes();

                // Add penalty if gap is less than 60 minutes
                if (gapInMinutes < 60) {
                    logger.debug(previousExamEnd);
                    logger.debug(currentExamStart);
                    logger.debug("The gap for student between consecutive exams is less than 60 minutes, it is: " + gapInMinutes);
                    minimumGapBetweenExamsStudentPunishment++;
                }
            }
        }
        return minimumGapBetweenExamsStudentPunishment;
    }

    public double invigilatorMoreThanThreeExamSameDay() {
        // No invigilator should monitor more than three exam in one day
        int invigilatorMoreThanThreeExamSameDayPunishment = 0;

        for (String invigilatorId : invigilatorExams.keySet()) {
            // new hashmap : date - exam count
            HashMap<LocalDate, Integer> examCountPerDay = new HashMap<>();
            ArrayList<EncodedExam> assignedExams = invigilatorExams.get(invigilatorId);

            for (EncodedExam exam : assignedExams) {
                Course course = Course.findByCourseCode(courses, exam.getCourseCode());
                if (course != null) {
                    Timeslot timeslot = exam.getTimeSlot();
                    LocalDate examDay = timeslot.getStart().toLocalDate();

                    if (examCountPerDay.containsKey(examDay)) {
                        examCountPerDay.put(examDay, examCountPerDay.get(examDay) + 1);
                    } else {
                        examCountPerDay.put(examDay, 1);
                    }

                }
            }
            logger.debug(examCountPerDay);
            for (int count : examCountPerDay.values()) {
                if (count > 3) {
                    invigilatorMoreThanThreeExamSameDayPunishment += count - 3;
                    logger.debug("Invigilator:" + invigilatorId + " Count:" + count);
                }
            }

        }

        return invigilatorMoreThanThreeExamSameDayPunishment;
    }

    public double minimumGapBetweenExamsInvigilator() {
        // If student has more than one exam in the same day , they should have at least 1 hour between
        int minimumGapBetweenExamsInvigilatorPunishment = 0;

        for (String invigilatorId : invigilatorExams.keySet()) {
            ArrayList<EncodedExam> assignedExams = invigilatorExams.get(invigilatorId);
            ArrayList<LocalDateTime> examEndTimes = new ArrayList<>();

            for (EncodedExam exam : assignedExams) {
                Timeslot timeslot = exam.getTimeSlot();
                examEndTimes.add(timeslot.getEnd());

            }

            // Sort exam end times
            Collections.sort(examEndTimes);

            // Check the gaps between consecutive exams
            for (int i = 1; i < examEndTimes.size(); i++) {
                LocalDateTime previousExamEnd = examEndTimes.get(i - 1);
                LocalDateTime currentExamStart = examEndTimes.get(i);

                // Calculate the gap in hours
                long gapInMinutes = Duration.between(previousExamEnd, currentExamStart).toMinutes();

                // Add penalty if gap is less than 60 minutes
                if (gapInMinutes < 30) {
                    logger.debug(previousExamEnd);
                    logger.debug(currentExamStart);
                    logger.debug("The gap for invigilator between consecutive exams is less than 30 minutes, it is: " + gapInMinutes);
                    minimumGapBetweenExamsInvigilatorPunishment++;
                }
            }
        }

        return minimumGapBetweenExamsInvigilatorPunishment;
    }

    public double noExamsAtWeekends(ArrayList<EncodedExam> chromosome) {
        // No exam at the weekend
        int noExamsWeekendPunishment = 0;

        for (EncodedExam exam : chromosome) {
            LocalDateTime startDateTime = exam.getTimeSlot().getStart();
            LocalDate examDate = startDateTime.toLocalDate();
            DayOfWeek dayOfWeek = examDate.getDayOfWeek();

            // Check if the exam is on a weekend
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                logger.debug("Exam Date: " + examDate);
                logger.debug("Exam can not be placed in the weekends");
                noExamsWeekendPunishment++;
            }

        }

        return noExamsWeekendPunishment;
    }

    // Remaining Soft Constraints
    // Advanced :
    // * Invigilators' preferred time slots should be allocated accordingly.
    // * If 2 exams are to be taken in the same day, these two exams should not be difficult courses
    // with high credits. one of the exams on the same day should be easy and one difficult,
    // or both should be easy.
    // * High-scoring courses should not take place on consecutive days.
    // * In cases where the course has a high number of credits, the exams for that course should be
    // taken at the end of the exam schedule as much as possible. This gives students more time to
    // study for difficult exams.

    public double examsNotInAfternoon() {
        // Most examinations should take place in the afternoon, when students' perceptions are normally most open.

        double afternoonPunishment = 0;

        LocalTime afternoonStart = LocalTime.of(12, 0);
        LocalTime afternoonEnd = LocalTime.of(18, 0);

        int totalExams = 0;
        int afternoonExams = 0;

        for (String studentId : studentExams.keySet()) {

            ArrayList<EncodedExam> assignedExams = studentExams.get(studentId);

            for (EncodedExam exam : assignedExams) {
                Course course = Course.findByCourseCode(courses, exam.getCourseCode());
                if (course != null) {
                    int beforeExamPrep = course.getBeforeExamPrepTime();
                    Timeslot timeslot = exam.getTimeSlot();
                    LocalTime examStartTime = timeslot.getStart().plusHours(beforeExamPrep).toLocalTime();
                    totalExams++;

                    if (!examStartTime.isBefore(afternoonStart) && !examStartTime.isAfter(afternoonEnd)) {
                        afternoonExams++;
                    }
                }

            }
        }

        double afternoonProportion = (double) afternoonExams / totalExams;
        double desiredAfternoonProportion = 0.7;

        if (afternoonProportion < desiredAfternoonProportion) {
            logger.debug("Proportion is not enough: " + afternoonProportion);
            double difference = Math.abs(desiredAfternoonProportion - afternoonProportion);
            afternoonPunishment += difference * 10;
        }
        return afternoonPunishment;
    }

    public double popularExamsAtBeginning(ArrayList<EncodedExam> chromosome) {
        // The lessons that most students have chosen should take place at the beginning of the
        // exam timetable. This gives the teacher enough time to assess the exams.
        // this function can be executed when there is more than 30 students for an exam?
        LocalDate examPeriodStart = startDate;
        LocalDate examPeriodThreshold = examPeriodStart.plusDays(4); // first 4 days

        HashMap<String, Integer> examPopularity = new HashMap<>();
        for (String studentId : studentExams.keySet()) {
            ArrayList<EncodedExam> assignedExams = studentExams.get(studentId);
            for (EncodedExam exam : assignedExams) {
                String courseCode = exam.getCourseCode();
                examPopularity.put(courseCode, examPopularity.getOrDefault(courseCode, 0) + 1);
            }
        }

        ArrayList<HashMap.Entry<String, Integer>> sortedExams = new ArrayList<>(examPopularity.entrySet());
        sortedExams.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())); // Descending order
        logger.debug(sortedExams);
        int numberOfPopularExams = 5; //  top 5 popular exams
        ArrayList<String> popularExams = new ArrayList<>();
        for (int i = 0; i < Math.min(numberOfPopularExams, sortedExams.size()); i++) {
            popularExams.add(sortedExams.get(i).getKey());
        }

        // Calculate the penalty for popular exams not being at the beginning
        int popularExamsNotAtBeginningPunishment = 0;
        for (EncodedExam exam : chromosome) {
            if (popularExams.contains(exam.getCourseCode())) {
                Timeslot timeslot = exam.getTimeSlot();
                if (timeslot.getStart().toLocalDate().isAfter(examPeriodThreshold)) {
                    popularExamsNotAtBeginningPunishment++;
                    logger.debug("Popular Exam that is not in the first 4 days: " + exam.getCourseCode());
                    logger.debug("Timeslot : " + timeslot);

                }
            }
        }

        return popularExamsNotAtBeginningPunishment;
    }
}
