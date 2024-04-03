package org.example.dataPreprocessing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.utils.ExcelDataParserHelper;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonDataParser {
    private String studentDataPath;
    private String invigilatorDataPath;
    private static HashMap<String, String> studentColumnHeaderMap = new HashMap<>();
    private static HashMap<String, String> invigilatorColumnHeaderMap = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(CourseDataParser.class);
    private static String studentKeyHeader;
    private static String invigilatorKeyHeader;
    private static ExcelDataParserHelper excelDataParserHelper = new ExcelDataParserHelper();
    public void initializeStudentColumnHeaderMap() {
        studentColumnHeaderMap.put("studentID", "School ID");
        studentColumnHeaderMap.put("studentName", "Name");
        studentColumnHeaderMap.put("studentSurname", "Surname");
        studentKeyHeader = "studentID";

    }

    public void initializeInvigilatorColumnHeaderMap() {
        invigilatorColumnHeaderMap.put("invigilatorID", "Invigilator ID");
        invigilatorColumnHeaderMap.put("invigilatorName", "Name");
        invigilatorColumnHeaderMap.put("invigilatorSurname", "Surname");
        invigilatorKeyHeader = "invigilatorID";
    }

    public HashMap<String, ArrayList<Object>> parseStudentData() {
        initializeStudentColumnHeaderMap();
        return excelDataParserHelper.parseData(studentColumnHeaderMap, studentDataPath, studentKeyHeader);
    }

    public HashMap<String, ArrayList<Object>> parseInvigilatorData() {
        initializeInvigilatorColumnHeaderMap();
        return excelDataParserHelper.parseData(invigilatorColumnHeaderMap, invigilatorDataPath, invigilatorKeyHeader);
    }
}
