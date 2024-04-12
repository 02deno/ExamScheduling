package org.example.geneticAlgorithm;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dataPreprocessing.RandomDataGenerator;
import org.example.geneticAlgorithm.operators.Initialization;
import org.example.models.*;
import org.example.utils.ArraylistHelper;
import org.example.utils.ConfigHelper;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

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
        HashMap<String, ArrayList<?>> resultCoursesInvigilators = Initialization.heuristicMapCoursesWithInvigilators(this.courses, this.invigilators);
        this.courses = ArraylistHelper.castArrayList(resultCoursesInvigilators.get("courses"), Course.class);
        this.invigilators = ArraylistHelper.castArrayList(resultCoursesInvigilators.get("invigilators"), Invigilator.class);

        HashMap<String, ArrayList<?>> resultCoursesClassrooms = Initialization.heuristicMapCoursesWithClassrooms(this.courses, this.classrooms);
        this.courses = ArraylistHelper.castArrayList(resultCoursesClassrooms.get("courses"), Course.class);
        this.classrooms = ArraylistHelper.castArrayList(resultCoursesClassrooms.get("classrooms"), Classroom.class);

        HashMap<String, ArrayList<?>> resultCoursesStudents = Initialization.heuristicMapCoursesWithStudents(this.courses, this.students);
        this.courses = ArraylistHelper.castArrayList(resultCoursesStudents.get("courses"), Course.class);
        this.students = ArraylistHelper.castArrayList(resultCoursesStudents.get("students"), Student.class);

        HashMap<String, ArrayList<?>> resultCoursesTimeslots = Initialization.randomMapCoursesWithTimeslots(this.courses, this.timeslots);
        this.courses = ArraylistHelper.castArrayList(resultCoursesTimeslots.get("courses"), Course.class);
    }
}
