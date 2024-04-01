package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.DataPreprocessing.RandomDataGenerator;

public class App 
{
    public static void main( String[] args )
    {
        final Logger logger = LogManager.getLogger(App.class);
        logger.info("Application started...");
        RandomDataGenerator generator = new RandomDataGenerator();
        logger.info((generator.combineAllData()));
        logger.info("Application finished!");
    }
}
