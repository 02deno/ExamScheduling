package org.example.geneticAlgorithm.operators;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.*;
import org.example.utils.ConfigHelper;

import java.time.*;
import java.util.*;
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
    private final int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));
    private final LocalDate startDate = LocalDate.parse(ConfigHelper.getProperty("START_DATE"));
    private final LocalDate endDate = LocalDate.parse(ConfigHelper.getProperty("END_DATE"));
    private final LocalTime startTime = LocalTime.parse(ConfigHelper.getProperty("START_TIME"));
    private final LocalTime endTime = LocalTime.parse(ConfigHelper.getProperty("END_TIME"));
    private final ArrayList<Chromosome> eliteChromosomes = new ArrayList<>();
    private final double elitismPercent = Double.parseDouble(ConfigHelper.getProperty("ELITISM_PERCENT"));

    public void mutation(ArrayList<Chromosome> population, ArrayList<Classroom> classrooms, double lowMutationRate, double highMutationRate, boolean isStable, ArrayList<Invigilator> invigilators) {

        ArrayList<Double> fitnessScores = new ArrayList<>();
        for (Chromosome chromosome : population) {
            fitnessScores.add(chromosome.getFitnessScore());
        }

        double threshHold = calculateAvgFitnessScore(population);
        setMutationRates(population, threshHold, lowMutationRate, highMutationRate);
        elitism(population);

        mutationRates.forEach((key, value) -> {
            double randomProbability = random.nextDouble() * 0.1;

            if (randomProbability <= value && !eliteChromosomes.contains(key)) {
                int examNumberToBeChanged = key.getFitnessScore() < threshHold ? 4 : 2;

                if (isStable) {
                    swapMutation(key, examNumberToBeChanged);
                } else {
                    randomResetMutation(key, classrooms, examNumberToBeChanged, invigilators);
                }
            }
        });
    }

    private void elitism(ArrayList<Chromosome> population) {
        population.sort(Chromosome.sortChromosomesByFitnessScoreDescendingOrder);//azalan

        for (Chromosome chromosome : population) {
            eliteChromosomes.add(chromosome);
            if (eliteChromosomes.size() == populationSize * elitismPercent) {
                break;
            }
        }
    }

    public void swapMutation(Chromosome chromosome, double examNumberToBeChanged) {
        Set<ImmutablePair<Integer, Integer>> uniqueRandomExamIndexPairs = new HashSet<>();
        while (uniqueRandomExamIndexPairs.size() < examNumberToBeChanged / 2) {
            int randomExamIndex = random.nextInt(chromosome.getEncodedExams().size());
            int randomExamIndex2;
            do {
                randomExamIndex2 = random.nextInt(chromosome.getEncodedExams().size());
            } while (randomExamIndex == randomExamIndex2);
            uniqueRandomExamIndexPairs.add(new ImmutablePair<>(randomExamIndex, randomExamIndex2));
        }

        for (ImmutablePair<Integer, Integer> indexPair : uniqueRandomExamIndexPairs) {
            EncodedExam firstOriginalExam = chromosome.getEncodedExams().get(indexPair.left);
            EncodedExam secondOriginalExam = chromosome.getEncodedExams().get(indexPair.right);

            EncodedExam firstCopyExam = new EncodedExam(firstOriginalExam.getCourseCode(), firstOriginalExam.getClassroomCode(),
                    firstOriginalExam.getTimeSlot(), firstOriginalExam.getInvigilators());
            EncodedExam secondCopyExam = new EncodedExam(secondOriginalExam.getCourseCode(), secondOriginalExam.getClassroomCode(),
                    secondOriginalExam.getTimeSlot(), secondOriginalExam.getInvigilators());

            Timeslot tempTimeslot = firstCopyExam.getTimeSlot();
            firstCopyExam.setTimeSlot(secondCopyExam.getTimeSlot());
            secondCopyExam.setTimeSlot(tempTimeslot);

            String tempClassCode = firstCopyExam.getClassroomCode();
            firstCopyExam.setClassroomCode(secondCopyExam.getClassroomCode());
            secondCopyExam.setClassroomCode(tempClassCode);

            ArrayList<String> tempInvigilators = firstCopyExam.getInvigilators();
            firstCopyExam.setInvigilators(secondCopyExam.getInvigilators());
            secondCopyExam.setInvigilators(tempInvigilators);
            EncodedExam.updateEncodedExam(chromosome.getEncodedExams(), firstCopyExam);
            EncodedExam.updateEncodedExam(chromosome.getEncodedExams(), secondCopyExam);
        }
    }

    public void randomResetMutation(Chromosome chromosome, ArrayList<Classroom> classrooms, double examNumberToBeChanged, ArrayList<Invigilator> invigilators) {
        Set<Integer> uniqueRandomExamIndexes = new HashSet<>();
        while (uniqueRandomExamIndexes.size() < examNumberToBeChanged) {
            int randomExamIndex = random.nextInt(chromosome.getEncodedExams().size());
            uniqueRandomExamIndexes.add(randomExamIndex);
        }

        for (int index : uniqueRandomExamIndexes) {
            EncodedExam originalExam = chromosome.getEncodedExams().get(index);
            EncodedExam copyExam = new EncodedExam(originalExam.getCourseCode(), originalExam.getClassroomCode(),
                    originalExam.getTimeSlot(), originalExam.getInvigilators());
            copyExam.setTimeSlot(getRandomTimeslot(copyExam, getRandomDay()));

            int randomClassroomIndex = random.nextInt(classrooms.size());
            copyExam.setClassroomCode(classrooms.get(randomClassroomIndex).getClassroomCode());

            int randomInvigilatorNumber = random.nextInt(4) + 1;
            ArrayList<String> randomInvigilators = new ArrayList<>();
            for (int i = 0; i < randomInvigilatorNumber; i++) {
                int randomInvigilatorIndex = random.nextInt(invigilators.size());
                randomInvigilators.add(invigilators.get(randomInvigilatorIndex).getID());
            }
            copyExam.setInvigilators(randomInvigilators);

            EncodedExam.updateEncodedExam(chromosome.getEncodedExams(), copyExam);
        }
        //HTMLHelper.generateExamTableDila(startDate, endDate, chromosome.getEncodedExams(), chromosome.getChromosomeId() + " After Random Reset Mutation.html");

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

    private void setMutationRates(ArrayList<Chromosome> population, double threshHold, double lowMutationRate, double highMutationRate) {

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
