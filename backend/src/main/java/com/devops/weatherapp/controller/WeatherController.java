package com.devops.weatherapp.controller;

import com.devops.weatherapp.model.WeatherLog;
import com.devops.weatherapp.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public ResponseEntity<String> getWeather(@RequestParam String place) {
        return ResponseEntity.ok(weatherService.getForecast(place));
    }

    @GetMapping("/history")
    public ResponseEntity<List<WeatherLog>> getHistory() {
        return ResponseEntity.ok(weatherService.getHistory());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
