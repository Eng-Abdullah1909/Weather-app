package com.devops.weatherapp.service;

import com.devops.weatherapp.model.WeatherLog;
import com.devops.weatherapp.repository.WeatherLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final WeatherLogRepository repository;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.host}")
    private String apiHost;

    public WeatherService(RestTemplate restTemplate, WeatherLogRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    public String getForecast(String place) {
        String url = UriComponentsBuilder.fromHttpUrl("https://" + apiHost + "/api/weather/forecast")
                .queryParam("place", place)
                .queryParam("cnt", 3)
                .queryParam("units", "standard")
                .queryParam("type", "three_hour")
                .queryParam("mode", "json")
                .queryParam("lang", "en")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-host", apiHost);
        headers.set("x-rapidapi-key", apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        WeatherLog log = new WeatherLog();
        log.setPlace(place);
        log.setRequestedAt(LocalDateTime.now());
        log.setRawResponse(response.getBody());
        repository.save(log);

        return response.getBody();
    }

    public List<WeatherLog> getHistory() {
        return repository.findAll();
    }
}
