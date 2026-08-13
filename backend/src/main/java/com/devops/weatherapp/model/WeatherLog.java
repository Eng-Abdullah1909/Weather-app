package com.devops.weatherapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_log")
public class WeatherLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String place;

    private LocalDateTime requestedAt;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public String getRawResponse() { return rawResponse; }
    public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }
}
