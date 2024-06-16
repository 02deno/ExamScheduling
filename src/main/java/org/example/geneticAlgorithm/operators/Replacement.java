package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.models.EncodedExam;
import org.example.utils.ConfigHelper;

import java.util.*;

import static org.example.utils.DataStructureHelper.sortByValueAscending;
import static org.example.utils.DataStructureHelper.sortByValueDescending;

//iyileştirirken en iyi %10 u bir yere kaydet onları yaşlı bile olsakarsilme
public class Replacement {
    private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);
    int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));

    public void ageBasedReplacement(HashMap<ArrayList<EncodedExam>, Integer> chromosomeAgesMap, int childChromosomesSize,
                                    ArrayList<ArrayList<EncodedExam>> population) {

        chromosomeAgesMap = sortByValueDescending(chromosomeAgesMap);
        for (int i = 0; i <= childChromosomesSize; i++) {
            for (ArrayList<EncodedExam> key : chromosomeAgesMap.keySet()) {
                population.remove(key);
                break;
            }
        }
    }

    public void fitnessBasedReplacement(HashMap<ArrayList<EncodedExam>, Double> hardConstraintFitnessScores, int childChromosomesSize,
                                        ArrayList<ArrayList<EncodedExam>> population) {
        hardConstraintFitnessScores = sortByValueAscending(hardConstraintFitnessScores);

        for (int i = 0; i <= childChromosomesSize; i++) {
            for (ArrayList<EncodedExam> key : hardConstraintFitnessScores.keySet()) {
                population.remove(key);
                break;
            }
        }
    }
}
