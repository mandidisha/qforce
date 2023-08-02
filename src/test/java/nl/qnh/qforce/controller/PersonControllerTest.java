package nl.qnh.qforce.controller;

import nl.qnh.qforce.domain.Person;
import nl.qnh.qforce.domain.impl.PersonImpl;
import nl.qnh.qforce.service.PersonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private MockMvc mockMvc;

    @Test
    public void testSearch() throws Exception {
        // Mocking and setup
        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
        given(personService.search("Luke")).willReturn(Collections.singletonList(new PersonImpl()));

        // Perform get request
        mockMvc.perform(get("/persons?q=Luke")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindById() throws Exception {
        // Mocking and setup
        mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
        given(personService.get(1L)).willReturn(Optional.of(new PersonImpl()));

        // Perform get request
        mockMvc.perform(get("/persons/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}