package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Chromosome;
import org.example.models.EncodedExam;
import org.example.models.Timeslot;
import org.example.utils.ConfigHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Mutation {
    /*
     * Calculate avg fitness value of population
     * calculate fitness value for each chromosome
     * threshold = avg fitness value
     * if fitness value of a chromosome < threshold, set high mutation rate
     * if fitness value of a chromosome >=, set low mutation rate
     */
    private static final Logger logger = LogManager.getLogger(Mutation.class);
    private final Map<Chromosome, Double> mutationRates = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private final double lowMutationRate = Double.parseDouble(ConfigHelper.getProperty("LOW_MUTATION_RATE"));
    private final double highMutationRate = Double.parseDouble(ConfigHelper.getProperty("HIGH_MUTATION_RATE"));
    private final int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));
    private final ArrayList<Chromosome> elitChromosomes = new ArrayList<>();
    private final double elitismPercent = populationSize * 0.1;

    public void mutation(ArrayList<Chromosome> population) {

        ArrayList<Double> fitnessScores = new ArrayList<>();
        for (Chromosome chromosome : population) {
            fitnessScores.add(chromosome.getFitnessScore());
        }

        double threshHold = calculateAvgFitnessScore(fitnessScores);
        setMutationRates(population, threshHold);
        elitism(population);

        mutationRates.forEach((key, value) -> {
            double randomProbability = random.nextDouble() * 0.1;

            if (randomProbability <= value && !elitChromosomes.contains(key)) {
                swapMutation(key);
            }
        });
    }

    private void elitism(ArrayList<Chromosome> population) {
        population.sort(Chromosome.sortChromosomesByFitnessScoreDescendingOrder);//azalan

        for (Chromosome chromosome : population) {
            elitChromosomes.add(chromosome);
            if (elitChromosomes.size() == elitismPercent) {
                break;
            }
        }
    }

    public void swapMutation(Chromosome chromosome) {
        int randomExam1 = random.nextInt(chromosome.getEncodedExams().size());
        int randomExam2;
        do {
            randomExam2 = random.nextInt(chromosome.getEncodedExams().size());
        } while (randomExam1 == randomExam2);

        EncodedExam firstExam = chromosome.getEncodedExams().get(randomExam1);
        EncodedExam secondExam = chromosome.getEncodedExams().get(randomExam2);

        Timeslot tempTimeSlot = firstExam.getTimeSlot();
        firstExam.setTimeSlot(secondExam.getTimeSlot());
        secondExam.setTimeSlot(tempTimeSlot);

        String tempClassCode = firstExam.getClassroomCode();
        firstExam.setClassroomCode(secondExam.getClassroomCode());
        secondExam.setClassroomCode(tempClassCode);
    }

    public void randomResetMutation(Chromosome chromosome) {

    }

    private void setMutationRates(ArrayList<Chromosome> population, double threshHold) {

        for (Chromosome chromosome : population) {
            if (chromosome.getFitnessScore() < threshHold) {
                mutationRates.put(chromosome, highMutationRate);
            } else {
                mutationRates.put(chromosome, lowMutationRate);
            }
        }
    }

    private double calculateAvgFitnessScore(ArrayList<Double> fitnessScores) {
        double totalFitnessScore = 0;

        for (Double score : fitnessScores) {
            totalFitnessScore += score;
        }
        return totalFitnessScore / fitnessScores.size();
    }
}
