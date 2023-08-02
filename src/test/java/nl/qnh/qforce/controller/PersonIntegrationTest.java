package nl.qnh.qforce.controller;

import com.fasterxml.jackson.databind.JsonNode;
import nl.qnh.qforce.domain.Person;
import nl.qnh.qforce.domain.impl.PersonImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.ARRAY;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetPerson() {
        ResponseEntity<JsonNode> responseEntity =
                restTemplate.getForEntity("http://localhost:" + port + "/persons/1", JsonNode.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
    }

    @Test
    public void testSearchPerson() {
        ResponseEntity<JsonNode> responseEntity =
                restTemplate.getForEntity("http://localhost:" + port + "/persons?q=Luke", JsonNode.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().isArray()).isTrue();
    }
}
