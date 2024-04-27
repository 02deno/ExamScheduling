package org.example.geneticAlgorithm.operators;

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
     * All lessons must have exams assigned to them
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
     * exponential
     *
     * */
}
