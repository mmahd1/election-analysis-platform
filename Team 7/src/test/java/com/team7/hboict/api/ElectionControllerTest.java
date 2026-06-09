package com.team7.hboict.api;

import com.team7.hboict.dto.ElectionMapDto;
import com.team7.hboict.model.Election;
import com.team7.hboict.service.DutchElectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElectionControllerTest {

    @Mock
    private DutchElectionService electionService;

    @InjectMocks
    private ElectionController electionController;

    @Test
    void getElections_happyFlow_delegatesToService() {
        // Arrange
        Collection<Election> expected = List.of(new Election("TK2023"));
        when(electionService.getElections()).thenReturn(expected);

        // Act
        Collection<Election> actual = electionController.getElections();

        // Assert
        assertThat(actual).isSameAs(expected);
        verify(electionService).getElections();
    }

    @Test
    void getElection_happyFlow_delegatesToService() {
        // Arrange
        String electionId = "TK2023";
        Election expected = new Election(electionId);
        when(electionService.getElection(electionId)).thenReturn(expected);

        // Act
        Election actual = electionController.getElection(electionId);

        // Assert
        assertThat(actual).isSameAs(expected);
        verify(electionService).getElection(electionId);
    }

    @Test
    void readResults_withoutFolderName_usesElectionIdAsFolderName() {
        // Arrange
        String electionId = "TK2023";
        when(electionService.readResults(eq(electionId), eq(electionId), eq("constituencyOnly"), eq(false)))
                .thenReturn(new Election(electionId));

        // Act
        Election election = electionController.readResults(electionId, null, "constituencyOnly", false);

        // Assert
        assertThat(election).isNotNull();
        assertThat(election.getId()).isEqualTo(electionId);
        verify(electionService).readResults(eq(electionId), eq(electionId), eq("constituencyOnly"), eq(false));
    }

    @Test
    void readResults_withFolderName_usesProvidedFolderName() {
        // Arrange
        String electionId = "TK2023";
        String folderName = "electionData/TK2023";
        when(electionService.readResults(eq(electionId), eq(folderName), eq("constituencyOnly"), eq(false)))
                .thenReturn(new Election(electionId));

        // Act
        Election election = electionController.readResults(electionId, folderName, "constituencyOnly", false);

        // Assert
        assertThat(election).isNotNull();
        assertThat(election.getId()).isEqualTo(electionId);
        verify(electionService).readResults(eq(electionId), eq(folderName), eq("constituencyOnly"), eq(false));
    }

    @Test
    void getRegionalElectionMapData_happyFlow_delegatesToService() {
        // Arrange
        String electionId = "TK2023";
        List<ElectionMapDto> expected = List.of(new ElectionMapDto(
                "nl-gr",
                "Groningen",
                "PVV",
                100,
                150,
                "#000000"
        ));
        when(electionService.getRegionalElectionMapData(electionId)).thenReturn(expected);

        // Act
        List<ElectionMapDto> actual = electionController.getRegionalElectionMapData(electionId);

        // Assert
        assertThat(actual).isSameAs(expected);
        verify(electionService).getRegionalElectionMapData(electionId);
    }

    @Test
    void getRegionalElectionMapData_unhappyFlow_serviceThrows_exceptionPropagates() {
        // Arrange
        String electionId = "TK2023";
        when(electionService.getRegionalElectionMapData(electionId))
                .thenThrow(new RuntimeException("boom"));

        // Act + Assert
        assertThatThrownBy(() -> electionController.getRegionalElectionMapData(electionId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("boom");

        verify(electionService).getRegionalElectionMapData(electionId);
    }
}

