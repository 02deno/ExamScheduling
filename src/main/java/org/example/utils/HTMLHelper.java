package org.example.utils;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.geneticAlgorithm.operators.Fitness;
import org.example.models.*;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
            FileWriter writer = new FileWriter(outputFilePath);
            writer.write(htmlContent.toString());
            writer.close();
            logger.debug("Histogram saved as HTML file: " + outputFilePath);
        } catch (IOException e) {
            logger.error("Error writing HTML file: " + e.getMessage());
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
                    logger.error("An error occurred while creating html report.", e);
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
            logger.debug("Report saved as HTML file: " + outputFilePath);
        } catch (IOException e) {
            logger.error("Error writing HTML file: " + e.getMessage());
        }
        logger.debug("Exam Table is generated.");
    }

    public static void generateInvigilatorReport(ArrayList<Invigilator> invigilators, String output, String title) {
        HTMLHelper.generateReport(invigilators, output, title,
                new String[]{"ID", "Name", "Surname", "Maximum Number of Courses to Monitor", "Monitored Course IDs", "Available"},
                new String[]{"ID", "name", "surname", "maxCoursesMonitoredCount", "monitoredExams", "isAvailable"});
    }

    public static void generateClassroomReport(ArrayList<Classroom> classrooms, String output, String title) {
        HTMLHelper.generateReport(classrooms, output, title,
                new String[]{"Code", "Name", "Capacity(#Studens)", "PC Lab", "Properties", "Course Codes"},
                new String[]{"classroomCode", "classroomName", "capacity", "isPcLab", "classroomProperties", "placedExams"});
    }
    public static void generateCourseReport(ArrayList<Course> courses, String output, String title) {
        HTMLHelper.generateReport(courses, output, title,
                new String[]{"Course Code", "Course Name", "Is PC Exam", "Student Capacity", "Remaining Student Capacity", "Registered Student IDs", "Before Exam", "Exam Duration", "After Exam"},
                new String[]{"courseCode", "courseName", "isPcExam", "studentCapacity", "remainingStudentCapacity", "registeredStudents", "beforeExamPrepTime", "examDuration", "afterExamPrepTime"});
    }

    public static void generateStudentReport(ArrayList<Student> students, String output, String title) {
        HTMLHelper.generateReport(students, output, title,
                new String[]{"Student ID", "Name", "Surname", "Max Number of Courses to take", "Registered Course Codes", "Remaining Course Capacity"},
                new String[]{"ID", "name", "surname", "maxCoursesTakenCount", "registeredCourses", "remainingCourseCapacity"});

    }

    public static void generateExamReport(ArrayList<Exam> exams, String output, String title) {
        HTMLHelper.generateReport(exams, output, title,
                new String[]{"Exam ID", "Course", "Classroom", "Exam Invigilators", "Exam Timeslot", "Combined Timeslot(before and after included)"},
                new String[]{"examCode", "course", "classroom", "examInvigilators", "examTimeslot", "combinedTimeslot"});

    }

    public static void generateExamTable(LocalTime startTime, LocalTime endTime, LocalDate startDate, LocalDate endDate, int interval, ArrayList<EncodedExam> exams, String title) {

        // Create HTML content
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"en\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlContent.append("<title>").append(title).append("</title>");
        htmlContent.append("<style>");
        htmlContent.append("table { border-collapse: collapse; width: 100%; }");
        htmlContent.append("th, td { border: 1px solid black; padding: 8px; text-align: center; }");
        htmlContent.append("th { background-color: #f2f2f2; }");
        htmlContent.append("</style>");
        htmlContent.append("<script>");
        htmlContent.append("function getContrastYIQ(hexcolor){var r=parseInt(hexcolor.substr(1,2),16),g=parseInt(hexcolor.substr(3,2),16),b=parseInt(hexcolor.substr(5,2),16),yiq=(r*299+g*587+b*114)/1000;return(yiq>=128)?'black':'white';}");
        htmlContent.append("</script>");
        htmlContent.append("</head><body>");
        htmlContent.append("<h1>").append(title).append("</h1>");
        htmlContent.append("<table>");
        htmlContent.append("<tr><th>Time Interval</th>");

        LocalDate currentDate = startDate;
        while (!currentDate.isEqual(endDate)) {
            htmlContent.append("<th>").append(currentDate.getDayOfWeek()).append("<br>").append(currentDate).append("</th>");
            currentDate = currentDate.plusDays(1);
        }
        htmlContent.append("</tr>");

        Map<String, String> courseCodeColors = new HashMap<>();

        LocalTime currentTime = startTime;
        while (!currentTime.equals(endTime)) {
            htmlContent.append("<tr><td>").append(currentTime).append("-").append(currentTime.plusMinutes(interval)).append("</td>");

            // Iterate through dates
            currentDate = startDate;
            while (!currentDate.isEqual(endDate)) {
                // Check if any exams are scheduled at the current time slot
                StringBuilder courseCodesHTML = new StringBuilder();
                Set<String> uniqueCourseCodes = new HashSet<>(); // To keep track of unique course codes in the time slot
                for (EncodedExam exam : exams) {
                    if (isInTimeInterval(exam.getTimeSlot().getStart(), exam.getTimeSlot().getEnd(), currentDate, currentTime, interval)) {
                        String courseCode = exam.getCourseCode();
                        uniqueCourseCodes.add(courseCode);
                    }
                }

                for (String courseCode : uniqueCourseCodes) {
                    // Get color for course code
                    String color = courseCodeColors.computeIfAbsent(courseCode, key -> generateColor(courseCode));
                    String textColor = getContrastColor(color); // Get contrast text color
                    courseCodesHTML.append("<span style=\"background-color: ").append(color).append("; color: ").append(textColor).append(";\">")
                            .append(courseCode).append("</span><br>");
                }

                htmlContent.append("<td>").append(courseCodesHTML).append("</td>");

                currentDate = currentDate.plusDays(1);
            }
            htmlContent.append("</tr>");
            currentTime = currentTime.plusMinutes(interval);
        }

        // Close HTML tags
        htmlContent.append("</table></body></html>");

        htmlContent.append("</br>");
        htmlContent.append("</br>");
        htmlContent.append("</br>");

        htmlContent.append("<html><head><title>Courses and Classrooms</title>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<style>");
        htmlContent.append(".container { display: flex; justify-content: space-between; }");
        htmlContent.append(".table { border-collapse: collapse; width: 45%; }");
        htmlContent.append("th, td { border: 1px solid black; padding: 8px; text-align: center; }");
        htmlContent.append("th { background-color: #f2f2f2; }");
        htmlContent.append("</style>");
        htmlContent.append("</head><body>");
        htmlContent.append("<div class=\"container\">");
        htmlContent.append("<table class=\"table\">");
        htmlContent.append("<tr><th>Course Code</th><th>Classroom</th></tr>");

        for (EncodedExam exam : exams) {
            String courseCode = StringEscapeUtils.escapeHtml4(exam.getCourseCode());
            String classroom = StringEscapeUtils.escapeHtml4(exam.getClassroomCode());
            String color = courseCodeColors.computeIfAbsent(courseCode, key -> generateColor(courseCode));
            String textColor = getContrastColor(color);
            htmlContent.append("<tr><td><span class=\"course-code\" style=\"background-color: ").append(color).append("; color: ").append(textColor).append(";\">")
                    .append(courseCode).append("</span></td><td>").append(classroom).append("</td></tr>");
        }

        htmlContent.append("</table>");
        htmlContent.append("</div>");
        htmlContent.append("</body></html>");

        htmlContent.append("</table></body></html>");

        try (FileWriter fileWriter = new FileWriter(title)) {
            fileWriter.write(htmlContent.toString());
            logger.debug("HTML exam schedule generated successfully. File saved as " + title);
        } catch (IOException e) {
            logger.error("An error occurred while writing the HTML file: " + e.getMessage());
        }

    }

    public static void generateExamTableDila(LocalDate startDate, LocalDate endDate, ArrayList<EncodedExam> exams, String title) {

        // Create HTML content
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>");
        htmlContent.append("<html lang=\"en\">");
        htmlContent.append("<head>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        htmlContent.append("<title>").append(title).append("</title>");
        htmlContent.append("<style>");
        htmlContent.append("table { border-collapse: collapse; width: 100%; }");
        htmlContent.append("th, td { border: 1px solid black; padding: 8px; text-align: center; }");
        htmlContent.append("th { background-color: #f2f2f2; }");
        htmlContent.append("</style>");
        htmlContent.append("<script>");
        htmlContent.append("function getContrastYIQ(hexcolor){var r=parseInt(hexcolor.substr(1,2),16),g=parseInt(hexcolor.substr(3,2),16),b=parseInt(hexcolor.substr(5,2),16),yiq=(r*299+g*587+b*114)/1000;return(yiq>=128)?'black':'white';}");
        htmlContent.append("</script>");
        htmlContent.append("</head><body>");
        htmlContent.append("<h1>").append(title).append("</h1>");
        htmlContent.append("<table>");
        htmlContent.append("<tr><th>Course Codes</th>");

        LocalDate currentDate = startDate;
        while (!currentDate.isEqual(endDate)) {
            htmlContent.append("<th>").append(currentDate.getDayOfWeek()).append("<br>").append(currentDate).append("</th>");
            currentDate = currentDate.plusDays(1);
        }
        htmlContent.append("</tr>");

        StringBuilder courseCodesHTML = new StringBuilder();
        Comparator<EncodedExam> comparator = EncodedExam.sortExamsByCourseCode();
        exams.sort(comparator);

        Timeslot timeslot;
        String classroomCode;
        for (EncodedExam exam : exams) {
            String courseCode = exam.getCourseCode();
            String color = generateColor(courseCode);
            String textColor = getContrastColor(color); // Get contrast text color
            courseCodesHTML.append("<span style=\"background-color: ").append(color).append("; color: ").append(textColor).append(";\">")
                    .append(courseCode).append("</span><br>");
            htmlContent.append("<tr><td>").append(courseCodesHTML).append("</td>");
            courseCodesHTML.delete(0, courseCodesHTML.length());
            currentDate = startDate;
            while (!currentDate.isEqual(endDate)) {
                if (exam.getTimeSlot().toString().contains(currentDate.toString())) {
                    timeslot = exam.getTimeSlot();
                    classroomCode = exam.getClassroomCode();
                    htmlContent.append("<td>").append(timeslot.getStart().toLocalTime()).append("-").append(timeslot.getEnd().toLocalTime()).append("<br>").append(classroomCode).append("<br>").append(exam.getInvigilators()).append("</td>");
                } else {
                    htmlContent.append("<td>").append("</td>");
                }

                currentDate = currentDate.plusDays(1);
            }
            htmlContent.append("</tr>");
        }


        // Close HTML tags
        htmlContent.append("</table></body></html>");

        htmlContent.append("</br>");
        htmlContent.append("</br>");
        htmlContent.append("</br>");

        htmlContent.append("<html><head><title>Courses and Classrooms</title>");
        htmlContent.append("<meta charset=\"UTF-8\">");
        htmlContent.append("<style>");
        htmlContent.append(".container { display: flex; justify-content: space-between; }");
        htmlContent.append(".table { border-collapse: collapse; width: 45%; }");
        htmlContent.append("th, td { border: 1px solid black; padding: 8px; text-align: center; }");
        htmlContent.append("th { background-color: #f2f2f2; }");
        htmlContent.append("</style>");
        htmlContent.append("</head><body>");
        htmlContent.append("<div class=\"container\">");
        htmlContent.append("<table class=\"table\">");
        htmlContent.append("</table>");
        htmlContent.append("</div>");
        htmlContent.append("</body></html>");

        htmlContent.append("</table></body></html>");

        try (FileWriter fileWriter = new FileWriter(title)) {
            fileWriter.write(htmlContent.toString());
            logger.debug("HTML exam schedule generated successfully. File saved as " + title);
        } catch (IOException e) {
            logger.error("An error occurred while writing the HTML file: " + e.getMessage());
        }

    }

    private static boolean isInTimeInterval(LocalDateTime startTime, LocalDateTime endTime, LocalDate date, LocalTime time, int interval) {
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        LocalDateTime intervalEnd = LocalDateTime.of(date, time.plusMinutes(interval));
        return !dateTime.isBefore(startTime) && !intervalEnd.isAfter(endTime);
    }

    private static String generateColor(String courseCode) {
        // Generate a color based on the hash code of the course code
        int hash = courseCode.hashCode();
        Random random = new Random(hash);
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;

        // Add randomness
        r = (r + random.nextInt(256)) / 2;
        g = (g + random.nextInt(256)) / 2;
        b = (b + random.nextInt(256)) / 2;

        return String.format("#%02X%02X%02X", r, g, b);
    }

    private static String getContrastColor(String hexColor) {
        // Calculate the YIQ brightness of the color and return the
        // background color black or white accordingly
        // to accomplish visibility
        int r = Integer.valueOf(hexColor.substring(1, 3), 16);
        int g = Integer.valueOf(hexColor.substring(3, 5), 16);
        int b = Integer.valueOf(hexColor.substring(5, 7), 16);
        double yiq = (double) ((r * 299) + (g * 587) + (b * 114)) / 1000;
        return (yiq >= 128) ? "#000000" : "#FFFFFF";
    }

    public static void generateLinePlot(List<Double> averageFitnessScores, String title, String path) {

        StringBuilder xValues = new StringBuilder("[");
        StringBuilder yValues = new StringBuilder("[");

        for (int i = 0; i < averageFitnessScores.size(); i++) {
            xValues.append(i + 1);
            yValues.append(averageFitnessScores.get(i));
            if (i < averageFitnessScores.size() - 1) {
                xValues.append(", ");
                yValues.append(", ");
            }
        }
        xValues.append("]");
        yValues.append("]");

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html>");
        htmlContent.append("<head>");
        htmlContent.append("<title>").append(title).append("</title>");
        htmlContent.append("<script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>");
        htmlContent.append("<style>");
        htmlContent.append("body { font-family: Arial, sans-serif; margin: 40px; }");
        htmlContent.append("#plot { width: 100%; height: 100%; }");
        htmlContent.append("</style>");
        htmlContent.append("</head>");
        htmlContent.append("<body>");
        htmlContent.append("<div id=\"plot\"></div>");
        htmlContent.append("<script>");
        htmlContent.append("var trace = {");
        htmlContent.append("x: ").append(xValues).append(",");
        htmlContent.append("y: ").append(yValues).append(",");
        htmlContent.append("mode: 'lines+markers',");
        htmlContent.append("type: 'scatter',");
        htmlContent.append("line: { color: 'blue', width: 2 },");
        htmlContent.append("marker: { size: 8, color: 'red' }");
        htmlContent.append("};");
        htmlContent.append("var data = [trace];");
        htmlContent.append("var layout = {");
        htmlContent.append("title: { text: '").append(title).append("', font: { size: 24 } },");
        htmlContent.append("xaxis: { title: { text: 'Population', font: { size: 18 } }, showgrid: true, zeroline: true },");
        htmlContent.append("yaxis: { title: { text: 'Fitness Score', font: { size: 18 } }, showgrid: true, zeroline: true },");
        htmlContent.append("margin: { l: 50, r: 50, b: 50, t: 50 },");
        htmlContent.append("paper_bgcolor: 'white',");
        htmlContent.append("plot_bgcolor: '#f8f8f8'");
        htmlContent.append("};");
        htmlContent.append("Plotly.newPlot('plot', data, layout);");
        htmlContent.append("</script>");
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        String baseFileName = "graphs/FitnessScores/plots/";
        FileHelper.createDirectory(baseFileName);
        try (FileWriter fileWriter = new FileWriter(baseFileName + path)) {
            fileWriter.write(htmlContent.toString());
        } catch (IOException e) {
            logger.error("Some error occurred while writing HTML file to desired path");
        }
    }

    public static void generateLinePlotAll(List<Double> averageFitnessScores,
                                           List<Double> bestFitnessScoresOfPopulations,
                                           List<Double> worstFitnessScoresOfPopulations,
                                           String title,
                                           String path) {

        StringBuilder xValues = new StringBuilder("[");
        StringBuilder yValues1 = new StringBuilder("[");
        StringBuilder yValues2 = new StringBuilder("[");
        StringBuilder yValues3 = new StringBuilder("[");

        for (int i = 0; i < averageFitnessScores.size(); i++) {
            xValues.append(i + 1);
            yValues1.append(averageFitnessScores.get(i));
            yValues2.append(bestFitnessScoresOfPopulations.get(i));
            yValues3.append(worstFitnessScoresOfPopulations.get(i));
            if (i < averageFitnessScores.size() - 1) {
                xValues.append(", ");
                yValues1.append(", ");
                yValues2.append(", ");
                yValues3.append(", ");
            }
        }
        xValues.append("]");
        yValues1.append("]");
        yValues2.append("]");
        yValues3.append("]");

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<html>");
        htmlContent.append("<head>");
        htmlContent.append("<title>").append(title).append("</title>");
        htmlContent.append("<script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>");
        htmlContent.append("<style>");
        htmlContent.append("body { font-family: Arial, sans-serif; margin: 40px; }");
        htmlContent.append("#plot { width: 100%; height: 100%; }");
        htmlContent.append("</style>");
        htmlContent.append("</head>");
        htmlContent.append("<body>");
        htmlContent.append("<div id=\"plot\"></div>");
        htmlContent.append("<script>");
        htmlContent.append("var trace1 = {");
        htmlContent.append("x: ").append(xValues).append(",");
        htmlContent.append("y: ").append(yValues1).append(",");
        htmlContent.append("mode: 'lines+markers',");
        htmlContent.append("type: 'scatter',");
        htmlContent.append("name: 'Average Fitness Scores',");
        htmlContent.append("line: { color: 'blue', width: 2 },");
        htmlContent.append("marker: { size: 8, color: 'red' }");
        htmlContent.append("};");
        htmlContent.append("var trace2 = {");
        htmlContent.append("x: ").append(xValues).append(",");
        htmlContent.append("y: ").append(yValues2).append(",");
        htmlContent.append("mode: 'lines+markers',");
        htmlContent.append("type: 'scatter',");
        htmlContent.append("name: 'Best Fitness Scores',");
        htmlContent.append("line: { color: 'green', width: 2 },");
        htmlContent.append("marker: { size: 8, color: 'orange' }");
        htmlContent.append("};");
        htmlContent.append("var trace3 = {");
        htmlContent.append("x: ").append(xValues).append(",");
        htmlContent.append("y: ").append(yValues3).append(",");
        htmlContent.append("mode: 'lines+markers',");
        htmlContent.append("type: 'scatter',");
        htmlContent.append("name: 'Worst Fitness Scores',");
        htmlContent.append("line: { color: 'purple', width: 2 },");
        htmlContent.append("marker: { size: 8, color: 'yellow' }");
        htmlContent.append("};");
        htmlContent.append("var data = [trace1, trace2, trace3];");
        htmlContent.append("var layout = {");
        htmlContent.append("title: { text: '").append(title).append("', font: { size: 24 } },");
        htmlContent.append("xaxis: { title: { text: 'Population', font: { size: 18 } }, showgrid: true, zeroline: true },");
        htmlContent.append("yaxis: { title: { text: 'Fitness Score', font: { size: 18 } }, showgrid: true, zeroline: true },");
        htmlContent.append("margin: { l: 50, r: 50, b: 50, t: 50 },");
        htmlContent.append("paper_bgcolor: 'white',");
        htmlContent.append("plot_bgcolor: '#f8f8f8'");
        htmlContent.append("};");
        htmlContent.append("Plotly.newPlot('plot', data, layout);");
        htmlContent.append("</script>");
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(htmlContent.toString());
        } catch (IOException e) {
            logger.error("Some error occurred while writing HTML file to desired path", e);
        }
    }

    public static void visualizeBestChromosomeConstraintChecklist(Fitness fitness, Chromosome bestChromosome) {
        String title = "Best Chromosome With Constraints Checklist";
        StringBuilder htmlContent = new StringBuilder();

        htmlContent.append("<html>");
        htmlContent.append("<head>");
        htmlContent.append("<title>").append(title).append("</title>");
        htmlContent.append("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css\">");
        htmlContent.append("<style>");
        htmlContent.append(".header {background-color: #C2452A; height: 70px; margin-left: 50px; margin-right: 50px; margin-bottom: 10px;}");
        htmlContent.append(".constraint-title {color: #ffffff; font-size: 40px; justify-content: center; align-items: center; display: flex; padding-top: 13px;}");
        htmlContent.append(".checklist {background-color: #F5F0EF; margin-right: 50px; margin-left: 50px; color: #000000; font-size: 20px; padding: 10px;}");
        htmlContent.append(".icon {color: #3aad38; margin-left: 10px;}");
        htmlContent.append("</style>");
        htmlContent.append("</head>");
        htmlContent.append("<body>");
        htmlContent.append("<div class='header'>");
        htmlContent.append("<p class='constraint-title'>HARTE BESCHRAENKUNGEN</p>");
        htmlContent.append("</div>");
        htmlContent.append("<div class='checklist'>");
        htmlContent.append("<p id='required_time'>1- Alle Pruefungen sollten den erforderlichen Zeitrahmen fuer Aufsichtspersonen und Studierende haben.</p>");
        htmlContent.append("<p id='invigilator_count'>2- Fuer alle Pruefungen muss die erforderliche Anzahl an Aufseher vorhanden sein.</p>");
        htmlContent.append("<p id='classroom_overlap'>3- Keinem Klassenraum kann gleichzeitig mehr als eine Pruefung zugewiesen werden.</p>");
        htmlContent.append("<p id='student_overlap'>4- Kein Student darf mehr als eine Pruefung zur gleichen Zeit ablegen.</p>");
        htmlContent.append("<p id='invigilator_overlap'>5- Kein Aufseher sollte mehr als einer Pruefung zur gleichen Zeit zugeordnet sein.</p>");
        htmlContent.append("<p id='invigilator_available'>6- Keiner Aufsichtsperson kann mehr Aufgaben zugewiesen werden, als ihre/seine Kapazitaeten zulassen.</p>");
        htmlContent.append("<p id='have_classrooms'>7- Alle Pruefungen sollten einen Klassenraum haben.</p>");
        htmlContent.append("<p id='has_capacity'>8- In einem Klassenzimmer koennen sich nicht mehr Studenten aufhalten, als die festgelegte Studentenkapazitaet im selben Zeitfenster betraegt.</p>");
        htmlContent.append("<p id='start_end_time'>9- Die Pruefungen duerfen nicht vor 9 Uhr morgens und nicht nach 17 Uhr abends beginnen.</p>");
        htmlContent.append("<p id='holidays'>10- An Feiertagen sollten keine Pruefungen stattfinden.</p>");
        htmlContent.append("<p id='same_start_end'>11- Die Pruefung sollte am selben Tag beginnen und enden</p>");
        htmlContent.append("</div>");

        htmlContent.append("<div class='header' style='background-color: #39BBC8;'>");
        htmlContent.append("<p class='constraint-title'>WEICHE BESCHRAENKUNGEN</p>");
        htmlContent.append("</div>");
        htmlContent.append("<div class='checklist' style='background-color: #F0F5F6;' >");
        htmlContent.append("<p id='student_too_many_exams'>1- Kein Student sollte an einem Tag mehr als zwei Pruefungen ablegen.</p>");
        htmlContent.append("<p id='min_gap_students'>2- Wenn ein Student an einem Tag mehr als eine Pruefung hat, sollte zwischen den beiden mindestens eine Stunde liegen.</p>");
        htmlContent.append("<p id='inv_too_many_exams'>3- Kein Aufseher sollte mehr als drei Pruefungen an einem Tag ueberwachen.</p>");
        htmlContent.append("<p id='min_gap_inv'>4- Wenn der Aufseher mehr als eine Pruefung am selben Tag hat, sollte er mindestens 1 Stunde Zeit haben zwischen.</p>");
        htmlContent.append("<p id='weekend_exams'>5- Am Wochenende soll es keine Pruefungen geben.</p>");
        htmlContent.append("<p id='exams_in_afternoon'>6- Die meisten Pruefungen sollten nachmittags stattfinden, da die Wahrnehmung der Studierenden dann in der Regel am offensten ist.</p>");
        htmlContent.append("<p id='popular_exams'>7- Die von den meisten Schuelern gewaehlten Unterrichtsstunden sollten moeglichst zu Beginn des Pruefungsstundenplans stattfinden. So bleibt der Lehrkraft genuegend Zeit, die Pruefungen zu bewerten.</p>");
        htmlContent.append("</div>");


        htmlContent.append("<script>");
        htmlContent.append("function createIcon() {\n" +
                "    let icon = document.createElement(\"i\");\n" +
                "    icon.className = \"fas fa-check-square icon\"; // Font Awesome check box icon\n" +
                "    return icon;\n" +
                "}");
        htmlContent.append("let icon = document.createElement('i');");
        htmlContent.append("icon.className = \"fa-solid fa-square-check icon\";");


        if (fitness.allExamsHaveRequiredTime(bestChromosome.getEncodedExams()) == 0.0) htmlContent.append("document.getElementById('required_time').appendChild(createIcon());");
        if (fitness.allExamHaveRequiredInvigilatorCount(bestChromosome.getEncodedExams()) == 0.0) htmlContent.append("document.getElementById('invigilator_count').appendChild(createIcon());");
        if (fitness.classroomOverlapped() == 0.0) htmlContent.append("document.getElementById('classroom_overlap').appendChild(createIcon());");
        if (fitness.studentOverlapped() == 0.0) htmlContent.append("document.getElementById('student_overlap').appendChild(createIcon());");
        if (fitness.invigilatorOverlapped() == 0.0) htmlContent.append("document.getElementById('invigilator_overlap').appendChild(createIcon());");
        if (fitness.invigilatorAvailable() == 0.0) htmlContent.append("document.getElementById('invigilator_available').appendChild(createIcon());");
        if (fitness.allExamsHaveClassrooms(bestChromosome.getEncodedExams()) == 0.0) htmlContent.append("document.getElementById('have_classrooms').appendChild(createIcon());");
        if (fitness.classroomsHasCapacity(bestChromosome.getEncodedExams()) == 0.0) htmlContent.append("document.getElementById('has_capacity').appendChild(createIcon());");
        if (fitness.startAndEndTimeDateViolated(bestChromosome.getEncodedExams()) == 0.0) htmlContent.append("document.getElementById('start_end_time').appendChild(createIcon());");
        if (fitness.noExamsInHolidays(bestChromosome.getEncodedExams()) == 0.0) htmlContent.append("document.getElementById('holidays').appendChild(createIcon());");
        if (fitness.examStartAndEndDateSame(bestChromosome.getEncodedExams()) == 0.0) htmlContent.append("document.getElementById('same_start_end').appendChild(createIcon());");


        if (fitness.studentMoreThanTwoExamSameDay() == 0.0) htmlContent.append("document.getElementById('student_too_many_exams').appendChild(createIcon());");
        if (fitness.minimumGapBetweenExamsStudent() == 0.0) htmlContent.append("document.getElementById('min_gap_students').appendChild(createIcon());");
        if (fitness.invigilatorMoreThanThreeExamSameDay() == 0.0) htmlContent.append("document.getElementById('inv_too_many_exams').appendChild(createIcon());");
        if (fitness.minimumGapBetweenExamsInvigilator() == 0.0) htmlContent.append("document.getElementById('min_gap_inv').appendChild(createIcon());");
        if (fitness.noExamsAtWeekends(bestChromosome.getEncodedExams()) == 0.0) htmlContent.append("document.getElementById('weekend_exams').appendChild(createIcon());");
        if (fitness.examsNotInAfternoon() == 0.0) htmlContent.append("document.getElementById('exams_in_afternoon').appendChild(createIcon());");
        if (fitness.popularExamsAtBeginning(bestChromosome.getEncodedExams()) == 0.0) htmlContent.append("document.getElementById('popular_exams').appendChild(createIcon());");

        htmlContent.append("</script>");
        htmlContent.append("</body>");
        htmlContent.append("</html>");

        String baseFileName = "graphs/Checklist/";
        FileHelper.createDirectory(baseFileName);
        String outputFilePath = baseFileName + "checklist.html";
        try {
            FileWriter writer = new FileWriter(outputFilePath);
            writer.write(htmlContent.toString());
            writer.close();
            logger.info("Report saved as HTML file: " + outputFilePath);
        } catch (IOException e) {
            logger.error("Error writing HTML file: " + e.getMessage());
        }
    }

}
