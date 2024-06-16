package org.example.geneticAlgorithm.operators;

import org.example.models.Chromosome;
import org.example.models.EncodedExam;
import org.example.utils.ConfigHelper;
import org.example.utils.DataStructureHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Crossover {

    private Random random = new Random();
    private final double crossoverRate = Double.parseDouble(ConfigHelper.getProperty("CROSSOVER_RATE"));
    Comparator<EncodedExam> comparator = EncodedExam.sortExamsByCourseCode();


    public ArrayList<Chromosome> onePointCrossover(ArrayList<Chromosome> parents) {
        Chromosome firstParent;
        Chromosome secondParent;
        ArrayList<Chromosome> childChromosomes = new ArrayList<>();


        for (Chromosome parent : parents) {
            parent.getEncodedExams().sort(comparator);
        }

        int i = 0;
        while (i < parents.size() / 2) {//burdan emin değilim bakarız yine
            double randomProbability = random.nextDouble();
            Chromosome firstChildChromosome = new Chromosome();
            Chromosome secondChildChromosome = new Chromosome();

            if (randomProbability <= crossoverRate) {
                int randomParent1 = random.nextInt(parents.size());
                int randomParent2;
                do {
                    randomParent2 = random.nextInt(parents.size());
                } while (randomParent1 == randomParent2);


                firstParent = parents.get(randomParent1);
                secondParent = parents.get(randomParent2);
                int crossoverPoint = random.nextInt(firstParent.getEncodedExams().size());

                ArrayList<EncodedExam> firstChildList = new ArrayList<>();
                firstChildList.addAll(firstParent.getEncodedExams().subList(0, crossoverPoint));
                firstChildList.addAll(secondParent.getEncodedExams().subList(crossoverPoint,
                        firstParent.getEncodedExams().size()));

                firstChildChromosome.setEncodedExams(firstChildList);



                ArrayList<EncodedExam> secondChildList = new ArrayList<>();
                secondChildList.addAll(secondParent.getEncodedExams().subList(0, crossoverPoint));
                secondChildList.addAll(firstParent.getEncodedExams().subList(crossoverPoint,
                        firstParent.getEncodedExams().size()));

                secondChildChromosome.setEncodedExams(secondChildList);

                childChromosomes.add(firstChildChromosome);
                childChromosomes.add(secondChildChromosome);
            }
            i++;
        }
        return childChromosomes;
    }
}
