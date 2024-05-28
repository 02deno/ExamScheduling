package org.example.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHelper {

    private static final Logger logger = LogManager.getLogger(FileHelper.class);

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

    public static void writeFitnessScoresToFile(ArrayList<double[]> scoresList, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            String[] header = {"checkCourseExamCompatibility", "classroomOverlapped", "allExamsHaveClassrooms",
                    "classroomsHasCapacity", "invigilatorOverlapped", "studentOverlapped", "invigilatorAvailable",
                    "fitnessScore"};
            for (int i = 0; i < header.length; i++) {
                writer.write(header[i]);
                // Add comma if it's not the last header element
                if (i < header.length - 1) {
                    writer.write(",");
                }
            }
            writer.write("\n");
            // Write each row of scores
            for (double[] row : scoresList) {
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
            System.out.println("Rows appended to CSV file: " + filePath);
        } catch (IOException e) {
            System.err.println("Error appending rows to CSV file: " + e.getMessage());
        }
    }

}