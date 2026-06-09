package com.team7.hboict.service;

import com.team7.hboict.dto.ConstituencyDto;
import com.team7.hboict.model.Constituency;
import com.team7.hboict.model.ConstituencyResult;
import com.team7.hboict.repository.ConstituencyRepository;
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
public class NationalResultsServiceTest {

    @Mock
    private ConstituencyRepository constituencyRepository;

    @InjectMocks
    private ConstituencyService constituencyService;

    /**
     * Verifies that constituencies and their nested party vote results are
     * mapped correctly from the repository model to DTO output.
     */
    @Test
    public void happyFlow_returnsConstituenciesForNationalResults() {
        Constituency constituency1 = new Constituency("Amsterdam", "1");
        constituency1.addPartyResult(new ConstituencyResult("VVD", 1200));
        constituency1.addPartyResult(new ConstituencyResult("GL-PvdA", 950));

        Constituency constituency2 = new Constituency("Arnhem", "2");
        constituency2.addPartyResult(new ConstituencyResult("D66", 640));

        when(constituencyRepository.findByElectionId("TK2023"))
                .thenReturn(List.of(constituency1, constituency2));

        List<ConstituencyDto> result = constituencyService.getConstituencies("TK2023");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Amsterdam");
        assertThat(result.get(0).getNumber()).isEqualTo("1");
        assertThat(result.get(0).getPartyResults()).hasSize(2);
        assertThat(result.get(0).getPartyResults().get(0).getPartyName()).isEqualTo("VVD");
        assertThat(result.get(0).getPartyResults().get(0).getVotes()).isEqualTo(1200);
        assertThat(result.get(1).getName()).isEqualTo("Arnhem");
        assertThat(result.get(1).getPartyResults()).singleElement().satisfies(partyResult -> {
            assertThat(partyResult.getPartyName()).isEqualTo("D66");
            assertThat(partyResult.getVotes()).isEqualTo(640);
        });

        verify(constituencyRepository).findByElectionId("TK2023");
    }

    /**
     * Verifies that the service returns an empty list when the repository
     * contains no constituencies for the requested election id.
     */
    @Test
    public void unhappyFlow_returnsEmptyListWhenNoConstituenciesExistForElection() {
        when(constituencyRepository.findByElectionId("TK2099")).thenReturn(List.of());

        List<ConstituencyDto> result = constituencyService.getConstituencies("TK2099");

        assertThat(result).isEmpty();
        verify(constituencyRepository).findByElectionId("TK2099");
    }

    /**
     * Verifies that a constituency without any party results is still mapped
     * to a DTO and exposes an empty partyResults collection.
     */
    @Test
    public void edgeFlow_returnsConstituencyWithEmptyPartyResultsList() {
        Constituency constituency = new Constituency("Leiden", "3");

        when(constituencyRepository.findByElectionId("TK2026"))
                .thenReturn(List.of(constituency));

        List<ConstituencyDto> result = constituencyService.getConstituencies("TK2026");

        assertThat(result).singleElement().satisfies(dto -> {
            assertThat(dto.getName()).isEqualTo("Leiden");
            assertThat(dto.getNumber()).isEqualTo("3");
            assertThat(dto.getPartyResults()).isEmpty();
        });
    }
}
