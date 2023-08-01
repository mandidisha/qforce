package nl.qnh.qforce.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapiFilmResponse {

    private String title;

    @JsonProperty("episode_id")
    private Integer episode;

    private String director;

    @JsonProperty("release_date")
    private LocalDate releaseDate;

}
