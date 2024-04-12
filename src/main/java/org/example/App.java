package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dataPreprocessing.RandomDataGenerator;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.models.Classroom;
import org.example.models.Course;
import org.example.models.Invigilator;
import org.example.models.Student;
import org.example.utils.ArraylistHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
