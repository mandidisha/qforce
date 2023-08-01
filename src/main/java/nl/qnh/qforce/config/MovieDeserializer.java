package nl.qnh.qforce.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import nl.qnh.qforce.domain.Movie;
import nl.qnh.qforce.domain.impl.MovieImpl;

import java.io.IOException;
import java.time.LocalDate;

public class MovieDeserializer extends StdDeserializer<Movie> {

    public MovieDeserializer() {
        this(null);
    }

    public MovieDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Movie deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        // assuming MovieImpl has a constructor that accepts all these properties
        // replace "propertyX" with actual property names
        String title = node.get("title").asText();
        int episode = node.get("episode").asInt();
        String director = node.get("director").asText();
        LocalDate releaseDate = LocalDate.parse(node.get("releaseDate").asText());

        return new MovieImpl(title, episode, director, releaseDate);
    }
}