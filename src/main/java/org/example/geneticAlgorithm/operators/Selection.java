package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Chromosome;
import org.example.utils.ConfigHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Selection {

    /*
    * Parent Selection: (roulette wheel)
    * get fitness score of each individual from fitness function
    * normalize the fitness scores
    * map normalized scores with individuals
    * sum = sum + fitness score for each ind
    * get random number betw 0 and sum
    * calculate sum from the beginning of population -> temp
    * if random number < temp select this one
    * select parents that are 50% of population size
    */

    /*
    * advance
    * try tournament selection
    * change number of selected parents
    * use one of these selection methods and additionally use rank selection when algo get closer to end
    */

    private final int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));
    private final HashMap<Chromosome, Double> normalizedFitnessScores = new HashMap<Chromosome, Double>();
    private static final Logger logger = LogManager.getLogger(Selection.class);
    private final Random random = new Random();
    private final ArrayList<Chromosome> parents = new ArrayList<>();

    public ArrayList<Chromosome> rouletteWheelSelection(ArrayList<Chromosome> population) {
        int i = 0;

        while (i < populationSize/2){
            double totalScore = 0;
            for (Chromosome chromosome : population) {
                totalScore += chromosome.getFitnessScore();
            }

            double randomValue = random.nextDouble() * totalScore;

            double temp = 0;
            for (Chromosome chromosome : population) {
                temp += chromosome.getFitnessScore();
                if (randomValue < temp) {
                    parents.add(chromosome);
                    break;
                }
            }
            i++;
        }
        return parents;

    }

}
