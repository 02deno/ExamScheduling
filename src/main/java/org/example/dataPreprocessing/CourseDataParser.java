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
public class CourseDataParser {
    private String dataPath;
    private static HashMap<String, String> columnHeaderMap = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(CourseDataParser.class);
    private static String keyHeader;
    private static ExcelDataParserHelper excelDataParserHelper = new ExcelDataParserHelper();
    public void initializeCourseColumnHeaderMap() {
        columnHeaderMap.put("classCode", "Ders Kodu");
        columnHeaderMap.put("className", "Ders Adı");
        columnHeaderMap.put("examDuration", "Sınav Süresi (Slot Sayısı)");
        columnHeaderMap.put("beforeExamPrep", "Gözetmenlik Öncesi Boşluk Süresi (Slot Sayısı)");
        columnHeaderMap.put("afterExamPrep", "Gözetmenlik Sonrası Boşluk Süresi (Slot Sayısı)");
        keyHeader = "classCode";
    }

    public HashMap<String, ArrayList<Object>> parseCourseData() {
        initializeCourseColumnHeaderMap();
        return excelDataParserHelper.parseData(columnHeaderMap, dataPath, keyHeader);
    }


}
