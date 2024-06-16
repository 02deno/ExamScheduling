package org.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FileHelper {

    private static final Logger logger = LogManager.getLogger(FileHelper.class);
    public static final String holidayFilePath = "data/holidays.json";

    public static void deleteFolderContents(File folder) {
        boolean result = false;
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteFolderContents(file);
                    } else if (!file.getName().equals(".gitkeep")) {
                        result = file.delete();
                    }
                }
            } else {
                logger.info("Graphs folder is already empty");
                return;
            }
        }

        if (result) {
            logger.info("Folder contents is deleted successfully :)");
        } else {
            logger.error("Some error occurred during folder deletion");
        }
    }

    public static void writeHardFitnessScoresToFile(ArrayList<double[]> scoresList, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            String[] header = {"Exam id", "allExamsHaveRequiredTime", "allExamHaveRequiredInvigilatorCount", "classroomOverlapped",
                    "allExamsHaveClassrooms", "classroomsHasCapacity", "invigilatorOverlapped",
                    "studentOverlapped", "invigilatorAvailable", "startAndEndTimeDateViolated",
                    "allExamsHaveRequiredEquipments", "noExamsHolidays", "examStartAndEndDateSame",
                    "fitnessScore"};
            fitnessTableGenerator(scoresList, filePath, header, writer);
        } catch (IOException e) {
            logger.error("Error appending rows to CSV file: " + e.getMessage());
        }

    }

    public static void writeSoftFitnessScoresToFile(ArrayList<double[]> scoresList, String filePath) {

        String[] header = {"Exam id", "studentMoreThanTwoExamSameDay",
                "minimumGapBetweenExamsStudent", "invigilatorMoreThanThreeExamSameDay",
                "minimumGapBetweenExamsInvigilator", "noExamsAtWeekends", "examsNotInAfternoon",
                "popularExamsAtBeginning",
                "fitnessScore"};

        try (FileWriter writer = new FileWriter(filePath, true)) {
            fitnessTableGenerator(scoresList, filePath, header, writer);
        } catch (IOException e) {
            logger.error("Error appending rows to CSV file: " + e.getMessage());
        }

    }

    private static void fitnessTableGenerator(ArrayList<double[]> scoresList, String filePath, String[] header, FileWriter writer) throws IOException {
        for (int i = 0; i < header.length; i++) {
            writer.write(header[i]);
            // Add comma if it's not the last header element
            if (i < header.length - 1) {
                writer.write(",");
            }
        }
        writer.write("\n");
        int id = 1;
        for (double[] row : scoresList) {
            writer.write(Integer.toString(id++));
            writer.write(",");
            // Write scores as a new row, separated by commas
            for (int i = 0; i < row.length; i++) {
                writer.write(Double.toString(row[i]));
                // Add comma if it's not the last score
                if (i < row.length - 1) {
                    writer.write(",");
                }
            }
            // Add new line after each row
            writer.write("\n");
        }
        logger.info("Rows appended to CSV file: " + filePath);
    }

    public static void writeFitnessScoresToFile(ArrayList<Double> scoresList, String filePath) {
        String[] header = {"Exam id", "fitnessScore"};

        try (FileWriter writer = new FileWriter(filePath, true)) {

            for (int i = 0; i < header.length; i++) {
                writer.write(header[i]);
                // Add comma if it's not the last header element
                if (i < header.length - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");
            int id = 1;
            for (double row : scoresList) {
                writer.write(Integer.toString(id++));
                writer.write(",");
                // Write scores as a new row, separated by commas
                writer.write(Double.toString(row));
                // Add new line after each row
                writer.write("\n");
            }
            logger.info("Rows appended to CSV file: " + filePath);

        } catch (IOException e) {
            logger.error("Error appending rows to CSV file: " + e.getMessage());
        }
    }

    public static void saveHolidaysToFile() {
        Set<LocalDate> holidays = APIHelper.fetchHolidays();
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode holidaysArray = mapper.createArrayNode();

        for (LocalDate holiday : holidays) {
            holidaysArray.add(holiday.toString());
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(holidayFilePath), holidaysArray);
        } catch (IOException e) {
            logger.error("Error saving holidays to file: " + e.getMessage());
        }
    }

    public static Set<LocalDate> loadHolidaysFromFile() {
        Set<LocalDate> holidays = new HashSet<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(holidayFilePath);
            if (file.exists()) {
                JsonNode holidaysArray = mapper.readTree(file);

                for (JsonNode dateNode : holidaysArray) {
                    holidays.add(LocalDate.parse(dateNode.asText()));
                }
            }
        } catch (IOException e) {
            logger.error("Error loading holidays from file: " + e.getMessage());
        }

        return holidays;
    }

}