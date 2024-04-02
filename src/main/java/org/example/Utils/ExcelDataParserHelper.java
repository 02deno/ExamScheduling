package org.example.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class ExcelDataParserHelper {

    private static final Logger logger = LogManager.getLogger(ExcelDataParserHelper.class);

    public HashMap<String, Integer> getHeaderIndex(HashMap<String, String> columnHeaderMap, Sheet sheet) {
        HashMap<String, Integer> columnIndexMap = new HashMap<>();
        for (String header : columnHeaderMap.values()) {
            columnIndexMap.put(header, -1);
        }

        Row headerRow = sheet.getRow(0);
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                String columnHeader = cell.getStringCellValue();
                //logger.info("Column Name:" + columnHeader);

                if (columnIndexMap.containsKey(columnHeader)) {
                    columnIndexMap.put(columnHeader, cell.getColumnIndex());
                }
                if (!columnIndexMap.containsValue(-1)) {
                    // if indexes of all column headers
                    // are found stop the for loop.
                    break;
                }
            }
        }
        return columnIndexMap;
    }

    public HashMap<String, ArrayList<Object>> getCellValues(HashMap<String, Integer> columnIndexMap, HashMap<String, String> columnHeaderMap, Sheet sheet, String keyHeader) {
        HashMap<String, ArrayList<Object>> map = new HashMap<>();
        if (columnIndexMap.values().stream().noneMatch(index -> index == -1)) {
            for (Row row : sheet) {
                // Skip header row
                if (row.getRowNum() == 0) {
                    continue;
                }

                String keyValue = null;
                ArrayList<Object> info = new ArrayList<>();
                for (HashMap.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
                    Cell cell = row.getCell(entry.getValue());
                    if (cell == null) {
                        break;
                    }
                    if (entry.getKey().equals(columnHeaderMap.get(keyHeader))) {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            keyValue = String.valueOf(BigDecimal.valueOf(cell.getNumericCellValue()));
                        } else {
                            keyValue = cell.getStringCellValue();
                        }
                    } else {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            info.add((int) cell.getNumericCellValue());
                        } else {
                            info.add(cell.getStringCellValue());
                        }
                    }
                }
                if (keyValue != null && info.size() == columnHeaderMap.size() - 1) {
                    map.put(keyValue, info);
                }
            }
            logger.info("Data extracted successfully and hashmap created.");
        } else {
            logger.error("Column headers not found.");
        }
        return map;
    }

    public HashMap<String, ArrayList<Object>> parseData(HashMap<String, String> columnHeaderMap, String dataPath, String keyHeader) {
        HashMap<String, ArrayList<Object>> map = null;
        try (FileInputStream file = new FileInputStream(dataPath);
             Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            HashMap<String, Integer> columnIndexMap = getHeaderIndex(columnHeaderMap, sheet);
            map = getCellValues(columnIndexMap, columnHeaderMap, sheet, keyHeader);
        } catch (IOException e) {
            logger.error("An error occurred while reading the Excel file.", e);
        }
        return map;
    }
}

