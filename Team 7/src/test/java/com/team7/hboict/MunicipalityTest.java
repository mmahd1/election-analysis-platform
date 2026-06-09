package com.team7.hboict;

import com.team7.hboict.model.Municipality;
import com.team7.hboict.repository.MunicipalityRepository;
import com.team7.hboict.service.MunicipalityService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


/**
 * Unit tests voor MunicipalityService.
 *
 * @author Edris Zanikhil
 */
class MunicipalityTest {

    private final MunicipalityRepository repository = Mockito.mock(MunicipalityRepository.class);
    private final MunicipalityService service = new MunicipalityService(repository);

    @Test
    @DisplayName("Happy flow: Geeft gemeenten terug voor een electionId")
    void getMunicipalities_returnsData() {
        // Arrange
        String electionId = "TK2025";

        Municipality m1 = new Municipality();
        Municipality m2 = new Municipality();

        List<Municipality> mockResult = List.of(m1, m2);

        when(repository.findByElectionId(electionId)).thenReturn(mockResult);

        // Act
        List<Municipality> result = service.getMunicipalities(electionId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(mockResult, result);

        verify(repository, times(1)).findByElectionId(electionId);
    }

    @Test
    @DisplayName("Unhappy flow: Geeft lege lijst terug als er geen data is")
    void getMunicipalities_returnsEmptyList() {
        // Arrange
        String electionId = "TK2025";

        when(repository.findByElectionId(electionId)).thenReturn(List.of());

        // Act
        List<Municipality> result = service.getMunicipalities(electionId);

        // Assert
        assertEquals(0, result.size());

        verify(repository, times(1)).findByElectionId(electionId);
    }
}
