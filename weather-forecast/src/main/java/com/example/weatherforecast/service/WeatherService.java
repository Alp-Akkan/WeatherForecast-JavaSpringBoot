package com.example.weatherforecast.service;

import com.example.weatherforecast.model.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherResponse getWeatherForecast(double lat, double lon) {
        String url = "https://api.openweathermap.org/data/3.0/onecall?lat=" + lat + "&lon=" + lon + "&exclude=minutely,hourly&appid=" + apiKey + "&units=metric";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        logger.info("API response: " + response);

        if (response == null || response.isEmpty()) {
            logger.error("Empty or null response from API");
            return null; 
        }

        JSONObject jsonResponse = new JSONObject(response);

        if (!jsonResponse.has("daily")) {
            logger.error("'daily' array not found in the response");
            return null; 
        }

        JSONArray dailyArray = jsonResponse.getJSONArray("daily");

        if (dailyArray.length() == 0) {
            logger.error("'daily' array is empty");
            return null; 
        }

        JSONObject today = dailyArray.getJSONObject(0);

        if (!today.has("temp") || !today.getJSONObject("temp").has("max")) {
            logger.error("'max' temperature not found in the response");
            return null; 
        }

        if (!today.has("feels_like") || !today.getJSONObject("feels_like").has("day")) {
            logger.error("'feels_like' temperature not found in the response");
            return null; 
        }

        if (!today.has("humidity")) {
            logger.error("'humidity' not found in the response");
            return null; 
        }

        double maxTemperature = today.getJSONObject("temp").getDouble("max");
        double feelsLikeTemperature = today.getJSONObject("feels_like").getDouble("day");
        int humidity = today.getInt("humidity");

        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setMaxTemperature(maxTemperature);
        weatherResponse.setFeelsLikeTemperature(feelsLikeTemperature);
        weatherResponse.setHumidity(humidity);

        return weatherResponse;
    }
}
