package org.example.geneticAlgorithm.parameter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.utils.ConfigHelper;
import org.example.utils.FileHelper;
import org.example.utils.VisualizationHelper;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class HyperparameterSearch {
    private static final Logger logger = LogManager.getLogger(HyperparameterSearch.class);
    private final Hyperparameters parameters;
    public static double experimentId = 1;

    public HyperparameterSearch() {
        this.parameters = new Hyperparameters();
    }

    public double[] gridSearch() {
        long totalStartTime = System.currentTimeMillis();
        double bestFitness = 0;
        int bestPopulationSize = 0;
        int bestGenerationCount = 0;
        int bestGenerationWithoutImprovement = 0;
        double bestLowMutationRate = 0;
        double bestHighMutationRate = 0;
        double bestCrossoverRate = 0;
        double bestExecutionTime = 0;
        double bestExperimentId = 0;
        double bestConvergenceRate = 0;
        double bestTournamentSelectionNumber = 0;
        double bestElitismPercent = 0;

        ArrayList<Integer> experimentIds = new ArrayList<>();
        ArrayList<Double> bestFitnessScores = new ArrayList<>();
        ArrayList<Double> convergenceRates = new ArrayList<>();
        ArrayList<Long> executionTimes = new ArrayList<>();
        ArrayList<Integer> populationSizes = new ArrayList<>();
        ArrayList<Integer> generationCounts = new ArrayList<>();
        ArrayList<Integer> generationWithoutImprovements = new ArrayList<>();
        ArrayList<Double> lowMutationRates = new ArrayList<>();
        ArrayList<Double> highMutationRates = new ArrayList<>();
        ArrayList<Double> crossoverRates = new ArrayList<>();
        ArrayList<Integer> tournamentSelectionNumbers = new ArrayList<>();
        ArrayList<Double> elitismPercents = new ArrayList<>();

        for (int populationSize : parameters.getPopulationSizes()) {
            for (int generationCount : parameters.getGenerationCounts()) {
                for (int generationWithoutImprovement : parameters.getGenerationWithoutImprovementNumbers()) {
                    for (double lowMutationRate : parameters.getLowMutationRates()) {
                        for (double highMutationRate : parameters.getHighMutationRates()) {
                            for (double crossoverRate : parameters.getCrossoverRates()) {
                                for (int tournamentSelectionNumber : parameters.getTournamentSelectionNumbers()) {
                                    for (double elitismPercent : parameters.getElitismPercents()) {
                                        //generationCount = 10; // to check grid search functionality, later this line will be deleted

                                        logger.debug("Population Size: " + populationSize);
                                        logger.debug("Generation Count: " + generationCount);
                                        logger.debug("Generations without improvement: " + generationWithoutImprovement);
                                        logger.debug("Low Mutation Rate: " + lowMutationRate);
                                        logger.debug("High Mutation Rate: " + highMutationRate);
                                        logger.debug("Crossover Rate: " + crossoverRate);
                                        logger.debug("Tournament Selection Number: " + tournamentSelectionNumber);
                                        logger.debug("Elitism Percent: " + elitismPercent);

                                        ConfigHelper.setProperty("POPULATION_SIZE", String.valueOf(populationSize));
                                        ConfigHelper.setProperty("MAX_GENERATIONS", String.valueOf(generationCount));
                                        ConfigHelper.setProperty("GENERATIONS_WITHOUT_IMPROVEMENT", String.valueOf(generationWithoutImprovement));
                                        ConfigHelper.setProperty("LOW_MUTATION_RATE", String.valueOf(lowMutationRate));
                                        ConfigHelper.setProperty("HIGH_MUTATION_RATE", String.valueOf(highMutationRate));
                                        ConfigHelper.setProperty("CROSSOVER_RATE", String.valueOf(crossoverRate));
                                        ConfigHelper.setProperty("TOURNAMENT_SELECTION_NUMBER_OF_CHROMOSOMES", String.valueOf(tournamentSelectionNumber));
                                        ConfigHelper.setProperty("ELITISM_PERCENT", String.valueOf(elitismPercent));

                                        ConfigHelper.saveConfig();

                                        long startTime = System.currentTimeMillis();
                                        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
                                        double[] metrics = geneticAlgorithm.algorithm(true, (int) experimentId);
                                        double convergenceRate = metrics[0];
                                        double currentBestFitness = metrics[1];
                                        long endTime = System.currentTimeMillis();
                                        long durationMs = endTime - startTime;
                                        long executionTime = durationMs / 1000;

                                        if (convergenceRate > bestConvergenceRate) {
                                            bestFitness = currentBestFitness;
                                            bestPopulationSize = populationSize;
                                            bestGenerationCount = generationCount;
                                            bestGenerationWithoutImprovement = generationWithoutImprovement;
                                            bestLowMutationRate = lowMutationRate;
                                            bestHighMutationRate = highMutationRate;
                                            bestCrossoverRate = crossoverRate;
                                            bestExecutionTime = executionTime;
                                            bestExperimentId = experimentId;
                                            bestConvergenceRate = convergenceRate;
                                            bestTournamentSelectionNumber = tournamentSelectionNumber;
                                            bestElitismPercent = elitismPercent;
                                        }
                                        experimentIds.add((int) experimentId);
                                        bestFitnessScores.add(currentBestFitness);
                                        convergenceRates.add(convergenceRate);
                                        executionTimes.add(executionTime);
                                        populationSizes.add(populationSize);
                                        generationCounts.add(generationCount);
                                        generationWithoutImprovements.add(generationWithoutImprovement);
                                        lowMutationRates.add(lowMutationRate);
                                        highMutationRates.add(highMutationRate);
                                        crossoverRates.add(crossoverRate);
                                        tournamentSelectionNumbers.add(tournamentSelectionNumber);
                                        elitismPercents.add(elitismPercent);

                                        saveExperiment();
                                        experimentId++;
                                    }

                                }


                            }
                        }
                    }
                }

            }
        }
        long totalEndTime = System.currentTimeMillis();
        long totalDurationMs = totalEndTime - totalStartTime;
        long totalExecutionTime = totalDurationMs / 1000;

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        String output = "experiments/grid_search_results_" + formattedDate + ".html";
        String title = "Grid Search Result Table";
        saveResultsToTable(experimentIds, bestFitnessScores, convergenceRates, executionTimes,
                populationSizes, generationCounts, generationWithoutImprovements, lowMutationRates,
                highMutationRates, crossoverRates, tournamentSelectionNumbers, elitismPercents, output, title);

        logger.info("Total Execution Time of Grid Search in seconds: " + totalExecutionTime);
        logger.info("Best Fitness Score of Grid Search: " + bestFitness);
        logger.info("Best Population Size of Grid Search: " + bestPopulationSize);
        logger.info("Best Generation Count of Grid Search: " + bestGenerationCount);
        logger.info("Best Generations without improvement of Grid Search: " + bestGenerationWithoutImprovement);
        logger.info("Best Low Mutation Rate of Grid Search: " + bestLowMutationRate);
        logger.info("Best High Mutation Rate of Grid Search: " + bestHighMutationRate);
        logger.info("Best Crossover Rate of Grid Search: " + bestCrossoverRate);
        logger.info("Best Execution Time of Grid Search: " + bestExecutionTime);
        logger.info("Best Experiment ID of Grid Search: " + bestExperimentId);
        logger.info("Best Tournament Selection Number of Grid Search: " + bestTournamentSelectionNumber);
        logger.info("Best Elitsim Percent of Grid Search: " + bestElitismPercent);

        return new double[]{bestExperimentId, bestFitness, bestConvergenceRate};
    }

    public double[] randomSearch(int iterations) {
        long totalStartTime = System.currentTimeMillis();
        double bestFitness = 0;
        int bestPopulationSize = 0;
        int bestGenerationCount = 0;
        int bestGenerationWithoutImprovement = 0;
        double bestLowMutationRate = 0;
        double bestHighMutationRate = 0;
        double bestCrossoverRate = 0;
        double bestExecutionTime = 0;
        double bestExperimentId = 0;
        double bestConvergenceRate = 0;
        double bestTournamentSelectionNumber = 0;
        double bestElitismPercent = 0;

        ArrayList<Integer> experimentIds = new ArrayList<>();
        ArrayList<Double> bestFitnessScores = new ArrayList<>();
        ArrayList<Double> convergenceRates = new ArrayList<>();
        ArrayList<Long> executionTimes = new ArrayList<>();
        ArrayList<Integer> populationSizes = new ArrayList<>();
        ArrayList<Integer> generationCounts = new ArrayList<>();
        ArrayList<Integer> generationWithoutImprovements = new ArrayList<>();
        ArrayList<Double> lowMutationRates = new ArrayList<>();
        ArrayList<Double> highMutationRates = new ArrayList<>();
        ArrayList<Double> crossoverRates = new ArrayList<>();
        ArrayList<Integer> tournamentSelectionNumbers = new ArrayList<>();
        ArrayList<Double> elitismPercents = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            Random random = new Random();
            int populationSize = random.nextInt(parameters.getPopulationSizeMax() - parameters.getPopulationSizeMin() + 1) + parameters.getPopulationSizeMin();
            int generationCount = random.nextInt(parameters.getGenerationNumberMax() - parameters.getGenerationNumberMin() + 1) + parameters.getGenerationNumberMin();
            //generationCount = 10; // to check random search functionality, later this line will be deleted
            int generationWithoutImprovement = random.nextInt(parameters.getGenerationWithoutImprovementMax() - parameters.getGenerationWithoutImprovementMin() + 1) + parameters.getGenerationWithoutImprovementMin();
            double lowMutationRate = parameters.getLowMutationRateMin() + (parameters.getLowMutationRateMax() - parameters.getLowMutationRateMin()) * random.nextDouble();
            double highMutationRate = parameters.getHighMutationRateMin() + (parameters.getHighMutationRateMax() - parameters.getHighMutationRateMin()) * random.nextDouble();
            double crossoverRate = parameters.getCrossoverRateMin() + (parameters.getCrossoverRateMax() - parameters.getCrossoverRateMin()) * random.nextDouble();
            int tournamentSelectionNumber = random.nextInt(parameters.getTournamentSelectionNumberMax() - parameters.getTournamentSelectionNumberMin() + 1) + parameters.getTournamentSelectionNumberMin();
            double elitismPercent = parameters.getElitismPercentMin() + (parameters.getElitismPercentMax() - parameters.getElitismPercentMin()) * random.nextDouble();

            logger.debug("Population Size: " + populationSize);
            logger.debug("Generation Count: " + generationCount);
            logger.debug("Generations without improvement: " + generationWithoutImprovement);
            logger.debug("Low Mutation Rate: " + lowMutationRate);
            logger.debug("High Mutation Rate: " + highMutationRate);
            logger.debug("Crossover Rate: " + crossoverRate);
            logger.debug("Tournament Selection Number: " + tournamentSelectionNumber);
            logger.debug("Elitism Percent: " + elitismPercent);

            ConfigHelper.setProperty("POPULATION_SIZE", String.valueOf(populationSize));
            ConfigHelper.setProperty("MAX_GENERATIONS", String.valueOf(generationCount));
            ConfigHelper.setProperty("GENERATIONS_WITHOUT_IMPROVEMENT", String.valueOf(generationWithoutImprovement));
            ConfigHelper.setProperty("LOW_MUTATION_RATE", String.valueOf(lowMutationRate));
            ConfigHelper.setProperty("HIGH_MUTATION_RATE", String.valueOf(highMutationRate));
            ConfigHelper.setProperty("CROSSOVER_RATE", String.valueOf(crossoverRate));
            ConfigHelper.setProperty("TOURNAMENT_SELECTION_NUMBER_OF_CHROMOSOMES", String.valueOf(tournamentSelectionNumber));
            ConfigHelper.setProperty("ELITISM_PERCENT", String.valueOf(elitismPercent));

            ConfigHelper.saveConfig();

            long startTime = System.currentTimeMillis();
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
            double[] metrics = geneticAlgorithm.algorithm(true, (int) experimentId);
            double convergenceRate = metrics[0];
            double currentBestFitness = metrics[1];
            long endTime = System.currentTimeMillis();
            long durationMs = endTime - startTime;
            long executionTime = durationMs / 1000;

            if (convergenceRate > bestConvergenceRate) {
                bestFitness = currentBestFitness;
                bestPopulationSize = populationSize;
                bestGenerationCount = generationCount;
                bestGenerationWithoutImprovement = generationWithoutImprovement;
                bestLowMutationRate = lowMutationRate;
                bestHighMutationRate = highMutationRate;
                bestCrossoverRate = crossoverRate;
                bestExecutionTime = executionTime;
                bestExperimentId = experimentId;
                bestConvergenceRate = convergenceRate;
                bestTournamentSelectionNumber = tournamentSelectionNumber;
                bestElitismPercent = elitismPercent;
            }

            experimentIds.add((int) experimentId);
            bestFitnessScores.add(currentBestFitness);
            convergenceRates.add(convergenceRate);
            executionTimes.add(executionTime);
            populationSizes.add(populationSize);
            generationCounts.add(generationCount);
            generationWithoutImprovements.add(generationWithoutImprovement);
            lowMutationRates.add(lowMutationRate);
            highMutationRates.add(highMutationRate);
            crossoverRates.add(crossoverRate);
            tournamentSelectionNumbers.add(tournamentSelectionNumber);
            elitismPercents.add(elitismPercent);

            saveExperiment();

            experimentId++;
        }
        long totalEndTime = System.currentTimeMillis();
        long totalDurationMs = totalEndTime - totalStartTime;
        long totalExecutionTime = totalDurationMs / 1000;

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        String output = "experiments/random_search_results_" + formattedDate + ".html";
        String title = "Random Search Result Table";
        saveResultsToTable(experimentIds, bestFitnessScores, convergenceRates, executionTimes,
                populationSizes, generationCounts, generationWithoutImprovements, lowMutationRates,
                highMutationRates, crossoverRates, tournamentSelectionNumbers, elitismPercents, output, title);

        logger.info("Total Execution Time of Random Search in seconds: " + totalExecutionTime);
        logger.info("Best Fitness Score of Random Search: " + bestFitness);
        logger.info("Best Population Size of Random Search: " + bestPopulationSize);
        logger.info("Best Generation Count of Random Search: " + bestGenerationCount);
        logger.info("Best Generations without improvement of Random Search: " + bestGenerationWithoutImprovement);
        logger.info("Best Low Mutation Rate of Random Search: " + bestLowMutationRate);
        logger.info("Best High Mutation Rate of Random Search: " + bestHighMutationRate);
        logger.info("Best Crossover Rate of Random Search: " + bestCrossoverRate);
        logger.info("Best Execution Time of Random Search: " + bestExecutionTime);
        logger.info("Best Experiment ID of Random Search: " + bestExperimentId);
        logger.info("Best Tournament Selection Number of Random Search: " + bestTournamentSelectionNumber);
        logger.info("Best Elitsim Percent of Random Search: " + bestElitismPercent);

        return new double[]{bestExperimentId, bestFitness, bestConvergenceRate};

    }

    private void saveExperiment() {

        VisualizationHelper.generateFitnessPlotsExperiment(experimentId);
        String sourcePath = "src/main/resources/config.properties";
        String outputPath = "experiments/experiment_" + (int) experimentId + "/";
        FileHelper.copyFile(sourcePath, outputPath);

    }

    private void saveResultsToTable(ArrayList<Integer> experimentIds, ArrayList<Double> bestFitnessScores,
                                    ArrayList<Double> convergenceRates, ArrayList<Long> executionTimes,
                                    ArrayList<Integer> populationSizes, ArrayList<Integer> generationCounts,
                                    ArrayList<Integer> generationWithoutImprovements, ArrayList<Double> lowMutationRates,
                                    ArrayList<Double> highMutationRates, ArrayList<Double> crossoverRates,
                                    ArrayList<Integer> tournamentSelectionNumbers, ArrayList<Double> elitismPercents,
                                    String output, String title) {

        ArrayList<String> headers = new ArrayList<>();
        headers.add("Experiment Id");
        headers.add("Best Fitness");
        headers.add("Convergence Rate");
        headers.add("Execution Time");
        headers.add("Population Size");
        headers.add("Generation Count");
        headers.add("Generation Without Improvement");
        headers.add("Low Mutation Rate");
        headers.add("High Mutation Rate");
        headers.add("Crossover Rate");
        headers.add("Tournament Selection Number");
        headers.add("Elitism Percent");

        StringBuilder resultHeaders = new StringBuilder("[");
        for (int i = 0; i < headers.size(); i++) {
            resultHeaders.append("'<b>").append(headers.get(i)).append("</b>'");
            if (i < headers.size() - 1) {
                resultHeaders.append(", ");
            }
        }
        resultHeaders.append("]");

        StringBuilder col1 = integerArraylistToString(experimentIds);
        StringBuilder col2 = doubleArraylistToString(bestFitnessScores);
        StringBuilder col3 = doubleArraylistToString(convergenceRates);
        StringBuilder col4 = longArraylistToString(executionTimes);
        StringBuilder col5 = integerArraylistToString(populationSizes);
        StringBuilder col6 = integerArraylistToString(generationCounts);
        StringBuilder col7 = integerArraylistToString(generationWithoutImprovements);
        StringBuilder col8 = doubleArraylistToString(lowMutationRates);
        StringBuilder col9 = doubleArraylistToString(highMutationRates);
        StringBuilder col10 = doubleArraylistToString(crossoverRates);
        StringBuilder col11 = integerArraylistToString(tournamentSelectionNumbers);
        StringBuilder col12 = doubleArraylistToString(elitismPercents);

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <script src='https://cdn.plot.ly/plotly-latest.min.js'></script>\n" +
                "    <title>" + title + "</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h2>" + title + "</h2>\n" +
                "    <div id='myTable'></div>\n" +
                "    <script>\n" +
                "        var data = [{\n" +
                "            type: 'table',\n" +
                "            header: {\n" +
                "                values: " + resultHeaders + ",\n" +
                "                align: 'center',\n" +
                "                line: {width: 1, color: 'black'},\n" +
                "                fill: {color: 'darkblue'},\n" +
                "                font: {family: 'Arial', size: 12, color: 'white'}\n" +
                "            },\n" +
                "            cells: {\n" +
                "                values: [" +
                col1.toString() + "," +
                col2.toString() + "," +
                col3.toString() + "," +
                col4.toString() + "," +
                col5.toString() + "," +
                col6.toString() + "," +
                col7.toString() + "," +
                col8.toString() + "," +
                col9.toString() + "," +
                col10.toString() + "," +
                col11.toString() + "," +
                col12.toString() +
                "],\n" +
                "                align: 'center',\n" +
                "                line: {color: 'black', width: 1},\n" +
                "                fill: {color: ['lightblue', 'white']},\n" +
                "                font: {family: 'Arial', size: 11, color: ['black']}\n" +
                "            }\n" +
                "        }];\n" +
                "        var layout = {\n" +
                "            title: '" + title + "',\n" +
                "            margin: {t: 30, b: 30, l: 30, r: 30}\n" +
                "        };\n" +
                "        Plotly.newPlot('myTable', data, layout);\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";


        try (FileWriter fileWriter = new FileWriter(output)) {
            fileWriter.write(htmlContent);
        } catch (IOException e) {
            logger.error("Some error occured while saving HTML file.");
        }
    }

    public StringBuilder integerArraylistToString(ArrayList<Integer> list) {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
            if (i < list.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result;

    }

    public StringBuilder doubleArraylistToString(ArrayList<Double> list) {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            BigDecimal bigDecimal = new BigDecimal(Double.toString(list.get(i)));
            bigDecimal = bigDecimal.setScale(10, RoundingMode.HALF_UP);
            result.append(bigDecimal.doubleValue());
            if (i < list.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result;

    }

    public StringBuilder longArraylistToString(ArrayList<Long> list) {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
            if (i < list.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result;

    }
}
