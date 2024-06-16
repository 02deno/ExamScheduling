package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.models.Chromosome;
import org.example.models.EncodedExam;
import org.example.utils.ConfigHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.example.utils.DataStructureHelper.sortByValueAscending;
import static org.example.utils.DataStructureHelper.sortByValueDescending;

//iyileştirirken en iyi %10 u bir yere kaydet onları yaşlı bile olsalar silme
public class Replacement {
    private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);
    int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));

    public void ageBasedReplacement(HashMap<Chromosome, Integer> chromosomeAgesMap, int childChromosomesSize,
                                    ArrayList<Chromosome> population) {
        logger.info(population);
        Collections.sort(population, Chromosome.sortChromosomesByAge);
        logger.info(population);
        population.remove(0);
    }

    public void fitnessBasedReplacement(HashMap<Chromosome, Double> hardConstraintFitnessScores, int childChromosomesSize,
                                        ArrayList<Chromosome> population) {
        hardConstraintFitnessScores = sortByValueAscending(hardConstraintFitnessScores);

        for (int i = 0; i <= childChromosomesSize; i++) {
            for (Chromosome key : hardConstraintFitnessScores.keySet()) {
                population.remove(key);
                break;
            }
        }
    }
}
