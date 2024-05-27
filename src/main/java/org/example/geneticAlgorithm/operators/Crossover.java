package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.GeneticAlgorithm;
import org.example.models.EncodedExam;
import org.example.utils.ConfigHelper;
import org.example.utils.DataStructureHelper;
import org.example.utils.HTMLHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Crossover {
    private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);
    private Random random = new Random();
    Comparator<EncodedExam> comparator = DataStructureHelper.sortExamsByCourseCode();
/*  private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int interval;*/
    public void onePointCrossover(ArrayList<ArrayList<EncodedExam>> parents) {

        ArrayList<EncodedExam> firstParent;
        ArrayList<EncodedExam> secondParent;
        ArrayList<EncodedExam> childChromosome = new ArrayList<>();
        for (ArrayList<EncodedExam> parent : parents) {
            parent.sort(comparator);
        }

        int randomValue1 = random.nextInt(parents.size());
        int randomValue2;
        do {
            randomValue2 = random.nextInt(parents.size());
        } while (randomValue1 == randomValue2);


        firstParent = parents.get(randomValue1);
        secondParent = parents.get(randomValue2);
        int crossoverPoint = random.nextInt(firstParent.size());

        childChromosome.addAll(firstParent.subList(0, crossoverPoint));
        childChromosome.addAll(secondParent.subList(crossoverPoint, firstParent.size()));

/*        logger.info("crossoverPoint" + crossoverPoint);
        this.startDate = LocalDate.parse(ConfigHelper.getProperty("START_DATE"));
        this.endDate = LocalDate.parse(ConfigHelper.getProperty("END_DATE")); // this date is not included
        this.startTime = LocalTime.parse(ConfigHelper.getProperty("START_TIME"));
        this.endTime = LocalTime.parse(ConfigHelper.getProperty("END_TIME"));
        this.interval = Integer.parseInt(ConfigHelper.getProperty("TIME_SLOT_INTERVAL"));
        HTMLHelper.generateExamTableDila(startTime, endTime, startDate, endDate, interval, firstParent, "Exam ScheduleDilafirstparent" );
        HTMLHelper.generateExamTableDila(startTime, endTime, startDate, endDate, interval, secondParent, "Exam ScheduleDilasecparent" );
        HTMLHelper.generateExamTableDila(startTime, endTime, startDate, endDate, interval, childChromosome, "Exam ScheduleDilachild" );*/
    }
}
