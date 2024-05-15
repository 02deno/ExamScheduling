package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.geneticAlgorithm.operators.Selection;
import org.example.models.EncodedExam;

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


        logger.info("Application started...");

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.generateData();
        ArrayList<ArrayList<EncodedExam>> population = geneticAlgorithm.initializationAndEncode();
        geneticAlgorithm.visualization(wantedExamScheduleCount);
        geneticAlgorithm.calculateFitness();
        geneticAlgorithm.selectParents();

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;

        logger.info("Application finished! Time taken: " + durationMinutes + " minutes " + durationSeconds + " seconds");
    }

}
