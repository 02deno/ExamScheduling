package org.example.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class APIHelper {

    private static final Logger logger = LogManager.getLogger(APIHelper.class);
    private static final String API_URL = "https://holidayapi.com/v1/holidays";
    private static final String API_KEY = "36a8ed62-d377-4180-8a74-3691cf2be2a8";

    public static Set<LocalDate> fetchHolidays() {

        Set<LocalDate> holidays = new HashSet<>();
        OkHttpClient client = new OkHttpClient();
        String country = "TR";
        int year = 2023;

        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(API_URL)).newBuilder();
        urlBuilder.addQueryParameter("country", country);
        urlBuilder.addQueryParameter("year", String.valueOf(year));
        urlBuilder.addQueryParameter("pretty", "");
        urlBuilder.addQueryParameter("key", API_KEY);

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        logger.info("Request: " + request);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Response isnt successful " + response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(Objects.requireNonNull(response.body()).string());
            JsonNode holidaysNode = rootNode.path("holidays");

            for (JsonNode holidayNode : holidaysNode) {
                String date = holidayNode.path("date").asText();
                holidays.add(LocalDate.parse(date));
            }
        } catch (IOException e) {
            logger.error("Error fetching holidays: " + e.getMessage());
        }
        return holidays;
    }
}
