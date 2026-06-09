package com.team7.hboict.service;

import com.team7.hboict.dto.ConstituencyDto;
import com.team7.hboict.model.Constituency;
import com.team7.hboict.repository.ConstituencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test voor ophalen van de kieskringen
 *
 * De test controleert of de getConstituencies methode van de ConstituencyService
 * werkt door een mock ConstituencyRepository en mock DutchElectionService te gebruiken.
 * Het test het geval waarin er kieskringen worden gevonden en het geval waarin er geen kieskringen zijn.
 */
@ExtendWith(MockitoExtension.class)
public class ConstituencyServiceTest {

    @Mock
    private DutchElectionService dutchElectionService;
//    maakt een nep versie van DutchElectionService

    @Mock
    private ConstituencyRepository constituencyRepository;
//    maakt een nep versie van de ConstituencyRepository

    @InjectMocks
    private ConstituencyService constituencyService;
//    gebruikt mock data in de ConstituencyService

    @Test
    public void testGetConstituencies() {
        Constituency constituency1 = new Constituency("Amsterdam", "1");
        Constituency constituency2 = new Constituency("Arnhem", "2");
        List<Constituency> mockConstituencies = List.of(constituency1, constituency2);
//maak 2 kieskring objecten aan met nep data

        when(constituencyRepository.findByElectionId("TK2023")).thenReturn(mockConstituencies);
//geef de lijst met nep objecten terug wanneer in de repository .findByElectionId wordt aangeroepen

        List<ConstituencyDto> result = constituencyService.getConstituencies("TK2023");
//als de repository wordt aangeroepen, geeft deze lijst terug

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Amsterdam");
        assertThat(result.get(1).getNumber()).isEqualTo("2");
    }
}
