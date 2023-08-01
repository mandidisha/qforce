package nl.qnh.qforce.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapiSearchResponse {

    private int count;
    private String next;
    private String previous;

    @JsonProperty("results")
    private List<SwapiPersonResponse> persons;

}
