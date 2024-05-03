package org.example.geneticAlgorithm;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dataPreprocessing.RandomDataGenerator;
import org.example.geneticAlgorithm.operators.Encode;
import org.example.geneticAlgorithm.operators.Initialization;
import org.example.models.*;
import org.example.utils.ArraylistHelper;
import org.example.utils.ConfigHelper;
import org.example.utils.HTMLHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class GeneticAlgorithm {

    private ArrayList<Course> courses = new ArrayList<>();
    private ArrayList<Invigilator> invigilators = new ArrayList<>();
    private ArrayList<Classroom> classrooms = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Timeslot> timeslots = new ArrayList<>();
    private ArrayList<Exam> exams = new ArrayList<>();
    private ArrayList<EncodedExam> encodedExams = new ArrayList<>();
    private HashMap<String, ArrayList<?>> chromosome = new HashMap<>();
    private ArrayList<HashMap<String, ArrayList<?>>> population = new ArrayList<>();
    private Schedule schedule;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);


    public void generateData() {
        HashMap<String, HashMap<String, ArrayList<Object>>> randomData = RandomDataGenerator.combineAllData();
        this.courses = RandomDataGenerator.generateCourseInstances(randomData.get("courseData"));
        this.invigilators = RandomDataGenerator.generateInvigilatorInstances(randomData.get("invigilatorData"));
        this.classrooms = RandomDataGenerator.generateClassroomInstances(randomData.get("classroomData"));
        this.students = RandomDataGenerator.generateStudentInstances(randomData.get("studentData"));
        this.startDate = LocalDate.parse(ConfigHelper.getProperty("START_DATE"));
        this.endDate = LocalDate.parse(ConfigHelper.getProperty("END_DATE")); // this date is not included
        this.startTime = LocalTime.parse(ConfigHelper.getProperty("START_TIME"));
        this.endTime = LocalTime.parse(ConfigHelper.getProperty("END_TIME"));
        this.schedule = RandomDataGenerator.generateSchedule(startDate, endDate, startTime, endTime);
        this.timeslots = schedule.calculateTimeSlots();
    }

    public void initialization() {
        generateData();
        int populationSize = Integer.parseInt(ConfigHelper.getProperty("POPULATION_SIZE"));
        for (int i = 0; i < populationSize; i++) {
            HashMap<String, ArrayList<?>> resultCoursesStudents = Initialization.heuristicMapCoursesWithStudents(this.courses, this.students);
            this.courses = ArraylistHelper.castArrayList(resultCoursesStudents.get("courses"), Course.class);
            this.students = ArraylistHelper.castArrayList(resultCoursesStudents.get("students"), Student.class);
            logger.info("heuristicMapCoursesWithStudents finished.");

            HashMap<String, ArrayList<?>> resultExams = Initialization.createExamInstances(this.courses);
            this.exams = ArraylistHelper.castArrayList(resultExams.get("exams"), Exam.class);
            logger.info(" createExamInstances finished.");

            HashMap<String, ArrayList<?>> resultCoursesInvigilators = Initialization.heuristicMapExamsWithInvigilators(this.exams, this.invigilators);
            this.exams = ArraylistHelper.castArrayList(resultCoursesInvigilators.get("exams"), Exam.class);
            this.invigilators = ArraylistHelper.castArrayList(resultCoursesInvigilators.get("invigilators"), Invigilator.class);
            logger.info("heuristicMapExamsWithInvigilators finished.");

            HashMap<String, ArrayList<?>> resultCoursesClassrooms = Initialization.heuristicMapExamsWithClassrooms(this.exams, this.classrooms);
            this.exams = ArraylistHelper.castArrayList(resultCoursesClassrooms.get("exams"), Exam.class);
            this.classrooms = ArraylistHelper.castArrayList(resultCoursesClassrooms.get("classrooms"), Classroom.class);
            logger.info("heuristicMapExamsWithClassrooms finished.");

            HashMap<String, ArrayList<?>> resultCoursesTimeslots = Initialization.heuristicMapExamsWithTimeslots(this.exams, this.timeslots);
            this.exams = ArraylistHelper.castArrayList(resultCoursesTimeslots.get("exams"), Exam.class);
            logger.info("heuristicMapExamsWithTimeslots finished.");

            this.chromosome.put("courses", courses);
            this.chromosome.put("students", students);
            this.chromosome.put("exams", exams);
            this.chromosome.put("invigilators", invigilators);
            this.chromosome.put("classrooms", classrooms);
            this.population.add(chromosome);
            //visualization();
            generateData();
        }
        //logger.info(population);
    }

    public void encode() {
        this.encodedExams = Encode.encodeOperator(this.exams);
    }

    public void visualization() {
        // this will visualize a random exam schedule from population
        Random rand = new Random();
        logger.info("Population Size " + population.size());
        int n = rand.nextInt(population.size());
        ArrayList<Exam> randomExamSchedule = ArraylistHelper.castArrayList(this.population.get(n).get("exams"), Exam.class);
        HTMLHelper.generateExamTable(startTime, endTime, startDate, endDate, randomExamSchedule);
        logger.info("Exam Table with random UUID is generated.");
    }
}
