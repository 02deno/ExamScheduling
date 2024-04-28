package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;

public class App
{
    public static void main( String[] args )
    {
        long startTime = System.currentTimeMillis();
        final Logger logger = LogManager.getLogger(App.class);
        logger.info("Application started...");

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.generateData();
        geneticAlgorithm.initialization();

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;
        logger.info("Application finished! Time taken: " + durationMinutes + " minutes " + durationSeconds + " seconds");
    }

}
