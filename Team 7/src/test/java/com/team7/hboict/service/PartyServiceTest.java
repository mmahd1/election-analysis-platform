package com.team7.hboict.service;

import com.team7.hboict.dto.PartyDTO;
import com.team7.hboict.model.Election;
import com.team7.hboict.model.Party;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PartyServiceTest {

    @Mock
    private DutchElectionService dutchElectionService;

    @Mock
    private PartyIdeologyService partyIdeologyService;

    @InjectMocks
    private PartyService partyService;

    /**
     * Verifies that parties are returned sorted, deduplicated by normalized
     * name and enriched with ideology descriptions.
     */
    @Test
    public void happyFlow_returnsPartiesForElection() {
        Election election = new Election("TK2023");
        election.addParty(new Party("VVD"));
        election.addParty(new Party("vvd"));
        election.addParty(new Party("PVV"));
        election.addParty(new Party(""));

        when(dutchElectionService.getElection("TK2023")).thenReturn(election);
        when(partyIdeologyService.getDescriptionForParty("PVV")).thenReturn("Rechts-populistisch");
        when(partyIdeologyService.getDescriptionForParty("VVD")).thenReturn("Liberaal-conservatief");

        List<PartyDTO> result = partyService.getAllParties("TK2023");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("PVV");
        assertThat(result.get(0).getDescription()).isEqualTo("Rechts-populistisch");
        assertThat(result.get(1).getName()).isEqualTo("VVD");
        assertThat(result.get(1).getDescription()).isEqualTo("Liberaal-conservatief");

        verify(dutchElectionService).getElection("TK2023");
        verify(dutchElectionService, never()).readResults("TK2023", "electionData/TK2023", null);
    }

    /**
     * Verifies that the service returns an empty list when the election
     * cannot be found in cache and cannot be loaded from XML results.
     */
    @Test
    public void unhappyFlow_returnsEmptyListWhenElectionDoesNotExist() {
        when(dutchElectionService.getElection("TK2099")).thenReturn(null);
        when(dutchElectionService.readResults("TK2099", "electionData/TK2099", null)).thenReturn(null);

        List<PartyDTO> result = partyService.getAllParties("TK2099");

        assertThat(result).isEmpty();

        verify(dutchElectionService).getElection("TK2099");
        verify(dutchElectionService).readResults("TK2099", "electionData/TK2099", null);
    }

    /**
     * Verifies that the service falls back to reading election results when
     * a cached election exists but does not yet contain party data.
     */
    @Test
    public void happyFlow_readsResultsWhenElectionExistsButHasNoParties() {
        Election cachedElectionWithoutParties = new Election("TK2025");
        Election loadedElection = new Election("TK2025");
        loadedElection.addParty(new Party("NSC"));

        when(dutchElectionService.getElection("TK2025")).thenReturn(cachedElectionWithoutParties);
        when(dutchElectionService.readResults("TK2025", "electionData/TK2025", null)).thenReturn(loadedElection);
        when(partyIdeologyService.getDescriptionForParty("NSC")).thenReturn("Christendemocratisch");

        List<PartyDTO> result = partyService.getAllParties("TK2025");

        assertThat(result).singleElement().satisfies(party -> {
            assertThat(party.getName()).isEqualTo("NSC");
            assertThat(party.getDescription()).isEqualTo("Christendemocratisch");
        });

        verify(dutchElectionService).readResults("TK2025", "electionData/TK2025", null);
    }

    /**
     * Verifies that blank or whitespace-only party names are filtered out
     * and do not result in PartyDTO objects or ideology lookups.
     */
    @Test
    public void unhappyFlow_returnsEmptyListWhenLoadedElectionContainsNoUsablePartyNames() {
        Election election = new Election("TK2024");
        election.addParty(new Party(""));
        election.addParty(new Party("   "));

        when(dutchElectionService.getElection("TK2024")).thenReturn(election);

        List<PartyDTO> result = partyService.getAllParties("TK2024");

        assertThat(result).isEmpty();
        verify(dutchElectionService).getElection("TK2024");
        verify(partyIdeologyService, never()).getDescriptionForParty(anyString());
    }
}
