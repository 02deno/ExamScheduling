package org.example.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
                logger.debug("Column Name:" + columnHeader);

                if (columnIndexMap.containsKey(columnHeader)) {
                    columnIndexMap.put(columnHeader, cell.getColumnIndex());
                }
                if (!columnIndexMap.containsValue(-1)) {
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
                            DecimalFormat decimalFormat = new DecimalFormat("#");
                            keyValue = decimalFormat.format(cell.getNumericCellValue());
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
            logger.debug("Data extracted successfully and hashmap created.");
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

    public static List<List<Double>> readFitnessScores(String filePath) {
        List<List<Double>> populations = new ArrayList<>();

        try (Reader in = new FileReader(filePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
            List<Double> currentPopulation = null;
            for (CSVRecord record : records) {
                if (record.get(0).equals("Chromosome id")) { // header
                    logger.debug("Encountered header line, time for a new generation");
                    if (currentPopulation != null) {
                        populations.add(currentPopulation);
                    }
                    currentPopulation = new ArrayList<>();
                } else if (currentPopulation != null) {
                    currentPopulation.add(Double.parseDouble(record.get(1)));
                }
            }
            if (currentPopulation != null) {
                populations.add(currentPopulation);
            }
        } catch (IOException e) {
            logger.error("File " + filePath + " not found!");
        }
        return populations;
    }

    public static List<Double> getAverage(List<List<Double>> populations) {
        List<Double> averageOfPopulations = new ArrayList<>();
        for (List<Double> population : populations) {
            int size = population.size();
            double sum = 0;
            for (Double fitnessScoreChromosom : population) {
                sum += fitnessScoreChromosom;
            }
            double average = sum / size;
            averageOfPopulations.add(average);
        }
        return averageOfPopulations;
    }

    public static List<Double> getBest(List<List<Double>> populations) {
        List<Double> bestOfPopulations = new ArrayList<>();
        for (List<Double> population : populations) {
            double best = Collections.max(population);
            bestOfPopulations.add(best);
        }
        return bestOfPopulations;
    }

    public static List<Double> getWorst(List<List<Double>> populations) {
        List<Double> worstOfPopulations = new ArrayList<>();
        for (List<Double> population : populations) {
            double worst = Collections.min(population);
            worstOfPopulations.add(worst);
        }
        return worstOfPopulations;
    }

    public static List<Double> averageFitnessScoresOfPopulations(String filePath) {
        List<List<Double>> populations = readFitnessScores(filePath);
        return getAverage(populations);
    }


    public static List<Double> bestFitnessScoresOfPopulations(String filePath) {
        List<List<Double>> populations = readFitnessScores(filePath);
        return getBest(populations);
    }

    public static List<Double> worstFitnessScoresOfPopulations(String filePath) {
        List<List<Double>> populations = readFitnessScores(filePath);
        return getWorst(populations);
    }

    public static List<List<Double>> readConstraintScores(String filePath) {
        List<List<Double>> populations = new ArrayList<>();
        try (Reader in = new FileReader(filePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
            List<Double> currentPopulation = null;
            for (CSVRecord record : records) {
                if (record.get(0).equals("Chromosome id")) { // header
                    logger.debug("Encountered header line, time for a new generation");
                    if (currentPopulation != null) {
                        populations.add(currentPopulation);
                    }
                    currentPopulation = new ArrayList<>();
                } else if (currentPopulation != null) {
                    currentPopulation.add(Double.parseDouble(record.get(record.size() - 1)));
                }
            }
            if (currentPopulation != null) {
                populations.add(currentPopulation);
            }
        } catch (IOException e) {
            logger.error("File not found!");
        }
        return populations;

    }

    public static List<Double> averageConstraintScoresOfPopulations(String filePath) {
        List<List<Double>> populations = readConstraintScores(filePath);
        return getAverage(populations);
    }


    public static List<Double> bestConstraintScoresOfPopulations(String filePath) {
        List<List<Double>> populations = readConstraintScores(filePath);
        return getBest(populations);
    }

    public static List<Double> worstConstraintScoresOfPopulations(String filePath) {
        List<List<Double>> populations = readConstraintScores(filePath);
        return getWorst(populations);
    }

}


