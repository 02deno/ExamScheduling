package org.example.models;

import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
@ToString
public class Chromosome {
    
    private ArrayList<EncodedExam> encodedExams;
    private int age;

    public static Comparator<Chromosome> sortChromosomesByAge = new Comparator<Chromosome>() {

        @Override
        public int compare(Chromosome c1, Chromosome c2) {
            return Integer.compare(c2.age, c1.age);
        }
    };
}
