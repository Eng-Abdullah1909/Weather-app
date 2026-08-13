package com.devops.weatherapp.repository;

import com.devops.weatherapp.model.WeatherLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherLogRepository extends JpaRepository<WeatherLog, Long> {
}
