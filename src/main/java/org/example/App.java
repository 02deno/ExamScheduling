package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.models.Chromosome;
import org.example.utils.ConfigHelper;
import org.example.utils.VisualizationHelper;

import java.io.File;
import java.util.ArrayList;

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

        while (currentGeneration < maxGenerations && generationsWithoutImprovement < toleratedGenerationsWithoutImprovement) {//değiştirilebilir
            geneticAlgorithm.calculateFitness(true);
            currentGeneration += 1;
            geneticAlgorithm.updateAgesOfChromosomes();
            //geneticAlgorithm.visualization(wantedExamScheduleCount, currentGeneration);
            double bestFitnessScore = geneticAlgorithm.findBestFitnessScore();

            logger.info("-----");
            geneticAlgorithm.selectParents();
            geneticAlgorithm.calculateFitness(false);
            logger.info("bestFitnessScore after selection of parents: " + geneticAlgorithm.findBestFitnessScore());
            childChromosomes = geneticAlgorithm.crossover();
            geneticAlgorithm.calculateFitness(false);
            logger.info("bestFitnessScore after crossover: " + geneticAlgorithm.findBestFitnessScore());
            geneticAlgorithm.mutation();
            geneticAlgorithm.calculateFitness(false);
            logger.info("bestFitnessScore after mutation: " + geneticAlgorithm.findBestFitnessScore());
            geneticAlgorithm.replacement(currentGeneration, childChromosomes.size());
            geneticAlgorithm.calculateFitness(false);
            logger.info("bestFitnessScore after replacement: " + geneticAlgorithm.findBestFitnessScore());
            population.addAll(childChromosomes);
            geneticAlgorithm.calculateFitness(false);
            logger.info("bestFitnessScore after adding all: " + geneticAlgorithm.findBestFitnessScore());

            geneticAlgorithm.calculateFitness(false);
            logger.info("bestFitnessScore after calculating fitness: " + geneticAlgorithm.findBestFitnessScore());
            logger.debug("population size: " + population.size());
            double lastBestFitnessScore = geneticAlgorithm.findBestFitnessScore();
            logger.info("Generation: " + currentGeneration);
            logger.info("bestFitnessScore: " + bestFitnessScore);
            logger.info("lastBestFitnessScore: " + lastBestFitnessScore);
            logger.info("-----");
            if (lastBestFitnessScore <= bestFitnessScore) {
                generationsWithoutImprovement += 1;
            } else {
                generationsWithoutImprovement = 0;
            }
        }

        // Create Graphs and Analyse Fitness Scores
        VisualizationHelper.generateFitnessPlots();

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;

        logger.info("Application finished! Time taken: " + durationMinutes + " minutes " + durationSeconds + " seconds");
    }

}
