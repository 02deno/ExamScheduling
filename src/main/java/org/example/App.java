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
        boolean removeFolderContents = true; // Set this to true if you want to remove folder contents of graph
        String folderPath = "graphs/";
        if (removeFolderContents) {
            deleteFolderContents(new File(folderPath));
        }

        logger.info("Application started...");

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        geneticAlgorithm.generateData();
        geneticAlgorithm.initializationAndEncode();
        for (int k = 0; k < wantedExamScheduleCount; k++) {
            geneticAlgorithm.visualization();
        }

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;
        logger.info("Application finished! Time taken: " + durationMinutes + " minutes " + durationSeconds + " seconds");
    }

}
