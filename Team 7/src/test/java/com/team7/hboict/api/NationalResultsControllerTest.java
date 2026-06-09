package com.team7.hboict.api;

import com.team7.hboict.dto.ConstituencyDto;
import com.team7.hboict.model.Constituency;
import com.team7.hboict.service.ConstituencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NationalResultsControllerTest {

    @Mock
    private ConstituencyService constituencyService;

    @InjectMocks
    private ConstituencyController constituencyController;

    /**
     * Verifies that the controller returns constituency DTO data for a valid
     * election id and serializes the response fields correctly.
     */
    @Test
    public void happyFlow_returnsConstituenciesForNationalResults() throws Exception {
        when(constituencyService.getConstituencies("TK2023"))
                .thenReturn(List.of(
                        new ConstituencyDto(
                                "Amsterdam",
                                "1", List.of())));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(constituencyController).build();

        mockMvc.perform(get("/api/constituencies/TK2023"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Amsterdam"))
                .andExpect(jsonPath("$[0].number").value("1"));

        verify(constituencyService).getConstituencies("TK2023");
    }

    /**
     * Verifies that an exception from the service layer propagates during the
     * request when the requested election data is missing.
     */
    @Test
    public void unhappyFlow_returnsServerErrorWhenElectionIsMissing() throws Exception {
        when(constituencyService.getConstituencies("TK2099"))
                .thenThrow(new IllegalArgumentException("No election found"));

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(constituencyController).build();

        assertThatThrownBy(() -> mockMvc.perform(get("/api/constituencies/TK2099")))
                .hasCauseInstanceOf(IllegalArgumentException.class);

        verify(constituencyService).getConstituencies("TK2099");
    }
}
