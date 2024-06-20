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

        String graphsFolderPath = "graphs/";
        deleteFolderContents(new File(graphsFolderPath));
        String experimentsFolderPath = "experiments/";
        deleteFolderContents(new File(experimentsFolderPath));

        logger.info("Application started...");

        logger.info("Experiments to find best parameters have started....");
        HyperparameterSearch hyperparameterSearch = new HyperparameterSearch();
        String destination = "src/main/resources/";

//        double[] bestExperimentRandomSearch = hyperparameterSearch.randomSearch(3);
//        logger.info("Best Experiment Id of Random Search: " + bestExperimentRandomSearch[0] +
//                "\nBest Experiment Fitness Score of Random Search: " + bestExperimentRandomSearch[1] +
//                "\nBest Experiment Convergence Rate of Random Search: " + bestExperimentRandomSearch[2]);
//        String source = "experiments/experiment_" + (int) bestExperimentRandomSearch[0] + "/config.properties";
//        FileHelper.copyFile(source, destination);

//        double[] bestExperimentGridSearch = hyperparameterSearch.gridSearch();
//        logger.info("Best Experiment Id of Grid Search: " + bestExperimentGridSearch[0] +
//                "\nBest Experiment Fitness Score of Grid Search: " + bestExperimentGridSearch[1] +
//                "\nBest Experiment Convergence Rate of Grid Search: " + bestExperimentGridSearch[2]);
//        source = "experiments/experiment_" + (int) bestExperimentRandomSearch[0] + "/config.properties";
//        FileHelper.copyFile(source, destination);
//

//
//        // If both searches will be executed, uncomment this :
//        if (bestExperimentRandomSearch[2] > bestExperimentGridSearch[2]) {
//            // random search > grid search
//            source = "experiments/experiment_" + bestExperimentRandomSearch[0] + "/config.properties";
//            FileHelper.copyFile(source, destination);
//        } else {
//            // grid search > random search
//            source = "experiments/experiment_" + bestExperimentRandomSearch[0] + "/config.properties";
//            FileHelper.copyFile(source, destination);
//        }

        logger.info("Experiments for best parameters have ended.");

        logger.info("Genetic Algorithm with best parameters has started....");
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        geneticAlgorithm.algorithm(false, 0);
        logger.info("Genetic Algorithm with best parameters has ended.");

        long endTime = System.currentTimeMillis();
        long durationMs = endTime - startTime;
        long durationSeconds = durationMs / 1000;
        long durationMinutes = durationSeconds / 60;

        logger.info("Application finished! Time taken: " + durationMinutes + " minutes or " + durationSeconds + " seconds");

    }

}
