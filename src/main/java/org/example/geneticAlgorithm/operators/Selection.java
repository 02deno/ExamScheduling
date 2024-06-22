package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.impl.store.CharUtil;
import org.example.models.Chromosome;
import org.example.utils.ConfigHelper;

import java.util.*;

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
    private final int numberOfChromosomesToBeSelected = Integer.parseInt(ConfigHelper.getProperty("TOURNAMENT_SELECTION_NUMBER_OF_CHROMOSOMES"));
    private static final Logger logger = LogManager.getLogger(Selection.class);
    private final Random random = new Random();
    private final Set<Chromosome> uniqueParents = new HashSet<>();
    private final ArrayList<Chromosome> parents = new ArrayList<>();

    public ArrayList<Chromosome> rouletteWheelSelection(ArrayList<Chromosome> population) {
        int i = 0;

        double totalScore = 0;
        for (Chromosome chromosome : population) {
            totalScore += chromosome.getFitnessScore();
        }

        while (i < populationSize * 0.6) {
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

    public ArrayList<Chromosome> tournamentSelection(ArrayList<Chromosome> population) {
        int i = 0;

        while (i < populationSize) {
            ArrayList<Chromosome> tournamentChromosomes = new ArrayList<>();
            for (int j = 0; j <= numberOfChromosomesToBeSelected; j++) {
                int randomChromosomeIndex = random.nextInt(populationSize);
                tournamentChromosomes.add(population.get(randomChromosomeIndex));
            }

            tournamentChromosomes.sort(Chromosome.sortChromosomesByFitnessScoreDescendingOrder);
            uniqueParents.add(tournamentChromosomes.get(0));
            i++;
        }
        parents.addAll(uniqueParents);
        return parents;
    }

    public ArrayList<Chromosome> rankSelection(ArrayList<Chromosome> population) {
        population.sort(Chromosome.sortChromosomesByFitnessScoreAscendingOrder);
        HashMap<Chromosome, Double> probabilityMap = new HashMap<>();

        double rank = 1;
        for (Chromosome chromosome : population) {
            probabilityMap.put(chromosome, rank / population.size());
            rank++;
        }


        int i = 0;
        double totalProbability = 0;
        for (Double probability : probabilityMap.values()) {
            totalProbability += probability;
        }

        while (i < populationSize * 0.6) {
            double randomValue = random.nextDouble() * totalProbability;

            double temp = 0;

            for (HashMap.Entry<Chromosome, Double> entry : probabilityMap.entrySet()) {
                temp += entry.getValue();
                if (randomValue < temp) {
                    parents.add(entry.getKey());
                    break;
                }
            }
            i++;
        }

        return parents;
    }

}
