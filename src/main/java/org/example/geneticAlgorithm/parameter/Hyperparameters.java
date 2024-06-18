package org.example.geneticAlgorithm.parameter;

import lombok.*;
import org.example.utils.ConfigHelper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@Data
@ToString
public class Hyperparameters {
    private List<Integer> populationSizes;
    private List<Integer> generationCounts;
    private List<Integer> generationWithoutImprovementNumbers;
    private List<Double> lowMutationRates;
    private List<Double> highMutationRates;
    private List<Double> crossoverRates;

    private int populationSizeMin;
    private int populationSizeMax;
    private int generationNumberMin;
    private int generationNumberMax;
    private int generationWithoutImprovementMin;
    private int generationWithoutImprovementMax;
    private double lowMutationRateMin;
    private double lowMutationRateMax;
    private double highMutationRateMax;
    private double highMutationRateMin;
    private double crossoverRateMin;
    private double crossoverRateMax;


    public Hyperparameters() {

        // Grid Search Parameters
        populationSizes = parseIntegerList(ConfigHelper.getProperty("POPULATION_SIZE_VALUES"));
        generationCounts = parseIntegerList(ConfigHelper.getProperty("MAX_GENERATIONS_VALUES"));
        generationWithoutImprovementNumbers = parseIntegerList(ConfigHelper.getProperty("GENERATIONS_WITHOUT_IMPROVEMENT_VALUES"));
        lowMutationRates = parseDoubleList(ConfigHelper.getProperty("LOW_MUTATION_RATE_VALUES"));
        highMutationRates = parseDoubleList(ConfigHelper.getProperty("HIGH_MUTATION_RATE_VALUES"));
        crossoverRates = parseDoubleList(ConfigHelper.getProperty("CROSSOVER_RATE_VALUES"));

        // Random Search Parameters
        populationSizeMin = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE_MIN"));
        populationSizeMax = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE_MAX"));
        generationNumberMin = Integer.parseInt(ConfigHelper.getProperty("MAX_GENERATIONS_MIN"));
        generationNumberMax = Integer.parseInt(ConfigHelper.getProperty("MAX_GENERATIONS_MAX"));
        generationWithoutImprovementMin = Integer.parseInt(ConfigHelper.getProperty("GENERATIONS_WITHOUT_IMPROVEMENT_MIN"));
        generationWithoutImprovementMax = Integer.parseInt(ConfigHelper.getProperty("GENERATIONS_WITHOUT_IMPROVEMENT_MAX"));
        lowMutationRateMin = Double.parseDouble(ConfigHelper.getProperty("LOW_MUTATION_RATE_MIN"));
        lowMutationRateMax = Double.parseDouble(ConfigHelper.getProperty("LOW_MUTATION_RATE_MAX"));
        highMutationRateMin = Double.parseDouble(ConfigHelper.getProperty("HIGH_MUTATION_RATE_MIN"));
        highMutationRateMax = Double.parseDouble(ConfigHelper.getProperty("HIGH_MUTATION_RATE_MAX"));
        crossoverRateMin = Double.parseDouble(ConfigHelper.getProperty("CROSSOVER_RATE_MAX"));
        crossoverRateMax = Double.parseDouble(ConfigHelper.getProperty("CROSSOVER_RATE_MIN"));

    }

    private List<Integer> parseIntegerList(String property) {
        return Stream.of(property.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private List<Double> parseDoubleList(String property) {
        return Stream.of(property.split(","))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }
}
