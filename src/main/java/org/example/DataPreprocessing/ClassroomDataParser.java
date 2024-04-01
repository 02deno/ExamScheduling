package org.example.DataPreprocessing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Utils.ExcelDataParserHelper;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDataParser {
    private String dataPath;
    private static HashMap<String, String> columnHeaderMap = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(ClassroomDataParser.class);
    private static String keyHeader;
    private static ExcelDataParserHelper excelDataParserHelper = new ExcelDataParserHelper();
    public void initializeClassroomColumnHeaderMap() {
        columnHeaderMap.put("classroomCode", "DERSLİK KODU");
        columnHeaderMap.put("classroomName", "DERSLİK ADI");
        columnHeaderMap.put("classroomCapacity", "SINAV KAPASİTESİ");
        columnHeaderMap.put("classroomProperties", "ÖZELLİKLER");
        keyHeader = "classroomCode";
    }

    public HashMap<String, ArrayList<Object>> parseClassroomData() {
        initializeClassroomColumnHeaderMap();
        return excelDataParserHelper.parseData(columnHeaderMap, dataPath, keyHeader);
    }
}
