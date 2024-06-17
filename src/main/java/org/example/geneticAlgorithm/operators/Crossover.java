package org.example.geneticAlgorithm.operators;

import org.example.models.Chromosome;
import org.example.models.EncodedExam;
import org.example.utils.ConfigHelper;
import org.example.utils.DataStructureHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Crossover {

    private final Random random = new Random();
    private final double crossoverRate = Double.parseDouble(ConfigHelper.getProperty("CROSSOVER_RATE"));
    Comparator<EncodedExam> comparator = EncodedExam.sortExamsByCourseCode();
    int crossoverPoint;

    public ArrayList<Chromosome> onePointCrossover(ArrayList<Chromosome> parents, long chromosomeIdCounter) {

        ArrayList<Chromosome> childChromosomes = new ArrayList<>();


        for (Chromosome parent : parents) {
            parent.getEncodedExams().sort(comparator);
        }

        int i = 0;
        while (i < parents.size() / 2) {
            double randomProbability = random.nextDouble();
            Chromosome firstChildChromosome = new Chromosome();
            Chromosome secondChildChromosome = new Chromosome();
            ArrayList<EncodedExam> firstChildList = new ArrayList<>();
            ArrayList<EncodedExam> secondChildList = new ArrayList<>();

            if (randomProbability <= crossoverRate) {
                int randomParent1 = random.nextInt(parents.size());
                int randomParent2;
                do {
                    randomParent2 = random.nextInt(parents.size());
                } while (randomParent1 == randomParent2);


                Chromosome firstParent = parents.get(randomParent1);
                Chromosome secondParent = parents.get(randomParent2);
                crossoverPoint = random.nextInt(firstParent.getEncodedExams().size());


                createOffspring(firstChildList, firstParent, secondParent, firstChildChromosome, chromosomeIdCounter);
                chromosomeIdCounter++;

                createOffspring(secondChildList, secondParent, firstParent, secondChildChromosome, chromosomeIdCounter);
                chromosomeIdCounter++;

                childChromosomes.add(firstChildChromosome);
                childChromosomes.add(secondChildChromosome);
            }
            i++;
        }
        return childChromosomes;
    }

    private void createOffspring(ArrayList<EncodedExam> childList, Chromosome parent1, Chromosome parent2,
                                 Chromosome childChromosome, long chromosomeIdCounter) {
        childList.addAll(parent1.getEncodedExams().subList(0, crossoverPoint));
        childList.addAll(parent2.getEncodedExams().subList(crossoverPoint, parent1.getEncodedExams().size()));

        childChromosome.setEncodedExams(childList);
        childChromosome.setAge(1);
        childChromosome.setChromosomeId(chromosomeIdCounter);
    }
}
