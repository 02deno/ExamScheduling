package org.example.geneticAlgorithm.operators;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.example.models.Chromosome;
import org.example.models.EncodedExam;
import org.example.utils.ConfigHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Crossover {

    private final Random random = new Random();
    private final double crossoverRate = Double.parseDouble(ConfigHelper.getProperty("CROSSOVER_RATE"));
    private Comparator<EncodedExam> comparator = EncodedExam.sortExamsByCourseCode();
    private ArrayList<Chromosome> childChromosomes = new ArrayList<>();;
    private Chromosome firstChildChromosome;
    private Chromosome secondChildChromosome;
    private ArrayList<EncodedExam> firstChildList;
    private ArrayList<EncodedExam> secondChildList;
    private int crossoverPoint;
    private int firstCrossoverPoint;
    private  int secondCrossoverPoint;

    public ArrayList<Chromosome> onePointCrossover(ArrayList<Chromosome> parents, long chromosomeIdCounter) {

        for (Chromosome parent : parents) {
            parent.getEncodedExams().sort(comparator);
        }

        int i = 0;
        while (i < parents.size() / 2) {
            double randomProbability = random.nextDouble();
            firstChildChromosome = new Chromosome();
            secondChildChromosome = new Chromosome();
            firstChildList = new ArrayList<>();
            secondChildList = new ArrayList<>();

            if (randomProbability <= crossoverRate) {
                Chromosome firstParent = getRandomParents(parents).right;
                Chromosome secondParent = getRandomParents(parents).left;

                crossoverPoint = random.nextInt(firstParent.getEncodedExams().size());


                createOffspring(firstChildList, firstParent, secondParent, firstChildChromosome, chromosomeIdCounter, true);
                chromosomeIdCounter++;

                createOffspring(secondChildList, secondParent, firstParent, secondChildChromosome, chromosomeIdCounter, true);
                chromosomeIdCounter++;

                childChromosomes.add(firstChildChromosome);
                childChromosomes.add(secondChildChromosome);
            }
            i++;
        }
        return childChromosomes;
    }

    public ArrayList<Chromosome> twoPointCrossover(ArrayList<Chromosome> parents, long chromosomeIdCounter) {
        for (Chromosome parent : parents) {
            parent.getEncodedExams().sort(comparator);
        }

        int i = 0;
        while (i < parents.size() / 2) {
            double randomProbability = random.nextDouble();
            firstChildChromosome = new Chromosome();
            secondChildChromosome = new Chromosome();
            firstChildList = new ArrayList<>();
            secondChildList = new ArrayList<>();

            if (randomProbability <= crossoverRate) {
                Chromosome firstParent = getRandomParents(parents).right;
                Chromosome secondParent = getRandomParents(parents).left;

                firstCrossoverPoint = random.nextInt(firstParent.getEncodedExams().size() - 2);
                secondCrossoverPoint = random.nextInt(
                        (firstParent.getEncodedExams().size() - 1) - (firstCrossoverPoint + 1)) + (firstCrossoverPoint + 1);


                createOffspring(firstChildList, firstParent, secondParent, firstChildChromosome, chromosomeIdCounter, false);
                chromosomeIdCounter++;

                createOffspring(secondChildList, secondParent, firstParent, secondChildChromosome, chromosomeIdCounter, false);
                chromosomeIdCounter++;

                childChromosomes.add(firstChildChromosome);
                childChromosomes.add(secondChildChromosome);
            }
            i++;
        }
        return childChromosomes;
    }

    private ImmutablePair<Chromosome, Chromosome> getRandomParents(ArrayList<Chromosome> parents) {
        int randomParent1 = random.nextInt(parents.size());
        int randomParent2;
        do {
            randomParent2 = random.nextInt(parents.size());
        } while (randomParent1 == randomParent2);

        Chromosome firstParent = parents.get(randomParent1);
        Chromosome secondParent = parents.get(randomParent2);

        return new ImmutablePair<>(firstParent, secondParent);
    }

    private void createOffspring(ArrayList<EncodedExam> childList, Chromosome parent1, Chromosome parent2,
                                 Chromosome childChromosome, long chromosomeIdCounter, boolean isOnePointCrossover) {

        if (isOnePointCrossover) {
            childList.addAll(parent1.getEncodedExams().subList(0, crossoverPoint));
            childList.addAll(parent2.getEncodedExams().subList(crossoverPoint, parent1.getEncodedExams().size()));
        } else {
            childList.addAll(parent1.getEncodedExams().subList(0, firstCrossoverPoint));
            childList.addAll(parent2.getEncodedExams().subList(firstCrossoverPoint, secondCrossoverPoint));
            childList.addAll(parent1.getEncodedExams().subList(secondCrossoverPoint, parent1.getEncodedExams().size()));
        }


        childChromosome.setEncodedExams(childList);
        childChromosome.setAge(1);
        childChromosome.setChromosomeId(chromosomeIdCounter);
    }
}
