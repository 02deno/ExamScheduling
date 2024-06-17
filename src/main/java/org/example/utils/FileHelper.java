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
        if (!folder.exists()) {
            logger.debug("Folder does not exist!!");
            return;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            logger.debug("Graphs folder is already empty");
            return;
        }

        boolean allFilesDeleted = true;

        for (File file : files) {
            if (file.isDirectory()) {
                deleteFolderContents(file);
                if (!file.delete()) {
                    allFilesDeleted = false;
                }
            } else if (!file.getName().equals(".gitkeep")) {
                if (!file.delete()) {
                    allFilesDeleted = false;
                }
            }
        }

        if (allFilesDeleted) {
            logger.debug("Folder contents are deleted successfully :)");
        } else {
            logger.error("Some error occurred during folder deletion");
        }
    }

    public static void createDirectory(String baseFileName) {

        File directory = new File(baseFileName);

        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (success) {
                logger.debug("Directory created: " + baseFileName);
            } else {
                logger.error("Failed to create directory: " + baseFileName);
            }
        } else {
            logger.debug("Directory already exists: " + baseFileName);
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
        writeHeaderRow(header, writer);
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
        logger.debug("Rows appended to CSV file: " + filePath);
    }

    private static void writeHeaderRow(String[] header, FileWriter writer) throws IOException {
        for (int i = 0; i < header.length; i++) {
            writer.write(header[i]);
            // Add comma if it's not the last header element
            if (i < header.length - 1) {
                writer.write(",");
            }
        }
        writer.write("\n");

    }

    public static void writeFitnessScoresToFile(ArrayList<Double> scoresList, String filePath) {
        String[] header = {"Exam id", "fitnessScore"};

        try (FileWriter writer = new FileWriter(filePath, true)) {
            writeHeaderRow(header, writer);
            int id = 1;
            for (double row : scoresList) {
                writer.write(Integer.toString(id++));
                writer.write(",");
                // Write scores as a new row, separated by commas
                writer.write(Double.toString(row));
                // Add new line after each row
                writer.write("\n");
            }
            logger.debug("Rows appended to CSV file: " + filePath);

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