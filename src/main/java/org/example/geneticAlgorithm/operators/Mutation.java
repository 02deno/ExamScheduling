package org.example.geneticAlgorithm.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.Chromosome;
import org.example.models.Classroom;
import org.example.models.EncodedExam;
import org.example.models.Timeslot;
import org.example.utils.ConfigHelper;

import java.time.*;
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
    private final LocalDate startDate = LocalDate.parse(ConfigHelper.getProperty("START_DATE"));
    private final LocalDate endDate = LocalDate.parse(ConfigHelper.getProperty("END_DATE"));
    private final LocalTime startTime = LocalTime.parse(ConfigHelper.getProperty("START_TIME"));
    private final LocalTime endTime = LocalTime.parse(ConfigHelper.getProperty("END_TIME"));
    private final ArrayList<Chromosome> elitChromosomes = new ArrayList<>();
    private final double elitismPercent = populationSize * 0.1;

    public void mutation(ArrayList<Chromosome> population, ArrayList<Timeslot> timeslots, ArrayList<Classroom> classrooms) {

        ArrayList<Double> fitnessScores = new ArrayList<>();
        for (Chromosome chromosome : population) {
            fitnessScores.add(chromosome.getFitnessScore());
        }

        double threshHold = calculateAvgFitnessScore(population);
        setMutationRates(population, threshHold);
        elitism(population);

        mutationRates.forEach((key, value) -> {
            double randomProbability = random.nextDouble() * 0.1;

            if (randomProbability <= value && !elitChromosomes.contains(key)) {
                randomResetMutation(key, classrooms);
                //swapMutation(key);
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

        EncodedExam firstExamOriginal = chromosome.getEncodedExams().get(randomExam1);
        EncodedExam secondExamOriginal = chromosome.getEncodedExams().get(randomExam2);

        EncodedExam firstExam = new EncodedExam(firstExamOriginal.getCourseCode(), firstExamOriginal.getClassroomCode(),
                firstExamOriginal.getTimeSlot(), firstExamOriginal.getInvigilators());

        EncodedExam secondExam = new EncodedExam(secondExamOriginal.getCourseCode(), secondExamOriginal.getClassroomCode(),
                secondExamOriginal.getTimeSlot(), secondExamOriginal.getInvigilators());

        Timeslot tempTimeSlot = firstExam.getTimeSlot();
        firstExam.setTimeSlot(secondExam.getTimeSlot());
        secondExam.setTimeSlot(tempTimeSlot);

        String tempClassCode = firstExam.getClassroomCode();
        firstExam.setClassroomCode(secondExam.getClassroomCode());
        secondExam.setClassroomCode(tempClassCode);

        // update Encoded exams
        EncodedExam.updateEncodedExam(chromosome.getEncodedExams(), firstExam);
        EncodedExam.updateEncodedExam(chromosome.getEncodedExams(), secondExam);
    }

    public void randomResetMutation(Chromosome chromosome, ArrayList<Classroom> classrooms) {
        int randomExam1 = random.nextInt(chromosome.getEncodedExams().size());
        int randomExam2;
        do {
            randomExam2 = random.nextInt(chromosome.getEncodedExams().size());
        } while (randomExam1 == randomExam2);

        EncodedExam firstExamOriginal = chromosome.getEncodedExams().get(randomExam1);
        EncodedExam secondExamOriginal = chromosome.getEncodedExams().get(randomExam2);

        EncodedExam firstExam = new EncodedExam(firstExamOriginal.getCourseCode(), firstExamOriginal.getClassroomCode(),
                firstExamOriginal.getTimeSlot(), firstExamOriginal.getInvigilators());
        EncodedExam secondExam = new EncodedExam(secondExamOriginal.getCourseCode(), secondExamOriginal.getClassroomCode(),
                secondExamOriginal.getTimeSlot(), secondExamOriginal.getInvigilators());


        firstExam.setTimeSlot(getRandomTimeslot(firstExam, getRandomDay()));
        int randomClassroom1 = random.nextInt(classrooms.size());
        firstExam.setClassroomCode(classrooms.get(randomClassroom1).getClassroomCode());

        secondExam.setTimeSlot(getRandomTimeslot(secondExam, getRandomDay()));
        int randomClassroom2 = random.nextInt(classrooms.size());
        secondExam.setClassroomCode(classrooms.get(randomClassroom2).getClassroomCode());

        EncodedExam.updateEncodedExam(chromosome.getEncodedExams(), firstExam);
        EncodedExam.updateEncodedExam(chromosome.getEncodedExams(), secondExam);
    }

    private LocalDate getRandomDay() {
        int dayDiff = Math.abs(endDate.getDayOfYear() - startDate.getDayOfYear());
        int randomDayIndex = random.nextInt(dayDiff);

        return startDate.plus(Period.ofDays(randomDayIndex));
    }

    private Timeslot getRandomTimeslot(EncodedExam exam, LocalDate randomDay1) {
        long examDuration = Duration.between(exam.getTimeSlot().getStart(), exam.getTimeSlot().getEnd()).toHours();
        int hourDiff = Math.abs(endTime.getHour() - startTime.getHour());
        int maxTimeBound = hourDiff - (int) examDuration;
        int randomTimeSlotIndex = random.nextInt(maxTimeBound) * 2;
        LocalTime randomStartTime = startTime.plusMinutes(randomTimeSlotIndex * 30L);
        LocalDateTime randomTimeSlotStart = LocalDateTime.of(randomDay1, randomStartTime);
        LocalDateTime randomTimeSlotEnd = LocalDateTime.of(randomDay1, randomStartTime.plusHours(examDuration));

        return new Timeslot(randomTimeSlotStart, randomTimeSlotEnd);
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

    private double calculateAvgFitnessScore(ArrayList<Chromosome> population) {
        double totalFitnessScore = 0;

        for (Chromosome chromosome : population) {
            totalFitnessScore += chromosome.getFitnessScore();
        }
        return totalFitnessScore / population.size();
    }
}
