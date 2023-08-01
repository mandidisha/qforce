package nl.qnh.qforce.domain.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.qnh.qforce.domain.Movie;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieImpl implements Movie {

    private String title;
    private Integer episode;
    private String director;
    private LocalDate releaseDate;
}
