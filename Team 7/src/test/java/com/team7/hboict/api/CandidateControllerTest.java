package com.team7.hboict.api;

import com.team7.hboict.dto.CandidateDTO;
import com.team7.hboict.service.CandidateDetailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CandidateControllerTest {

    @Mock
    private CandidateDetailService candidateDetailService;

    @InjectMocks
    private CandidateController candidateController;

    /**
     * HAPPY FLOW
     *
     * Situatie:
     * - kandidaten worden opgevraagd voor de verkiezingen van 2023 en 2025
     *
     * Verwachting:
     * - controller geeft de kandidatenlijst terug
     */
    @Test
    public void getAllCandidates_ShouldReturnCandidates_WhenElectionExists() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(candidateController).build();

        for (String electionId : List.of("TK2023", "TK2025")) {
            when(candidateDetailService.getAllCandidates(electionId))
                    .thenReturn(List.of(new CandidateDTO(1L, "Sophie", "de Vries", "F", 1240, "deVriesS")));

            mockMvc.perform(get("/elections/{electionId}/candidates", electionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].firstName").value("Sophie"))
                    .andExpect(jsonPath("$[0].lastName").value("de Vries"))
                    .andExpect(jsonPath("$[0].votes").value(1240));

            verify(candidateDetailService).getAllCandidates(electionId);
        }
    }

    /**
     * HAPPY FLOW
     *
     * Situatie:
     * - kandidaten van D66 worden opgevraagd voor 2023 en 2025
     *
     * Verwachting:
     * - controller geeft alleen de kandidaten van D66 terug
     */
    @Test
    public void getCandidatesByParty_ShouldReturnCandidates_WhenPartyExists() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(candidateController).build();

        for (String electionId : List.of("TK2023", "TK2025")) {
            when(candidateDetailService.getCandidatesByParty(electionId, "D66"))
                    .thenReturn(List.of(new CandidateDTO(2L, "Thomas", "Jansen", "M", 845, "JansenT")));

            mockMvc.perform(get("/elections/{electionId}/parties/D66/candidates", electionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].firstName").value("Thomas"))
                    .andExpect(jsonPath("$[0].votes").value(845));

            verify(candidateDetailService).getCandidatesByParty(electionId, "D66");
        }
    }

    /**
     * UNHAPPY FLOW
     *
     * Situatie:
     * - kandidaten worden opgevraagd voor een partij die niet bestaat
     *
     * Verwachting:
     * - controller geeft een lege lijst terug
     */
    @Test
    public void getCandidatesByParty_ShouldReturnEmptyList_WhenPartyDoesNotExist() throws Exception {
        when(candidateDetailService.getCandidatesByParty("TK2025", "Toekomstpartij"))
                .thenReturn(List.of());

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(candidateController).build();

        mockMvc.perform(get("/elections/TK2025/parties/Toekomstpartij/candidates"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
