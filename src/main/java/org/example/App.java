package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;

import java.io.File;

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


        logger.info("Application started...");

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.generateData();
        geneticAlgorithm.initializationAndEncode();
        geneticAlgorithm.visualization(wantedExamScheduleCount);
        geneticAlgorithm.calculateFitness();
        geneticAlgorithm.selectParents();
        geneticAlgorithm.crossover();

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;

        logger.info("Application finished! Time taken: " + durationMinutes + " minutes " + durationSeconds + " seconds");
    }

}
