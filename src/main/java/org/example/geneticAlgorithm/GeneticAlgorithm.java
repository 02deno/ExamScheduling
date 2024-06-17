package org.example.geneticAlgorithm;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dataPreprocessing.RandomDataGenerator;
import org.example.geneticAlgorithm.operators.*;
import org.example.models.*;
import org.example.utils.*;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.example.utils.DataStructureHelper.sortByValueDescending;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class GeneticAlgorithm {

    // TODO(Deniz) : Chromosome and Population classes can be avoid to
    //  reduce complexity

    // TODO(Deniz) : Add a diversity calculator, it should calculate the
    //  diversity of a chromosom

    private ArrayList<Course> courses = new ArrayList<>();
    private ArrayList<Invigilator> invigilators = new ArrayList<>();
    private ArrayList<Classroom> classrooms = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Timeslot> timeslots = new ArrayList<>();
    private ArrayList<Exam> exams = new ArrayList<>();
    private ArrayList<EncodedExam> encodedExams = new ArrayList<>();
    private Chromosome chromosome;
    private HashMap<String, ArrayList<?>> chromosomeForVisualization = new HashMap<>();
    private ArrayList<Chromosome> population = new ArrayList<>();
    private ArrayList<HashMap<String, ArrayList<?>>> populationForVisualization = new ArrayList<>();
    private Schedule schedule;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int interval;

    private HashMap<Chromosome, Double> hardConstraintFitnessScores = new HashMap<>();
    private HashMap<Chromosome, Double> softConstraintFitnessScores = new HashMap<>();
    private HashMap<Chromosome, Double> fitnessScores = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);
    private ArrayList<Chromosome> parents = new ArrayList<>();
    private double bestFitnessScore;
    private int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));
    private ArrayList<EncodedExam> encodedExamArrayList = new ArrayList<>();
    private long chromosomeIdCounter = 0;


    public void generateData() {
        HashMap<String, HashMap<String, ArrayList<Object>>> randomData = RandomDataGenerator.combineAllData();
        this.courses = RandomDataGenerator.generateCourseInstances(randomData.get("courseData"));
        this.courses = new ArrayList<>(courses.subList(0, Math.min(Integer.parseInt(ConfigHelper.getProperty("COURSE_COUNT")), courses.size())));

        this.invigilators = RandomDataGenerator.generateInvigilatorInstances(randomData.get("invigilatorData"));
        this.invigilators = new ArrayList<>(invigilators.subList(0, Math.min(Integer.parseInt(ConfigHelper.getProperty("INVIGILATOR_COUNT")), invigilators.size())));

        this.classrooms = RandomDataGenerator.generateClassroomInstances(randomData.get("classroomData"));
        this.classrooms = new ArrayList<>(classrooms.subList(0, Math.min(Integer.parseInt(ConfigHelper.getProperty("CLASSROOM_COUNT")), classrooms.size())));

        this.students = RandomDataGenerator.generateStudentInstances(randomData.get("studentData"));
        this.students = new ArrayList<>(students.subList(0, Math.min(Integer.parseInt(ConfigHelper.getProperty("STUDENT_COUNT")), students.size())));

        this.startDate = LocalDate.parse(ConfigHelper.getProperty("START_DATE"));
        this.endDate = LocalDate.parse(ConfigHelper.getProperty("END_DATE")); // this date is not included
        this.startTime = LocalTime.parse(ConfigHelper.getProperty("START_TIME"));
        this.endTime = LocalTime.parse(ConfigHelper.getProperty("END_TIME"));
        this.interval = Integer.parseInt(ConfigHelper.getProperty("TIME_SLOT_INTERVAL"));
        this.schedule = RandomDataGenerator.generateSchedule(startDate, endDate, startTime, endTime, interval);
        this.timeslots = schedule.calculateTimeSlots();

        logger.debug("Number of Students: " + students.size());
        logger.debug("Number of Classroom: " + classrooms.size());
        logger.debug("Number of invigilators: " + invigilators.size());
        logger.debug("Number of courses: " + courses.size());
        logger.debug("Number of timeslots: " + schedule.calculateMaxTimeSlots());

        HashMap<String, ArrayList<?>> resultCoursesStudents = Initialization.heuristicMapCoursesWithStudents(this.courses, this.students);
        this.courses = DataStructureHelper.castArrayList(resultCoursesStudents.get("courses"), Course.class);
        this.students = DataStructureHelper.castArrayList(resultCoursesStudents.get("students"), Student.class);
        logger.debug("heuristicMapCoursesWithStudents finished.");

        File holidaysFile = new File(FileHelper.holidayFilePath);
        if (!holidaysFile.exists()) {
            FileHelper.saveHolidaysToFile();
        }

    }

    public ArrayList<Chromosome> initializationAndEncode() {
        int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));
        for (int i = 0; i < populationSize; i++) {

            logger.debug("Population " + i);

            HashMap<String, ArrayList<?>> resultExams = Initialization.createExamInstances(this.courses);
            this.exams = DataStructureHelper.castArrayList(resultExams.get("exams"), Exam.class);
            logger.debug("createExamInstances finished.");
            Random rand = new Random();

            Collections.shuffle(this.exams, new Random(rand.nextInt(10000)));
            Collections.shuffle(this.invigilators, new Random(rand.nextInt(10000)));
            Collections.shuffle(this.classrooms, new Random(rand.nextInt(10000)));

            HashMap<String, ArrayList<?>> resultCoursesInvigilators = Initialization.heuristicMapExamsWithInvigilators(exams, invigilators);
            this.exams = DataStructureHelper.castArrayList(resultCoursesInvigilators.get("exams"), Exam.class);
            logger.debug("heuristicMapExamsWithInvigilators finished.");

            Collections.shuffle(exams, new Random(rand.nextInt(10000)));
            HashMap<String, ArrayList<?>> resultCoursesClassrooms = Initialization.heuristicMapExamsWithClassrooms(exams, classrooms);
            this.exams = DataStructureHelper.castArrayList(resultCoursesClassrooms.get("exams"), Exam.class);
            logger.debug("heuristicMapExamsWithClassrooms finished.");

            Collections.shuffle(exams, new Random(rand.nextInt(10000)));
            HashMap<String, ArrayList<?>> resultCoursesTimeslots = Initialization.heuristicMapExamsWithTimeslots(exams, timeslots);
            this.exams = DataStructureHelper.castArrayList(resultCoursesTimeslots.get("exams"), Exam.class);
            logger.debug("heuristicMapExamsWithTimeslots finished.");

            encode();

            // for visualization purposes and reduce complexity
            this.chromosomeForVisualization.put("exams", new ArrayList<>(exams));
            this.chromosomeForVisualization.put("invigilators", new ArrayList<>(invigilators));
            this.chromosomeForVisualization.put("classrooms", new ArrayList<>(classrooms));
            this.populationForVisualization.add(new HashMap<>(chromosomeForVisualization));
            reset();
        }
        VisualizationHelper.generateReports(courses, students, classrooms);
        return population;
    }

    public void encode() {
        this.encodedExams = Encode.encode(this.exams);

        chromosome = new Chromosome(chromosomeIdCounter, encodedExams, 0);
        chromosomeIdCounter++;
        this.population.add(chromosome);
        logger.debug("Encode is finished.");
    }

    public void visualization(int wantedExamScheduleCount, int currentGeneration) {

        String baseFileName = "graphs/Population" + currentGeneration + "/";
        FileHelper.createDirectory(baseFileName);

        Set<Integer> uniqueNumbers = new HashSet<>();
        Random rand = new Random();
        while (uniqueNumbers.size() < wantedExamScheduleCount) {
            uniqueNumbers.add(rand.nextInt(populationForVisualization.size()));
        }

        for (int k = 0; k < wantedExamScheduleCount; k++) {

            // Exam Schedule :
            // this will visualize a random exam schedule from population

            // this exam schedule is for invigilators not for students
            int n = (Integer) uniqueNumbers.toArray()[k];
            HashMap<String, ArrayList<?>> randomInfo = populationForVisualization.get(n);
            ArrayList<EncodedExam> randomExamScheduleForInvigilators = Encode.encode(DataStructureHelper.castArrayList(randomInfo.get("exams"), Exam.class));
            HTMLHelper.generateExamTable(startTime, endTime, startDate, endDate, interval, randomExamScheduleForInvigilators, baseFileName + "Exam Schedule-" + n + " for Invigilators.html");

            ArrayList<EncodedExam> randomExamScheduleForStudents = new ArrayList<>();
            for (EncodedExam encodedExam : randomExamScheduleForInvigilators) {
                Course course = Course.findByCourseCode(courses, encodedExam.getCourseCode());
                if (course != null) {
                    int beforeExam = course.getBeforeExamPrepTime();
                    int afterExam = course.getAfterExamPrepTime();
                    Timeslot combinedTimeslot = encodedExam.getTimeSlot();
                    Timeslot examTimeslot = new Timeslot(combinedTimeslot.getStart().plusHours(beforeExam), combinedTimeslot.getEnd().minusHours(afterExam));
                    randomExamScheduleForStudents.add(new EncodedExam(encodedExam.getCourseCode(),
                            encodedExam.getClassroomCode(),
                            examTimeslot,
                            encodedExam.getInvigilators()));
                }
            }
            HTMLHelper.generateExamTable(startTime, endTime, startDate, endDate, interval, randomExamScheduleForStudents, baseFileName + "Exam Schedule-" + n + " for Students.html");
            HTMLHelper.generateExamTableDila(startDate, endDate, randomExamScheduleForStudents, baseFileName + "Exam ScheduleDila-" + n + " for Students.html");

            // Reports that are changing : invigilators, classrooms, exam schedules
            HTMLHelper.generateInvigilatorReport(DataStructureHelper.castArrayList(randomInfo.get("invigilators"), Invigilator.class), baseFileName + "invigilator_report_" + n + ".html", "Invigilator Report");
            HTMLHelper.generateClassroomReport(DataStructureHelper.castArrayList(randomInfo.get("classrooms"), Classroom.class), baseFileName + "classroom_report_" + n + ".html", "Classroom Report");
            HTMLHelper.generateExamReport(DataStructureHelper.castArrayList(randomInfo.get("exams"), Exam.class), baseFileName + "exams_" + n + ".html", "Exam Schedule");
        }

    }

    public void reset() {
        // reset classrooms and invigilators
        ArrayList<Invigilator> resetInvigilators = new ArrayList<>();
        ArrayList<Classroom> resetClassrooms = new ArrayList<>();
        for (Invigilator originalInvigilator : this.invigilators) {
            Invigilator invigilator = new Invigilator(originalInvigilator.getID(), originalInvigilator.getName(), originalInvigilator.getSurname(), originalInvigilator.getMaxCoursesMonitoredCount());
            resetInvigilators.add(invigilator);
        }

        for (Classroom originalClassroom : this.classrooms) {
            Classroom classroom = new Classroom(originalClassroom.getClassroomCode(), originalClassroom.getClassroomName(), originalClassroom.getCapacity(), originalClassroom.isPcLab(), originalClassroom.getClassroomProperties());
            resetClassrooms.add(classroom);
        }
        this.invigilators = resetInvigilators;
        this.classrooms = resetClassrooms;
    }

    public void calculateFitness() {
        // make a hashmap with encoded exam as a key
        // and fitness score as a value
        Fitness fitness = new Fitness(courses, students, classrooms, invigilators, startDate, endDate, startTime, endTime);
        ArrayList<double[]> hardConstraintScoresList = new ArrayList<>();
        ArrayList<double[]> softConstraintScoresList = new ArrayList<>();
        ArrayList<Double> fitnessScoresList = new ArrayList<>();

        hardConstraintFitnessScores.clear();
        softConstraintFitnessScores.clear();
        fitnessScores.clear();

        for (Chromosome chromosome : population) {
            double[][] scores = fitness.fitnessScore(chromosome.getEncodedExams());

            double[] hardConstraintScores = scores[0];
            double[] softConstraintScores = scores[1];
            double fitnessScore = scores[2][0];

            hardConstraintScoresList.add(hardConstraintScores);
            softConstraintScoresList.add(softConstraintScores);
            fitnessScoresList.add(fitnessScore);

            double hardFitnessScore = hardConstraintScores[hardConstraintScores.length - 1];
            hardConstraintFitnessScores.put(chromosome, hardFitnessScore);

            double softFitnessScore = softConstraintScores[softConstraintScores.length - 1];
            softConstraintFitnessScores.put(chromosome, softFitnessScore);

            fitnessScores.put(chromosome, fitnessScore);
            chromosome.setFitnessScore(fitnessScore);

        }

        // sort hashmaps based on fitness scores, this tables only contain fitness scores
        hardConstraintFitnessScores = sortByValueDescending(hardConstraintFitnessScores);
        softConstraintFitnessScores = sortByValueDescending(softConstraintFitnessScores);
        fitnessScores = sortByValueDescending(fitnessScores);

        // visualize
        for (Chromosome chromosome : fitnessScores.keySet()) {
            logger.debug("Hashcode of Exam Schedule: " + chromosome.hashCode() + ", Score: " + fitnessScores.get(chromosome));
        }

        // this tables contain all the fitness function scores
        String baseFileName = "graphs/FitnessScores/";
        FileHelper.createDirectory(baseFileName);
        FileHelper.writeHardFitnessScoresToFile(hardConstraintScoresList, baseFileName + "fitness_scores_HARD.csv");
        FileHelper.writeSoftFitnessScoresToFile(softConstraintScoresList, baseFileName + "fitness_scores_SOFT.csv");
        FileHelper.writeFitnessScoresToFile(fitnessScoresList, baseFileName + "fitness_scores.csv");
    }
    public double findBestFitnessScore() {
        population.sort(Chromosome.sortChromosomesByFitnessScoreDescendingOrder);
        return population.get(0).getFitnessScore();
    }

    public void selectParents() {
        Selection selection = new Selection();
        parents = selection.rouletteWheelSelection(population);
    }

    public ArrayList<Chromosome> crossover() {
        Crossover crossover = new Crossover();
        ArrayList<Chromosome> childChromosomes = crossover.twoPointCrossover(parents, chromosomeIdCounter);
        chromosomeIdCounter = childChromosomes.get(childChromosomes.size() - 1).getChromosomeId();
        chromosomeIdCounter++;

        return childChromosomes;
    }

    public void mutation() {
        Mutation mutation = new Mutation();
        mutation.mutation(population);
    }

    public void replacement(int currentGeneration, int childChromosomesSize) {
        Replacement replacement = new Replacement();

        if (currentGeneration == 1) {
            replacement.randomReplacement(population, childChromosomesSize);
        } else {
            replacement.ageBasedReplacement(population, childChromosomesSize);
        }
    }

    public void updateAgesOfChromosomes() {
        for (Chromosome chromosome: population) {
            chromosome.setAge(chromosome.getAge() + 1);
        }
    }
}
