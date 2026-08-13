CREATE TABLE IF NOT EXISTS weather_log (
    id SERIAL PRIMARY KEY,
    place VARCHAR(255),
    requested_at TIMESTAMP,
    raw_response TEXT
);