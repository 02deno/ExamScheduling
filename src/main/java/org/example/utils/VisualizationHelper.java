package org.example.utils;

import org.example.models.Classroom;
import org.example.models.Course;
import org.example.models.Student;

import java.util.ArrayList;
import java.util.List;

public class VisualizationHelper {
    public static void generateReports(ArrayList<Course> courses, ArrayList<Student> students, ArrayList<Classroom> classrooms) {
        // Reports that are always the same for exam schedules : students, courses, timeslots
        String baseFileName = "graphs/GeneralInformation/";
        FileHelper.createDirectory(baseFileName);

        HTMLHelper.generateStudentReport(students, baseFileName + "students_report.html", "Assigned Students Report");
        HTMLHelper.generateCourseReport(courses, baseFileName + "courses_report.html", "Assigned Courses Report");


        // Histogram - course capacities and timeslot counts
        ArrayList<Integer> courseCapacities = new ArrayList<>();
        ArrayList<Integer> timeslotCounts = new ArrayList<>();
        for (Course course : courses) {
            int capacity = course.getRegisteredStudents().size();
            courseCapacities.add(capacity);
            int requiredTimeslotCount = (course.getBeforeExamPrepTime() + course.getExamDuration() + course.getAfterExamPrepTime());
            timeslotCounts.add(requiredTimeslotCount);
        }
        // this histogram is made to see distribution and decide how many invigilators need to observe the exams
        HTMLHelper.generateHistogram(courseCapacities, baseFileName + "courseCapacityHistogram.html", "The number of students in courses");
        HTMLHelper.generateHistogram(timeslotCounts, baseFileName + "requiredTimeslotHistogram.html", "Timeslot histogram in Hour");

        // Histogram - classroom capacities
        ArrayList<Integer> classroomCapacities = new ArrayList<>();
        for (Classroom classroom : classrooms) {
            int capacity = classroom.getCapacity();
            classroomCapacities.add(capacity);
        }
        HTMLHelper.generateHistogram(classroomCapacities, baseFileName + "classroomCapacityHistogram.html", "Classroom Capacity");

    }

    public static void generateFitnessPlots() {
        // For Fitness Scores
        String fitnessFilePath = "graphs/FitnessScores/fitness_scores.csv";
        List<Double> averageFitnessScoresOfPopulations = ExcelDataParserHelper.averageFitnessScoresOfPopulations(fitnessFilePath);
        List<Double> bestFitnessScoresOfPopulations = ExcelDataParserHelper.bestFitnessScoresOfPopulations(fitnessFilePath);
        HTMLHelper.generateLinePlot(averageFitnessScoresOfPopulations, "Average Fitness Scores of Populations", "average_fitness_scores.html");
        HTMLHelper.generateLinePlot(bestFitnessScoresOfPopulations, "Best Fitness Scores of Populations", "best_fitness_scores.html");

        // For Hard Constraints Scores
        String fitnessHardFilePath = "graphs/FitnessScores/fitness_scores_HARD.csv";
        List<Double> averageHardFitnessScoresOfPopulations = ExcelDataParserHelper.averageConstraintScoresOfPopulations(fitnessHardFilePath);
        List<Double> bestHardFitnessScoresOfPopulations = ExcelDataParserHelper.bestConstraintScoresOfPopulations(fitnessHardFilePath);
        HTMLHelper.generateLinePlot(averageHardFitnessScoresOfPopulations, "Average Hard Constraint Scores of Populations", "average_fitness_scores_HARD.html");
        HTMLHelper.generateLinePlot(bestHardFitnessScoresOfPopulations, "Best Hard Constraint Scores of Populations", "best_fitness_scores_HARD.html");

        // For Soft Constraints Scores
        String fitnessSoftFilePath = "graphs/FitnessScores/fitness_scores_SOFT.csv";
        List<Double> averageSoftFitnessScoresOfPopulations = ExcelDataParserHelper.averageConstraintScoresOfPopulations(fitnessSoftFilePath);
        List<Double> bestSoftFitnessScoresOfPopulations = ExcelDataParserHelper.bestConstraintScoresOfPopulations(fitnessSoftFilePath);
        HTMLHelper.generateLinePlot(averageSoftFitnessScoresOfPopulations, "Average Soft Constraint Scores of Populations", "average_fitness_scores_SOFT.html");
        HTMLHelper.generateLinePlot(bestSoftFitnessScoresOfPopulations, "Best Soft Constraint Scores of Populations", "best_fitness_scores_SOFT.html");


    }

    // TODO(Deniz) : % of criteria met graph
    // TODO(Deniz) : combine mean, best and worst of scores in one graph
    // TODO(Deniz) : with Elitsim / without Elitism graphs
    // TODO(Deniz) : with adaptive parameters, with mutation rate of 0.1 and 0.001 in same graph
    // TODO(Deniz) : with termination time
    // TODO(Deniz) : needed time to reach gloabal optimum (average fitness score > 0.9)
    // TODO(Deniz) : implement restart when initial best fitness is too low or
    //  there is no progress in 50 generations
    //  TODO(Deniz) : implement random/grid search for parameter control

}
