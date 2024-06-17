package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.models.Chromosome;
import org.example.utils.ConfigHelper;
import org.example.utils.ExcelDataParserHelper;
import org.example.utils.HTMLHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.example.utils.FileHelper.deleteFolderContents;

public class App
{
    public static void main( String[] args )
    {
        long startTime = System.currentTimeMillis();
        final Logger logger = LogManager.getLogger(App.class);
        int wantedExamScheduleCount = 3;
        String folderPath = "graphs/";
        deleteFolderContents(new File(folderPath));
        int currentGeneration = 0;
        int generationsWithoutImprovement = 0;
        int maxGenerations = Integer.parseInt(ConfigHelper.getProperty("MAX_GENERATIONS"));
        int toleratedGenerationsWithoutImprovement = Integer.parseInt(ConfigHelper.getProperty("GENERATIONS_WITHOUT_IMPROVEMENT"));

        logger.info("Application started...");

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        ArrayList<Chromosome> population;
        ArrayList<Chromosome> childChromosomes;

        geneticAlgorithm.generateData();
        population = geneticAlgorithm.initializationAndEncode();
        geneticAlgorithm.calculateFitness(false);

        while (currentGeneration < maxGenerations && generationsWithoutImprovement < toleratedGenerationsWithoutImprovement) {//değiştirilebilir
            currentGeneration += 1;
            geneticAlgorithm.updateAgesOfChromosomes();
            geneticAlgorithm.visualization(wantedExamScheduleCount, currentGeneration);
            double bestFitnessScore = geneticAlgorithm.findBestFitnessScore();

            geneticAlgorithm.selectParents();
            childChromosomes = geneticAlgorithm.crossover();
            geneticAlgorithm.mutation();
            geneticAlgorithm.replacement(currentGeneration, childChromosomes.size());
            population.addAll(childChromosomes);

            geneticAlgorithm.calculateFitness(false);
            logger.info("population size: " + population.size());
            double lastBestFitnessScore = geneticAlgorithm.findBestFitnessScore();
            logger.info("Generation: " + currentGeneration);
            logger.info("bestFitnessScore: " + bestFitnessScore);
            logger.info("lastBestFitnessScore: " + lastBestFitnessScore);

            if (lastBestFitnessScore <= bestFitnessScore) {
                generationsWithoutImprovement += 1;
            } else {
                generationsWithoutImprovement = 0;
            }
        }

        // Create Graphs and Analyse Fitness Scores
        String fitnessFilePath = "graphs/FitnessScores/fitness_scores.csv";
        List<Double> averageFitnessScoresOfPopulations = ExcelDataParserHelper.averageFitnessScoresOfPopulations(fitnessFilePath);
        List<Double> bestFitnessScoresOfPopulations = ExcelDataParserHelper.bestFitnessScoresOfPopulations(fitnessFilePath);
        HTMLHelper.generateLinePlot(averageFitnessScoresOfPopulations, "Average Fitness Scores of Populations", "average_fitness_scores.html");
        HTMLHelper.generateLinePlot(bestFitnessScoresOfPopulations, "Best Fitness Scores of Populations", "best_fitness_scores.html");

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;

        logger.info("Application finished! Time taken: " + durationMinutes + " minutes " + durationSeconds + " seconds");
    }

}
