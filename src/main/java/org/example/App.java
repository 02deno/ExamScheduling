package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.geneticAlgorithm.operators.Encode;
import org.example.models.EncodedExam;
import org.example.models.Exam;

import java.util.ArrayList;

public class App
{
    public static void main( String[] args )
    {
        long startTime = System.currentTimeMillis();
        final Logger logger = LogManager.getLogger(App.class);
        logger.info("Application started...");

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.generateData();
        ArrayList<Exam> exams = geneticAlgorithm.initialization();

        Encode encode = new Encode();
        ArrayList<EncodedExam> encodedExams = encode.encodeOperator(exams);


        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;
        logger.info("Application finished! Time taken: " + durationMinutes + " minutes " + durationSeconds + " seconds");
    }

}
