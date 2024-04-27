package org.example.geneticAlgorithm;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dataPreprocessing.RandomDataGenerator;
import org.example.geneticAlgorithm.operators.Initialization;
import org.example.models.*;
import org.example.utils.ArraylistHelper;
import org.example.utils.ConfigHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

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
    private Schedule schedule;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private static final Logger logger = LogManager.getLogger(GeneticAlgorithm.class);


    public void generateData() {
        HashMap<String, HashMap<String, ArrayList<Object>>> randomData = RandomDataGenerator.combineAllData();
        this.courses = RandomDataGenerator.generateCourseInstances(randomData.get("courseData"));
        this.invigilators = RandomDataGenerator.generateInvigilatorInstances(randomData.get("invigilatorData"));
        this.classrooms = RandomDataGenerator.generateClassroomInstances(randomData.get("classroomData"));
        this.students = RandomDataGenerator.generateStudentInstances(randomData.get("studentData"));
        this.startDate = LocalDateTime.parse(ConfigHelper.getProperty("START_DATE_TIME"));
        this.endDate = LocalDateTime.parse(ConfigHelper.getProperty("END_DATE_TIME")); // this date is not included
        this.startTime = LocalDateTime.parse(ConfigHelper.getProperty("START_TIME"));
        this.endTime = LocalDateTime.parse(ConfigHelper.getProperty("END_TIME"));
        this.schedule = RandomDataGenerator.generateSchedule(startDate, endDate, startTime, endTime);
        this.timeslots = schedule.calculateTimeSlots();
    }

    public void initialization() {
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
    }
}
