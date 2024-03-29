package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Utils.ParseCourseData;

import java.util.HashMap;

public class App 
{
    public static void main( String[] args )
    {
        final Logger logger = LogManager.getLogger(App.class);
        logger.info("Hello World!");
        String dataPath = "data/tum_dersler.xlsx";
        ParseCourseData courseDataParser = new ParseCourseData();
        HashMap<String, String> courseData = courseDataParser.parseCourseData();
        logger.info(courseData);
    }
}
