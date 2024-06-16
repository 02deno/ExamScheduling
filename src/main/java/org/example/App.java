package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.models.Chromosome;
import org.example.models.EncodedExam;
import org.example.utils.ConfigHelper;

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
        geneticAlgorithm.calculateFitness();

        while (currentGeneration < maxGenerations && generationsWithoutImprovement < toleratedGenerationsWithoutImprovement) {//değiştirilebilir
            currentGeneration += 1;

            geneticAlgorithm.updateAgesOfChromosomes();
            geneticAlgorithm.visualization(wantedExamScheduleCount);
            double bestFitnessScore = geneticAlgorithm.findBestFitnessScore();

            geneticAlgorithm.selectParents();
            childChromosomes = geneticAlgorithm.crossover();
            geneticAlgorithm.mutation();
            geneticAlgorithm.replacement(currentGeneration, childChromosomes.size());
            population.addAll(childChromosomes);

            geneticAlgorithm.calculateFitness();
            double lastBestFitnessScore = geneticAlgorithm.findBestFitnessScore();
            logger.info("bestFitnessScore: " + bestFitnessScore);
            logger.info("lastBestFitnessScore: " + lastBestFitnessScore);

            if (lastBestFitnessScore <= bestFitnessScore) {
                generationsWithoutImprovement += 1;
            } else {
                generationsWithoutImprovement = 0;
            }
        }


        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;

        logger.info("Application finished! Time taken: " + durationMinutes + " minutes " + durationSeconds + " seconds");
    }

}
