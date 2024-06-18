package org.example.geneticAlgorithm.parameter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.utils.ConfigHelper;
import org.example.utils.ExcelDataParserHelper;

import java.util.List;
import java.util.Random;

public class HyperparameterSearch {
    private static final Logger logger = LogManager.getLogger(HyperparameterSearch.class);
    private final Hyperparameters parameters;

    public HyperparameterSearch() {
        this.parameters = new Hyperparameters();
    }

    public long gridSearch() {
        long totalStartTime = System.currentTimeMillis();

        for (int populationSize : parameters.getPopulationSizes()) {
            for (int generationCount : parameters.getGenerationCounts()) {
                for (int generationWithoutImprovement : parameters.getGenerationWithoutImprovementNumbers()) {
                    for (double lowMutationRate : parameters.getLowMutationRates()) {
                        for (double highMutationRate : parameters.getHighMutationRates()) {
                            for (double crossoverRate : parameters.getCrossoverRates()) {

                                logger.debug("Population Size: " + populationSize);
                                logger.debug("Generation Count: " + generationCount);
                                logger.debug("Generations without improvement: " + generationWithoutImprovement);
                                logger.debug("Low Mutation Rate: " + lowMutationRate);
                                logger.debug("High Mutation Rate: " + highMutationRate);
                                logger.debug("Crossover Rate: " + crossoverRate);

                                ConfigHelper.setProperty("POPULATION_SIZE", String.valueOf(populationSize));
                                ConfigHelper.setProperty("MAX_GENERATIONS", String.valueOf(generationCount));
                                ConfigHelper.setProperty("GENERATIONS_WITHOUT_IMPROVEMENT", String.valueOf(generationWithoutImprovement));
                                ConfigHelper.setProperty("LOW_MUTATION_RATE", String.valueOf(lowMutationRate));
                                ConfigHelper.setProperty("HIGH_MUTATION_RATE", String.valueOf(highMutationRate));
                                ConfigHelper.setProperty("CROSSOVER_RATE", String.valueOf(crossoverRate));

                                ConfigHelper.saveConfig();

                                long startTime = System.currentTimeMillis();
                                GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
                                geneticAlgorithm.algorithm();
                                long endTime = System.currentTimeMillis();
                                long durationMs = endTime - startTime;
                                long executionTime = durationMs / 1000;

                                saveExperiment(populationSize, generationCount, generationWithoutImprovement,
                                        lowMutationRate, highMutationRate, crossoverRate, executionTime);

                                // save best with parameters and return that in the end of search
                                // Experiment Number - Best Fitness score table in the end
                                // Total Execution Time of Grid Search
                            }
                        }
                    }
                }

            }
        }
        long totalEndTime = System.currentTimeMillis();
        long totalDurationMs = totalEndTime - totalStartTime;
        long totalExecutionTime = totalDurationMs / 1000;
        return totalExecutionTime;
    }

    public long randomSearch(int iterations) {
        long totalStartTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            Random random = new Random();
            int populationSize = random.nextInt(parameters.getPopulationSizeMax() - parameters.getPopulationSizeMin() + 1) + parameters.getPopulationSizeMin();
            int generationCount = random.nextInt(parameters.getGenerationNumberMax() - parameters.getGenerationNumberMin() + 1) + parameters.getGenerationNumberMin();
            int generationWithoutImprovement = random.nextInt(parameters.getGenerationWithoutImprovementMax() - parameters.getGenerationWithoutImprovementMin() + 1) + parameters.getGenerationWithoutImprovementMin();
            double lowMutationRate = parameters.getLowMutationRateMin() + (parameters.getLowMutationRateMax() - parameters.getLowMutationRateMin()) * random.nextDouble();
            double highMutationRate = parameters.getHighMutationRateMin() + (parameters.getHighMutationRateMax() - parameters.getHighMutationRateMin()) * random.nextDouble();
            double crossoverRate = parameters.getCrossoverRateMin() + (parameters.getCrossoverRateMax() - parameters.getCrossoverRateMin()) * random.nextDouble();

            logger.debug("Population Size: " + populationSize);
            logger.debug("Generation Count: " + generationCount);
            logger.debug("Generations without improvement: " + generationWithoutImprovement);
            logger.debug("Low Mutation Rate: " + lowMutationRate);
            logger.debug("High Mutation Rate: " + highMutationRate);
            logger.debug("Crossover Rate: " + crossoverRate);

            ConfigHelper.setProperty("POPULATION_SIZE", String.valueOf(populationSize));
            ConfigHelper.setProperty("MAX_GENERATIONS", String.valueOf(generationCount));
            ConfigHelper.setProperty("GENERATIONS_WITHOUT_IMPROVEMENT", String.valueOf(generationWithoutImprovement));
            ConfigHelper.setProperty("LOW_MUTATION_RATE", String.valueOf(lowMutationRate));
            ConfigHelper.setProperty("HIGH_MUTATION_RATE", String.valueOf(highMutationRate));
            ConfigHelper.setProperty("CROSSOVER_RATE", String.valueOf(crossoverRate));

            ConfigHelper.saveConfig();

            long startTime = System.currentTimeMillis();
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
            geneticAlgorithm.algorithm();
            long endTime = System.currentTimeMillis();
            long durationMs = endTime - startTime;
            long executionTime = durationMs / 1000;

            saveExperiment(populationSize, generationCount, generationWithoutImprovement,
                    lowMutationRate, highMutationRate, crossoverRate, executionTime);
        }
        long totalEndTime = System.currentTimeMillis();
        long totalDurationMs = totalEndTime - totalStartTime;
        long totalExecutionTime = totalDurationMs / 1000;
        return totalExecutionTime;
    }

    private void saveExperiment(int populationSize, int generationCount, int generationWithoutImprovement,
                                double lowMutationRate, double highMutationRate, double crossoverRate, long executionTime) {

        String fitnessFilePath = "graphs/FitnessScores/fitness_scores.csv";
        List<Double> averageFitnessScoresOfPopulations = ExcelDataParserHelper.averageFitnessScoresOfPopulations(fitnessFilePath);
        List<Double> bestFitnessScoresOfPopulations = ExcelDataParserHelper.bestFitnessScoresOfPopulations(fitnessFilePath);

        // Folder Name : Experiment 1
        // Folder Content : Parameter Table, Best Fitness Score, Execution Time,
        // Plot : Best, Average, Worst in one Table

    }
}
