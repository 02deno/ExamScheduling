package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;

public class App
{
    public static void main( String[] args )
    {
        final Logger logger = LogManager.getLogger(App.class);
        logger.info("Application started...");

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.generateData();
        geneticAlgorithm.heuristicInitialization();

        logger.info("Application finished!");
    }

}
