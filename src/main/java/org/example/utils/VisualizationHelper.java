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
        String baseFileName = "graphs/FitnessScores/plots/";
        FileHelper.createDirectory(baseFileName);

        // For Fitness Scores
        String fitnessFilePath = "graphs/FitnessScores/fitness_scores.csv";
        List<Double> averageFitnessScoresOfPopulations = ExcelDataParserHelper.averageFitnessScoresOfPopulations(fitnessFilePath);
        List<Double> bestFitnessScoresOfPopulations = ExcelDataParserHelper.bestFitnessScoresOfPopulations(fitnessFilePath);
        List<Double> worstFitnessScoresOfPopulations = ExcelDataParserHelper.worstFitnessScoresOfPopulations(fitnessFilePath);
        HTMLHelper.generateLinePlotAll(averageFitnessScoresOfPopulations,
                bestFitnessScoresOfPopulations,
                worstFitnessScoresOfPopulations,
                "Fitness Scores of Populations",
                baseFileName + "fitness_scores.html");

        // For Hard Constraints Scores
        String fitnessHardFilePath = "graphs/FitnessScores/fitness_scores_HARD.csv";
        List<Double> averageHardFitnessScoresOfPopulations = ExcelDataParserHelper.averageConstraintScoresOfPopulations(fitnessHardFilePath);
        List<Double> bestHardFitnessScoresOfPopulations = ExcelDataParserHelper.bestConstraintScoresOfPopulations(fitnessHardFilePath);
        List<Double> worstHardFitnessScoresOfPopulations = ExcelDataParserHelper.worstConstraintScoresOfPopulations(fitnessHardFilePath);
        HTMLHelper.generateLinePlotAll(averageHardFitnessScoresOfPopulations,
                bestHardFitnessScoresOfPopulations,
                worstHardFitnessScoresOfPopulations,
                "HARD Constraint of Populations",
                baseFileName + "fitness_scores_HARD.html");

        // For Soft Constraints Scores
        String fitnessSoftFilePath = "graphs/FitnessScores/fitness_scores_SOFT.csv";
        List<Double> averageSoftFitnessScoresOfPopulations = ExcelDataParserHelper.averageConstraintScoresOfPopulations(fitnessSoftFilePath);
        List<Double> bestSoftFitnessScoresOfPopulations = ExcelDataParserHelper.bestConstraintScoresOfPopulations(fitnessSoftFilePath);
        List<Double> worstSoftFitnessScoresOfPopulations = ExcelDataParserHelper.worstConstraintScoresOfPopulations(fitnessSoftFilePath);
        HTMLHelper.generateLinePlotAll(averageSoftFitnessScoresOfPopulations,
                bestSoftFitnessScoresOfPopulations,
                worstSoftFitnessScoresOfPopulations,
                "SOFT Constraint of Populations",
                baseFileName + "fitness_scores_SOFT.html");

    }

    public static void generateFitnessPlotsExperiment(double experimentId) {
        String fitnessFilePath = "graphs/FitnessScores/fitness_scores.csv";
        String basePath = "experiments/experiment_" + (int) experimentId + "/";
        FileHelper.createDirectory(basePath);
        String outputPath = basePath + "fitness_score.html";

        List<Double> averageFitnessScoresOfPopulations = ExcelDataParserHelper.averageFitnessScoresOfPopulations(fitnessFilePath);
        List<Double> bestFitnessScoresOfPopulations = ExcelDataParserHelper.bestFitnessScoresOfPopulations(fitnessFilePath);
        List<Double> worstFitnessScoresOfPopulations = ExcelDataParserHelper.worstFitnessScoresOfPopulations(fitnessFilePath);
        HTMLHelper.generateLinePlotAll(averageFitnessScoresOfPopulations,
                bestFitnessScoresOfPopulations,
                worstFitnessScoresOfPopulations,
                "Fitness Scores of Populations",
                outputPath);

    }

    // TODO(Deniz) : % of criteria met graph
    // TODO(Deniz) : with Elitsim / without Elitism graphs
    // TODO(Deniz) : with adaptive parameters, with mutation rate of 0.1 and 0.001 in same graph
    // TODO(Deniz) : with termination time
    // TODO(Deniz) : needed time to reach gloabal optimum (average fitness score > 0.9)
    // TODO(Deniz) : implement restart when initial best fitness is too low or
    //  there is no progress in 50 generations

}
