package org.example.geneticAlgorithm.operators;

import org.example.models.EncodedExam;
import org.example.utils.DataStructureHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Crossover {

    private Random random = new Random();
    private final double crossoverRate = 0.7;
    Comparator<EncodedExam> comparator = DataStructureHelper.sortExamsByCourseCode();


    public ArrayList<ArrayList<EncodedExam>> onePointCrossover(ArrayList<ArrayList<EncodedExam>> parents) {
        ArrayList<EncodedExam> firstParent;
        ArrayList<EncodedExam> secondParent;
        ArrayList<ArrayList<EncodedExam>> childChromosomes = new ArrayList<>();


        for (ArrayList<EncodedExam> parent : parents) {
            parent.sort(comparator);
        }

        int i = 0;
        while (i < parents.size() / 2) {//burdan emin değilim bakarız yine
            double randomProbability = random.nextDouble();
            ArrayList<EncodedExam> firstChildChromosome = new ArrayList<>();
            ArrayList<EncodedExam> secondChildChromosome = new ArrayList<>();

            if (randomProbability <= crossoverRate) {
                int randomParent1 = random.nextInt(parents.size());
                int randomParent2;
                do {
                    randomParent2 = random.nextInt(parents.size());
                } while (randomParent1 == randomParent2);


                firstParent = parents.get(randomParent1);
                secondParent = parents.get(randomParent2);
                int crossoverPoint = random.nextInt(firstParent.size());

                firstChildChromosome.addAll(firstParent.subList(0, crossoverPoint));
                firstChildChromosome.addAll(secondParent.subList(crossoverPoint, firstParent.size()));

                secondChildChromosome.addAll(secondParent.subList(0, crossoverPoint));
                secondChildChromosome.addAll(firstParent.subList(crossoverPoint, firstParent.size()));

                childChromosomes.add(firstChildChromosome);
                childChromosomes.add(secondChildChromosome);
            }
            i++;
        }
        return childChromosomes;
    }
}
