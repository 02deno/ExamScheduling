package org.example.geneticAlgorithm;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dataPreprocessing.RandomDataGenerator;
import org.example.geneticAlgorithm.operators.*;
import org.example.models.*;
import org.example.utils.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

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

    private ArrayList<Course> courses = new ArrayList<>();
    private ArrayList<Invigilator> invigilators = new ArrayList<>();
    private ArrayList<Classroom> classrooms = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Timeslot> timeslots = new ArrayList<>();
    private ArrayList<Exam> exams = new ArrayList<>();
    private ArrayList<EncodedExam> encodedExams = new ArrayList<>();
    private ArrayList<EncodedExam> chromosome;
    private HashMap<String, ArrayList<?>> chromosomeForVisualization = new HashMap<>();
    private ArrayList<ArrayList<EncodedExam>> population = new ArrayList<>();
    private ArrayList<HashMap<String, ArrayList<?>>> populationForVisualization = new ArrayList<>();
    private Schedule schedule;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int interval;
    private HashMap<ArrayList<EncodedExam>, Double> fitnessScores = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);
    private ArrayList<ArrayList<EncodedExam>> parents = new ArrayList<>();


    public void generateData() {
        HashMap<String, HashMap<String, ArrayList<Object>>> randomData = RandomDataGenerator.combineAllData();
        this.courses = RandomDataGenerator.generateCourseInstances(randomData.get("courseData"));
        this.courses = new ArrayList<>(courses.subList(0, Math.min(70, courses.size())));

        this.invigilators = RandomDataGenerator.generateInvigilatorInstances(randomData.get("invigilatorData"));
        this.invigilators = new ArrayList<>(invigilators.subList(0, Math.min(20, invigilators.size())));

        this.classrooms = RandomDataGenerator.generateClassroomInstances(randomData.get("classroomData"));

        this.students = RandomDataGenerator.generateStudentInstances(randomData.get("studentData"));
        this.students = new ArrayList<>(students.subList(0, Math.min(100, students.size())));

        this.startDate = LocalDate.parse(ConfigHelper.getProperty("START_DATE"));
        this.endDate = LocalDate.parse(ConfigHelper.getProperty("END_DATE")); // this date is not included
        this.startTime = LocalTime.parse(ConfigHelper.getProperty("START_TIME"));
        this.endTime = LocalTime.parse(ConfigHelper.getProperty("END_TIME"));
        this.interval = Integer.parseInt(ConfigHelper.getProperty("TIME_SLOT_INTERVAL"));
        this.schedule = RandomDataGenerator.generateSchedule(startDate, endDate, startTime, endTime, interval);
        this.timeslots = schedule.calculateTimeSlots();

        logger.info("Number of Students: " + students.size());
        logger.info("Number of Classroom: " + classrooms.size());
        logger.info("Number of invigilators: " + invigilators.size());
        logger.info("Number of courses: " + courses.size());
        logger.info("Number of timeslots: " + schedule.calculateMaxTimeSlots());

        HashMap<String, ArrayList<?>> resultCoursesStudents = Initialization.heuristicMapCoursesWithStudents(this.courses, this.students);
        this.courses = DataStructureHelper.castArrayList(resultCoursesStudents.get("courses"), Course.class);
        this.students = DataStructureHelper.castArrayList(resultCoursesStudents.get("students"), Student.class);
        logger.info("heuristicMapCoursesWithStudents finished.");


    }

    public void initializationAndEncode() {
        int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));
        for (int i = 0; i < populationSize; i++) {

            //logger.info("Population " + i);

            HashMap<String, ArrayList<?>> resultExams = Initialization.createExamInstances(this.courses);
            this.exams = DataStructureHelper.castArrayList(resultExams.get("exams"), Exam.class);
            logger.info("createExamInstances finished.");
            Random rand = new Random();

            Collections.shuffle(this.exams, new Random(rand.nextInt(10000)));
            Collections.shuffle(this.invigilators, new Random(rand.nextInt(10000)));
            Collections.shuffle(this.classrooms, new Random(rand.nextInt(10000)));

            HashMap<String, ArrayList<?>> resultCoursesInvigilators = Initialization.heuristicMapExamsWithInvigilators(exams, invigilators);
            this.exams = DataStructureHelper.castArrayList(resultCoursesInvigilators.get("exams"), Exam.class);
            logger.info("heuristicMapExamsWithInvigilators finished.");

            Collections.shuffle(exams, new Random(rand.nextInt(10000)));
            HashMap<String, ArrayList<?>> resultCoursesClassrooms = Initialization.heuristicMapExamsWithClassrooms(exams, classrooms);
            this.exams = DataStructureHelper.castArrayList(resultCoursesClassrooms.get("exams"), Exam.class);
            logger.info("heuristicMapExamsWithClassrooms finished.");

            Collections.shuffle(exams, new Random(rand.nextInt(10000)));
            HashMap<String, ArrayList<?>> resultCoursesTimeslots = Initialization.heuristicMapExamsWithTimeslots(exams, timeslots);
            this.exams = DataStructureHelper.castArrayList(resultCoursesTimeslots.get("exams"), Exam.class);
            logger.info("heuristicMapExamsWithTimeslots finished.");

            encode();

            /*Optional<Course> filteredCourseOpt = courses.stream().filter(course -> course.getCourseCode().equals("KKW219")).findAny();
            Course filteredCourse = filteredCourseOpt.orElse(null);

            assert filteredCourse != null;
            logger.info("####" + filteredCourse.getRegisteredStudents());*/

            // for visualization purposes and reduce complexity
            this.chromosomeForVisualization.put("exams", new ArrayList<>(exams));
            this.chromosomeForVisualization.put("invigilators", new ArrayList<>(invigilators));
            this.chromosomeForVisualization.put("classrooms", new ArrayList<>(classrooms));
            this.populationForVisualization.add(new HashMap<>(chromosomeForVisualization));
            reset();
        }
    }

    public void encode() {
        this.encodedExams = Encode.encodeOperator(this.exams);
        this.chromosome = encodedExams;
        this.population.add(chromosome);
        logger.info("Encode is finished.");
    }

    public void visualization(int wantedExamScheduleCount) {
        for (int k = 0; k < wantedExamScheduleCount; k++) {
            VisualizationHelper.generateReports(courses, students, classrooms, interval);

            // Exam Schedule :
            // this will visualize a random exam schedule from population

            // this exam schedule is for invigilators not for students
            Random rand = new Random();
            int n = rand.nextInt(populationForVisualization.size());
            HashMap<String, ArrayList<?>> randomInfo = populationForVisualization.get(n);
            ArrayList<EncodedExam> randomExamScheduleForInvigilators = Encode.encodeOperator(DataStructureHelper.castArrayList(randomInfo.get("exams"), Exam.class));
            HTMLHelper.generateExamTable(startTime, endTime, startDate, endDate, interval, randomExamScheduleForInvigilators, "Exam Schedule-" + n + " for Invigilators");

            ArrayList<EncodedExam> randomExamScheduleForStudents = new ArrayList<>();
            for (EncodedExam encodedExam : randomExamScheduleForInvigilators) {
                Course course = Course.findByCourseCode(courses, encodedExam.getCourseCode());
                if (course != null) {
                    int beforeExam = course.getBeforeExamPrepTime();
                    int afterExam = course.getAfterExamPrepTime();
                    Timeslot combinedTimeslot = encodedExam.getTimeSlot();
                    Timeslot examTimeslot = new Timeslot(combinedTimeslot.getStart().plusHours(beforeExam), combinedTimeslot.getEnd().minusHours(afterExam));
                    randomExamScheduleForStudents.add(new EncodedExam( encodedExam.getCourseCode(),
                            encodedExam.getClassroomCode(),
                            examTimeslot,
                            encodedExam.getInvigilators()));
                }
            }
            HTMLHelper.generateExamTable(startTime, endTime, startDate, endDate, interval, randomExamScheduleForStudents, "Exam Schedule-" + n + " for Students");
            HTMLHelper.generateExamTableDila(startTime, endTime, startDate, endDate, interval, randomExamScheduleForStudents, "Exam ScheduleDila-" + n + " for Students");

            // Reports that are changing : invigilators, classrooms, exam schedules
            HTMLHelper.generateInvigilatorReport(DataStructureHelper.castArrayList(randomInfo.get("invigilators"), Invigilator.class), "graphs/invigilator_report_" + n + ".html", "Invigilator Report");
            HTMLHelper.generateClassroomReport(DataStructureHelper.castArrayList(randomInfo.get("classrooms"), Classroom.class), "graphs/classroom_report_" + n + ".html", "Classroom Report");
            HTMLHelper.generateExamReport(DataStructureHelper.castArrayList(randomInfo.get("exams"), Exam.class), "graphs/exams_" + n + ".html", "Exam Schedule");
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
        ArrayList<double[]> scoresList = new ArrayList<>();
        for (ArrayList<EncodedExam> chromosome : population) {
            double[] scores = fitness.fitnessScore(chromosome);
            scoresList.add(scores);
            double fitnessScore = scores[scores.length - 1];
            fitnessScores.put(chromosome, fitnessScore);
        }

        // sort this hashmap based on fitness scores
        fitnessScores = sortByValueDescending(fitnessScores);
        // visualize
        for (ArrayList<EncodedExam> chromosome : fitnessScores.keySet()) {
            logger.info("Hashcode of Exam Schedule: " + chromosome.hashCode() + ", Score: " + fitnessScores.get(chromosome));
        }
        FileHelper.writeFitnessScoresToFile(scoresList, "graphs/fitness_scores.csv");
    }

    public void selectParents() {
        Selection selection = new Selection();
        parents = selection.rouletteWheelSelection(this.fitnessScores);
    }

    public void crossover() {
        Crossover crossover = new Crossover();
        crossover.onePointCrossover(parents);
    }
}
