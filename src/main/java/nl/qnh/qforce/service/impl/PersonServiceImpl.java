package nl.qnh.qforce.service.impl;

import nl.qnh.qforce.domain.Gender;
import nl.qnh.qforce.domain.Movie;
import nl.qnh.qforce.domain.Person;
import nl.qnh.qforce.domain.impl.MovieImpl;
import nl.qnh.qforce.domain.impl.PersonImpl;
import nl.qnh.qforce.jpa.entities.Analytics;
import nl.qnh.qforce.jpa.repository.AnalyticsRepository;
import nl.qnh.qforce.service.PersonService;
import nl.qnh.qforce.service.dto.SwapiFilmResponse;
import nl.qnh.qforce.service.dto.SwapiPersonResponse;
import nl.qnh.qforce.service.dto.SwapiSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the PersonService.
 * This class handles communication with the external Star Wars API and mapping the results to our domain model.
 * It also takes care of saving analytics data to our database.
 */
@Service
public class PersonServiceImpl implements PersonService {

    private static final String SWAPI_API_URL = "https://swapi.dev/api/people";

    /**
     * Implementation decision:
     * We use WebClient as the HTTP client because it is non-blocking and fits well in the Spring WebFlux ecosystem.
     */
    private final WebClient webClient;

    /**
     * Implementation decision:
     * We use Spring Data JPA repository for simple CRUD operations on our Analytics entity.
     */
    private final AnalyticsRepository analyticsRepository;

    /**
     * Implementation decision:
     * We inject dependencies through constructor to ensure immutability and to make it easier to write unit tests.
     */
    @Autowired
    public PersonServiceImpl(WebClient.Builder webClientBuilder, AnalyticsRepository analyticsRepository) {
        this.webClient = webClientBuilder.baseUrl(SWAPI_API_URL).build();
        this.analyticsRepository = analyticsRepository;
    }

    /**
     * This method searches for persons in the Star Wars API based on the given name.
     *
     * Implementation decision:
     * We decided to use the search functionality provided by the Star Wars API.
     */
    @Override
    public List<Person> search(String name) {
        saveAnalytics("search");
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("search", name)
                        .build())
                .retrieve()
                .bodyToMono(SwapiSearchResponse.class)
                .flatMapIterable(SwapiSearchResponse::getPersons)
                .flatMap(swapiPersonResponse ->
                        Flux.fromIterable(swapiPersonResponse.getFilms())
                                .flatMap(this::getFilm)
                                .collectList()
                                .map(movies -> createPerson(swapiPersonResponse, movies)))
                .collectList().block();
    }

    /**
     * This method saves analytics data to our database.
     *
     * Implementation decision:
     * We decided to track each API call in our database to provide insights into the usage of our API.
     */
    private void saveAnalytics(String apiName) {
        Analytics analytics = analyticsRepository.findByApiName(apiName).orElse(new Analytics());
        if (analytics.getApiName() == null) {
            analytics.setApiName(apiName);
        } else {
            analytics.setHitCount(analytics.getHitCount() + 1);
        }
        analyticsRepository.save(analytics);
    }

    /**
     * This method retrieves information about a movie from the Star Wars API.
     *
     * Implementation decision:
     * We decided to fetch movie information for each movie the person appeared in to provide a more complete profile.
     */
    private Mono<Movie> getFilm(String filmUrl) {
        return webClient.get()
                .uri(filmUrl)
                .retrieve()
                .bodyToMono(SwapiFilmResponse.class)
                .map(film -> new MovieImpl(film.getTitle(), film.getEpisode(), film.getDirector(), film.getReleaseDate()));
    }

    /**
     * This method creates a Person object from the data provided by the Star Wars API.
     *
     * Implementation decision:
     * We map the data from the Star Wars API to our domain model to decouple our application from the external API.
     */
    private Person createPerson(SwapiPersonResponse swapiPersonResponse, List<Movie> movies) {
        long id = extractId(swapiPersonResponse.getUrl());
        return new PersonImpl(
                id,
                swapiPersonResponse.getName(),
                swapiPersonResponse.getBirthYear(),
                getGender(swapiPersonResponse.getGender()),
                swapiPersonResponse.getHeight(),
                swapiPersonResponse.getMass(),
                movies);
    }

    private long extractId(String url) {
        Pattern pattern = Pattern.compile("(\\d+)/$");
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : 0;
    }

    private Gender getGender(String gender) {
        return switch (gender) {
            case "male" -> Gender.MALE;
            case "female" -> Gender.FEMALE;
            case "n/a" -> Gender.NOT_APPLICABLE;
            default -> Gender.UNKNOWN;
        };
    }

    /**
     * This method retrieves a Person by ID from the Star Wars API.
     *
     * Implementation decision:
     * We decided to fetch the person data on-demand when requested by ID to avoid unnecessary API calls and data processing.
     */
    @Override
    public Optional<Person> get(long id) {
        saveAnalytics("getperson");
        SwapiPersonResponse swapiPersonResponse = webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(SwapiPersonResponse.class)
                .block();

        List<Movie> movies = Flux.fromIterable(swapiPersonResponse.getFilms())
                .flatMap(this::getFilm)
                .collectList()
                .block();

        Person person = createPerson(swapiPersonResponse, movies);

        return Optional.of(person);
    }
}

