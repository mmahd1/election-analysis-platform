package com.team7.hboict.service;

import com.team7.hboict.dto.CandidateDTO;
import com.team7.hboict.model.Candidate;
import com.team7.hboict.repository.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CandidateDetailServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateDetailService candidateDetailService;

    /**
     * HAPPY FLOW
     *
     * Situatie:
     * - repository bevat kandidaten voor de verkiezing
     *
     * Verwachting:
     * - kandidaten worden correct omgezet naar DTO's
     */
    @Test
    public void getAllCandidates_ShouldReturnCandidateDtos_WhenElectionHasCandidates() {
        Candidate candidate = candidate("Sophie", "de Vries", "F", 1240, "deVriesS");
        when(candidateRepository.findByElectionId("TK2025")).thenReturn(List.of(candidate));

        List<CandidateDTO> result = candidateDetailService.getAllCandidates("TK2025");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Sophie");
        assertThat(result.get(0).getLastName()).isEqualTo("de Vries");
        assertThat(result.get(0).getGender()).isEqualTo("F");
        assertThat(result.get(0).getVotes()).isEqualTo(1240);
        assertThat(result.get(0).getShortCode()).isEqualTo("deVriesS");
        verify(candidateRepository).findByElectionId("TK2025");
    }

    /**
     * HAPPY FLOW
     *
     * Situatie:
     * - repository bevat kandidaten van D66
     *
     * Verwachting:
     * - kandidaten van D66 worden teruggegeven als DTO's
     */
    @Test
    public void getCandidatesByParty_ShouldReturnCandidateDtos_WhenPartyHasCandidates() {
        Candidate candidate = candidate("Thomas", "Jansen", "M", 845, "JansenT");
        when(candidateRepository.findByElectionIdAndPartyName("TK2025", "D66"))
                .thenReturn(List.of(candidate));

        List<CandidateDTO> result = candidateDetailService.getCandidatesByParty("TK2025", "D66");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Thomas");
        assertThat(result.get(0).getVotes()).isEqualTo(845);
        verify(candidateRepository).findByElectionIdAndPartyName("TK2025", "D66");
    }

    /**
     * UNHAPPY FLOW
     *
     * Situatie:
     * - repository bevat geen kandidaten voor de opgegeven partij
     *
     * Verwachting:
     * - service geeft een lege lijst terug
     */
    @Test
    public void getCandidatesByParty_ShouldReturnEmptyList_WhenPartyHasNoCandidates() {
        when(candidateRepository.findByElectionIdAndPartyName("TK2025", "Toekomstpartij"))
                .thenReturn(List.of());

        assertThat(candidateDetailService.getCandidatesByParty("TK2025", "Toekomstpartij")).isEmpty();
    }

    private Candidate candidate(String firstName, String lastName, String gender, int votes, String shortCode) {
        Candidate candidate = new Candidate(firstName, lastName, gender);
        candidate.setVotes(votes);
        candidate.setShortCode(shortCode);
        return candidate;
    }
}
