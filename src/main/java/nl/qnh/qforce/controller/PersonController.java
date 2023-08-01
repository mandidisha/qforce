package nl.qnh.qforce.controller;

import lombok.RequiredArgsConstructor;
import nl.qnh.qforce.service.PersonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import nl.qnh.qforce.domain.Person;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PersonController {

    private final PersonService personService;

    @GetMapping("/persons")
    public List<Person> search(@RequestParam("q") String query) {
        return personService.search(query);
    }

    @GetMapping("/persons/{id}")
    public Person findById(@PathVariable long id) {
        return personService.get(id).orElseThrow(() -> new RuntimeException("Not found"));
    }
}
