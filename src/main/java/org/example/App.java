package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.geneticAlgorithm.parameter.HyperparameterSearch;

import java.io.File;

import static org.example.utils.FileHelper.deleteFolderContents;

public class App
{
    public static void main( String[] args )
    {
        long startTime = System.currentTimeMillis();
        final Logger logger = LogManager.getLogger(App.class);

        String folderPath = "graphs/";
        deleteFolderContents(new File(folderPath));

        logger.info("Application started...");

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.algorithm();


        HyperparameterSearch hyperparameterSearch = new HyperparameterSearch();
        long executionTimeRandomSearch = hyperparameterSearch.randomSearch(1);
        //long executionTimeGridSearch = hyperparameterSearch.gridSearch();
        //logger.info("Total execution time of Grid Search: " + executionTimeGridSearch +" seconds");
        logger.info("Total execution time of Random Search: " + executionTimeRandomSearch + " seconds");

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;

        logger.info("Application finished! Time taken: " + durationMinutes + " minutes or " + durationSeconds + " seconds");

    }

}
