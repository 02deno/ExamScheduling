package org.example.utils;

import org.example.models.Classroom;
import org.example.models.Course;
import org.example.models.Student;

import java.util.ArrayList;

public class VisualizationHelper {
    public static void generateReports(ArrayList<Course> courses, ArrayList<Student> students, ArrayList<Classroom> classrooms, int interval) {
        // Reports that are always the same for exam schedules : students, courses, timeslots
        String baseFileName = "graphs/GeneralInformation/";
        FileHelper.createDirectory(baseFileName);

        HTMLHelper.generateStudentReport(students, baseFileName + "students_report.html", "Assigned Students Report");
        HTMLHelper.generateCourseReport(courses, baseFileName + "courses_report.html", "Assigned Courses Report");


        // Histogram - course capacities and timeslot counts
        ArrayList<Integer> courseCapacities = new ArrayList<>();
        ArrayList<Integer> timeslotCounts = new ArrayList<>();
        for (Course course : courses) {
            int capacity = course.getRegisteredStudents().size();
            courseCapacities.add(capacity);
            int requiredTimeslotCount = (course.getBeforeExamPrepTime() + course.getExamDuration() + course.getAfterExamPrepTime());
            timeslotCounts.add(requiredTimeslotCount);
        }
        // this histogram is made to see distribution and decide how many invigilators need to observe the exams
        HTMLHelper.generateHistogram(courseCapacities, baseFileName + "courseCapacityHistogram.html", "The number of students in courses");
        HTMLHelper.generateHistogram(timeslotCounts, baseFileName + "requiredTimeslotHistogram.html", "Timeslot histogram in Hour");

        // Histogram - classroom capacities
        ArrayList<Integer> classroomCapacities = new ArrayList<>();
        for (Classroom classroom : classrooms) {
            int capacity = classroom.getCapacity();
            classroomCapacities.add(capacity);
        }
        HTMLHelper.generateHistogram(classroomCapacities, baseFileName + "classroomCapacityHistogram.html", "Classroom Capacity");

    }


}
