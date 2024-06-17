package org.example.models;

import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Chromosome {

    private long chromosomeId;
    private ArrayList<EncodedExam> encodedExams;
    private int age;
    private double fitnessScore;

    public Chromosome(long chromosomeId, ArrayList<EncodedExam> encodedExams, int age) {
        this.chromosomeId = chromosomeId;
        this.encodedExams = encodedExams;
        this.age = age;
    }

    public static Comparator<Chromosome> sortChromosomesByAge = new Comparator<Chromosome>() {

        @Override
        public int compare(Chromosome c1, Chromosome c2) {
            return Integer.compare(c2.age, c1.age);
        }
    };

    public static Comparator<Chromosome> sortChromosomesByFitnessScoreDescendingOrder = new Comparator<Chromosome>() {

        @Override
        public int compare(Chromosome c1, Chromosome c2) {
            return Double.compare(c2.fitnessScore, c1.fitnessScore);
        }
    };

    public static Comparator<Chromosome> sortChromosomesByFitnessScoreAscendingOrder = new Comparator<Chromosome>() {

        @Override
        public int compare(Chromosome c1, Chromosome c2) {
            return Double.compare(c1.fitnessScore, c2.fitnessScore);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o)  return true;
        if (!(o instanceof Chromosome)) return false;
        Chromosome chromosome = (Chromosome) o;
        return this.chromosomeId == chromosome.chromosomeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chromosomeId);
    }

}
