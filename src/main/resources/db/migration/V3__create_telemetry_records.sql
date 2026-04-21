CREATE TABLE telemetry_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    guincho_id BIGINT NOT NULL,
    fsr_reading DOUBLE,
    obstacle_detected BOOLEAN NOT NULL,
    anomaly_alert BOOLEAN NOT NULL,
    battery_level INT,
    recorded_at DATETIME NOT NULL,
    CONSTRAINT fk_telemetry_guincho FOREIGN KEY (guincho_id) REFERENCES guinchos(id)
);

CREATE INDEX idx_telemetry_guincho_recorded_at ON telemetry_records (guincho_id, recorded_at DESC);

