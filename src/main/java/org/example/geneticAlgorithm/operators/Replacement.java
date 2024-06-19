package org.example.geneticAlgorithm.operators;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.models.Chromosome;
import org.example.utils.ConfigHelper;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@ToString
public class Replacement {
    private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);
    private int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));
    private ArrayList<Chromosome> eliteChromosomes = new ArrayList<>();
    private double elitismPercent = Double.parseDouble(ConfigHelper.getProperty("ELITISM_PERCENT"));
    private ArrayList<Chromosome> chromosomesToBeRemoved = new ArrayList<>();
    private Random random = new Random();

    private void elitism(ArrayList<Chromosome> population) {
        population.sort(Chromosome.sortChromosomesByFitnessScoreDescendingOrder);//azalan

        for (Chromosome chromosome : population) {
            eliteChromosomes.add(chromosome);
            if (eliteChromosomes.size() == populationSize * elitismPercent) {
                break;
            }
        }
    }

    public void ageBasedReplacement(ArrayList<Chromosome> population, int childChromosomesSize) {
        elitism(population);
        Collections.shuffle(population);
        population.sort(Chromosome.sortChromosomesByAge);


        for (Chromosome chromosome : population) {
            if (!eliteChromosomes.contains(chromosome)) {
                if (chromosomesToBeRemoved.size() == childChromosomesSize) {
                    break;
                }
                chromosomesToBeRemoved.add(chromosome);
            }
        }

        population.removeAll(chromosomesToBeRemoved);

    }

    public void randomReplacement(ArrayList<Chromosome> population, int childChromosomesSize) {
        elitism(population);
        Collections.shuffle(population);

        for (Chromosome chromosome : population) {
            if (!eliteChromosomes.contains(chromosome)) {
                if (chromosomesToBeRemoved.size() == childChromosomesSize) {
                    break;
                }
                chromosomesToBeRemoved.add(chromosome);
            }
        }

        population.removeAll(chromosomesToBeRemoved);
    }
}
