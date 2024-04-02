package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.DataPreprocessing.RandomDataGenerator;
import org.example.Models.Course;
import org.example.Models.Invigilator;
import org.example.Utils.ArraylistHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class App
{
    public static void main( String[] args )
    {
        final Logger logger = LogManager.getLogger(App.class);
        logger.info("Application started...");
        HashMap<String, HashMap<String, ArrayList<Object>>> randomData = RandomDataGenerator.combineAllData();
        ArrayList<Course> courses = RandomDataGenerator.generateCourseInstances(randomData.get("courseData"));
        ArrayList<Invigilator> invigilators = RandomDataGenerator.generateInvigilatorInstances(randomData.get("invigilatorData"));
        HashMap<String, ArrayList<?>> result = RandomDataGenerator.mapInvigilatorsWithCourses(courses, invigilators);
        courses = ArraylistHelper.castArrayList(result.get("courses"), Course.class);
        invigilators = ArraylistHelper.castArrayList(result.get("invigilators"), Invigilator.class);
        logger.info("Application finished!");
    }

}
