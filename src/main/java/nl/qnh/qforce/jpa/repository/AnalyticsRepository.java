package nl.qnh.qforce.jpa.repository;

import nl.qnh.qforce.jpa.entities.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalyticsRepository extends JpaRepository<Analytics, Long> {
    Optional<Analytics> findByApiName(String apiName);
}
