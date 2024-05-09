package org.example.geneticAlgorithm.operators;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Course;
import org.example.models.EncodedExam;
import org.example.models.Student;
import org.example.models.Timeslot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
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


    private static final Logger logger = LogManager.getLogger(Fitness.class);
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
     * TODO(Deniz) : directly eliminate a chromosome if a hard constraint is violated
     *  we can do this by multiplying with zero or a negative number
     * */
    private ArrayList<Course> courses;
    private ArrayList<Student> students;

    public double fitnessScore(ArrayList<EncodedExam> encodedExams) {
        double fitnessScore;
        // use f1 score to calculate average, harmonic average
        // n = fitness function count
        int n = 2;
        fitnessScore = 2 / ((1 / checkCourseExamCompatibility(encodedExams)) +
                (1 / classroomOverlapped(encodedExams)));
        return fitnessScore;
    }

    public double checkCourseExamCompatibility(ArrayList<EncodedExam> chromosome) {
        // During Initialization all chromosomes are going to be best(fitness score = 1)
        // because of heuristic initialization, but after crossover or/and mutation
        // it can be changed. That's why this function is implemented.

        // Hard Constraints
        // all courses have just one exam
        // all courses have the required timeslot for both invigilators and students
        // all courses have the required number of invigilators to observe the exam
        // all courses have exactly one classroom assigned to them if no sessions
        Set<String> uniqueCourseCodes = new HashSet<>();
        int duplicatedCoursesPunishment = 0;
        int requiredTimeslotPunishment = 0;
        int invigilatorCountPunishment = 0;
        for (EncodedExam exam : chromosome) {
            String courseCode = exam.getCourseCode();
            Course course = Course.findByCourseCode(courses, courseCode);
            assert course != null;

            int invigilatorCount = exam.getInvigilators().size();
            Timeslot timeslots = exam.getTimeSlot();
            int examTimeslotCount = (int) Duration.between(timeslots.getStart(), timeslots.getEnd()).toHours();
            int timeslotCountForInvigilator = course.getBeforeExamPrepTime() + course.getExamDuration() + course.getAfterExamPrepTime();
            int timeslotCountForStudent = course.getExamDuration();

            // all courses have just one exam
            duplicatedCoursesPunishment += (uniqueCourseCodes.add(courseCode)) ? 0 : 1;

            // all courses have the required number of invigilators to observe the exam
            // if there are more invigilator than it is supposed to be is that okay ?
            int capacity = course.getRegisteredStudents().size();
            int requiredInvigilator = capacity < 20 ? 1 : capacity < 75 ? 2 : (capacity < 150 ? 3 : 4);
            invigilatorCountPunishment = Math.abs(requiredInvigilator - invigilatorCount);

            // all courses have the required timeslot for both invigilators and students
            // punish it proportional to the difference
            int differenceInvigilator = Math.abs(examTimeslotCount - timeslotCountForInvigilator);
            int differenceStudent = Math.abs((examTimeslotCount - (course.getBeforeExamPrepTime() + course.getAfterExamPrepTime())) - timeslotCountForStudent);
            requiredTimeslotPunishment += differenceInvigilator;
            requiredTimeslotPunishment += differenceStudent;

            // all courses have exactly one classroom assigned to them if no sessions
        }

//        logger.info("Duplicated Course Punishment: " + duplicatedCoursesPunishment);
//        logger.info("Required Timeslot Punishment: " + requiredTimeslotPunishment);
//        logger.info("Invigilator Count Punishment: " + invigilatorCountPunishment);
        return (double) 1 / (1 + duplicatedCoursesPunishment +
                requiredTimeslotPunishment +
                invigilatorCountPunishment);

    }

    public double classroomOverlapped(ArrayList<EncodedExam> chromosome) {
        // No classroom can be assigned to more than one exam at the same moment.
        int classroomPunishment = 0;

        return (double) 1 / (1 + classroomPunishment);
    }

    public double studentOverlapped() {
        // No student can be assigned to more than one exam at the same moment.
        int studentPunishment = 0;

        return (double) 1 / (1 + studentPunishment);
    }

    public double invigilatorOverlapped() {
        // No invigilator can be assigned to more than one exam at the same moment.
        int invigilatorPunishment = 0;

        return (double) 1 / (1 + invigilatorPunishment);
    }

    public double startAndEndTimeViolated() {
        // No exam can be held before or after the defined time frame
        int startAndEndTimPunishment = 0;

        return (double) 1 / (1 + startAndEndTimPunishment);
    }

    public double allExamsHaveClassrooms() {
        // All exams must have classrooms assigned to them
        int allExamsHaveClassroomsPunishment = 0;

        return (double) 1 / (1 + allExamsHaveClassroomsPunishment);
    }


}
