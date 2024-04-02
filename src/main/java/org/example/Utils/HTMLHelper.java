package org.example.Utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class HTMLHelper {

    private static final Logger logger = LogManager.getLogger(HTMLHelper.class);
    public static void generateHistogram(ArrayList<Integer> list, String outputFilePath, String title) {
        HashMap<Integer, Integer> frequencyMap = new HashMap<>();
        for (Integer num : list) {
            frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
        }

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html>");
        htmlContent.append("<head>");
        htmlContent.append("<title>Histogram of").append(title).append("</title>");
        htmlContent.append("<script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>");
        htmlContent.append("</head>");
        htmlContent.append("<body>");
        htmlContent.append("<div id=\"plot\"></div>");
        htmlContent.append("<script>");
        htmlContent.append("var data = [{");
        htmlContent.append("x: [");
        boolean first = true;
        for (HashMap.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (!first)
                htmlContent.append(",");
            htmlContent.append("'").append(entry.getKey()).append("'");
            first = false;
        }
        htmlContent.append("],");
        htmlContent.append("y: [");
        first = true;
        for (HashMap.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (!first)
                htmlContent.append(",");
            htmlContent.append(entry.getValue());
            first = false;
        }
        htmlContent.append("],");
        htmlContent.append("type: 'bar'");
        htmlContent.append("}];");
        htmlContent.append("var layout = {");
        htmlContent.append("title: 'Histogram of ").append(title).append("',");
        htmlContent.append("xaxis: {title: '").append(title).append("'},");
        htmlContent.append("yaxis: {title: 'Frequency'}");
        htmlContent.append("};");
        htmlContent.append("Plotly.newPlot('plot', data, layout);");
        htmlContent.append("</script>");
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        // Write HTML content to file
        try {
            FileWriter writer = new FileWriter(new File(outputFilePath));
            writer.write(htmlContent.toString());
            writer.close();
            System.out.println("Histogram saved as HTML file: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error writing HTML file: " + e.getMessage());
        }
    }

    public static void generateReport(ArrayList<?> data, String outputFilePath, String reportTitle, String[] headers, String[] fields) {
        StringBuilder htmlContent = new StringBuilder();

        // HTML head
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"en\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlContent.append("<title>").append(reportTitle).append("</title>");
        htmlContent.append("<style>");
        // CSS styles
        htmlContent.append("table { border-collapse: collapse; width: 100%; }");
        htmlContent.append("th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }");
        htmlContent.append("tr:hover { background-color: #f5f5f5; }");
        htmlContent.append("th { background-color: #4CAF50; color: white; }");
        htmlContent.append("</style>");
        htmlContent.append("</head>");
        htmlContent.append("<body>");

        // Report title
        htmlContent.append("<h1>").append(reportTitle).append("</h1>");

        // Table to display data
        htmlContent.append("<table>");
        htmlContent.append("<tr>");
        for (String header : headers) {
            htmlContent.append("<th>").append(header).append("</th>");
        }
        htmlContent.append("</tr>");

        // Iterate over data and add rows to the table
        for (int i = 0; i < data.size(); i++) {
            htmlContent.append("<tr");
            if (i % 2 == 0) {
                htmlContent.append(" style=\"background-color: #f2f2f2;\"");
            }
            htmlContent.append(">");
            Object obj = data.get(i);
            for (String field : fields) {
                try {
                    // Construct method name based on field
                    String methodName;
                    if (field.startsWith("is")) {
                        methodName = field;
                    } else {
                        methodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
                    }
                    // Invoke method dynamically
                    Method method = obj.getClass().getMethod(methodName);
                    Object value = method.invoke(obj);
                    htmlContent.append("<td>").append(value).append("</td>");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            htmlContent.append("</tr>");
        }

        htmlContent.append("</table>");

        // HTML end
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        // Write HTML content to file
        try {
            FileWriter writer = new FileWriter(outputFilePath);
            writer.write(htmlContent.toString());
            writer.close();
            System.out.println("Report saved as HTML file: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error writing HTML file: " + e.getMessage());
        }
    }
}
