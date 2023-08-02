package nl.qnh.qforce.service.impl;

import nl.qnh.qforce.domain.Person;
import nl.qnh.qforce.jpa.entities.Analytics;
import nl.qnh.qforce.jpa.repository.AnalyticsRepository;
import nl.qnh.qforce.service.dto.SwapiFilmResponse;
import nl.qnh.qforce.service.dto.SwapiPersonResponse;
import nl.qnh.qforce.service.dto.SwapiSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PersonServiceImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private AnalyticsRepository analyticsRepository;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private PersonServiceImpl personService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri((Function<UriBuilder, URI>) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(analyticsRepository.findByApiName(anyString())).thenReturn(Optional.of(new Analytics()));
        when(analyticsRepository.save(any(Analytics.class))).thenReturn(new Analytics());
        personService = new PersonServiceImpl(WebClient.builder(), analyticsRepository);
    }

    @Test
    void testSearch() {
        SwapiPersonResponse personResponse = new SwapiPersonResponse();
        personResponse.setName("Luke Skywalker");
        personResponse.setFilms(List.of("https://swapi.dev/films/1/"));

        SwapiSearchResponse searchResponse = new SwapiSearchResponse();
        List<SwapiPersonResponse> persons = List.of(personResponse);
        searchResponse.setPersons(persons);

        when(responseSpec.bodyToMono(SwapiSearchResponse.class)).thenReturn(Mono.just(searchResponse));

        SwapiFilmResponse filmResponse = new SwapiFilmResponse();
        filmResponse.setTitle("A New Hope");
        when(responseSpec.bodyToMono(SwapiFilmResponse.class)).thenReturn(Mono.just(filmResponse));

        List<Person> result = personService.search("Luke");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Luke Skywalker", result.get(0).getName());
    }

    @Test
    void testGet() {
        SwapiPersonResponse personResponse = new SwapiPersonResponse();
        personResponse.setName("Luke Skywalker");
        personResponse.setFilms(List.of("https://swapi.dev/films/1/"));

        when(responseSpec.bodyToMono(SwapiPersonResponse.class)).thenReturn(Mono.just(personResponse));

        SwapiFilmResponse filmResponse = new SwapiFilmResponse();
        filmResponse.setTitle("A New Hope");
        when(responseSpec.bodyToMono(SwapiFilmResponse.class)).thenReturn(Mono.just(filmResponse));

        Optional<Person> result = personService.get(1L);

        // Assert
        assertEquals("Luke Skywalker", result.get().getName());
    }
}
