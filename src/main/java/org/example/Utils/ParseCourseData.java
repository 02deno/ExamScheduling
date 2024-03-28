package org.example.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class ParseCourseData {
    private String dataPath;

    private static final Logger logger = LogManager.getLogger(ParseCourseData.class);

    public HashMap<String, String> parseCourseData(String dataPath) {
        HashMap<String, String> courseMap = new HashMap<>();
        String keyColumnHeader = "Ders Kodu";
        String valueColumnHeader = "Ders Adı";

        try {
            FileInputStream file = new FileInputStream(dataPath);
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);

            int keyColumnIndex = -1;
            int valueColumnIndex = -1;
            Row headerRow = sheet.getRow(0);
            for (Cell cell : headerRow) {
                String columnHeader = cell.getStringCellValue();
                if (columnHeader.equals(keyColumnHeader)) {
                    keyColumnIndex = cell.getColumnIndex();
                } else if (columnHeader.equals(valueColumnHeader)) {
                    valueColumnIndex = cell.getColumnIndex();
                }
            }

            if (keyColumnIndex != -1 && valueColumnIndex != -1) {
                for (Row row : sheet) {
                    // Skip header row
                    if (row.getRowNum() == 0) {
                        continue;
                    }
                    Cell keyCell = row.getCell(keyColumnIndex);
                    Cell valueCell = row.getCell(valueColumnIndex);
                    if (keyCell != null && valueCell != null) {
                        String key = keyCell.getStringCellValue();
                        String value = valueCell.getStringCellValue();
                        courseMap.put(key, value);
                    }
                }
                //logger.info("HashMap: " + courseMap);
                logger.info("Course data extracted successfully and hashmap created.");
            } else {
                logger.error("Column headers not found.");
            }

            workbook.close();
            file.close();
        } catch (IOException e) {
            logger.error("An error occurred while reading the Excel file.", e);
        }

        return courseMap;
    }


}
