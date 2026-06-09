package com.team7.hboict.service;

import com.team7.hboict.model.*;
import com.team7.hboict.repository.ElectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test voor ChartService
 *
 * Controleert of chart data  wordt opgehaald
 * voor een geselecteerde partij.
 */

@ExtendWith(MockitoExtension.class)
public class ConstituencyGraphServiceTest {

    @Mock
    private ElectionRepository electionRepository;
//    maakt nep repository

    @InjectMocks
    private ChartService chartService;
//    gebruik mock repository in service

    @Test
    public void testGetPartyChartData() {

//        maak nep election object, kieskringen en resultaten aan.
        Election election = new Election("TK2023");

        Constituency constituency1 = new Constituency("Amsterdam", "1");
        Constituency constituency2 = new Constituency("Rotterdam", "2");

        ConstituencyResult result1 =
                new ConstituencyResult("VVD", 1000);
        ConstituencyResult result2 =
                new ConstituencyResult("VVD", 2000);

//        zet resultaten in de kieskringen en kieskring in election
        constituency1.getPartyResults().add(result1);
        constituency2.getPartyResults().add(result2);

        election.getConstituencies().add(constituency1);
        election.getConstituencies().add(constituency2);

//        laat repository de gemockte data terug geven
        when(electionRepository.retrieve("TK2023")).thenReturn(election);

//        roep de service aan die data opvraagt via de mock repository
        List<Map<String, Object>> data = chartService.getPartyChartData("VVD");

        assertThat(data).hasSize(2);

        assertThat(data.get(0).get("kieskring"))
                .isEqualTo("Amsterdam");

        assertThat(data.get(0).get("stemmen"))
                .isEqualTo(1000);

        assertThat(data.get(1).get("kieskring"))
                .isEqualTo("Rotterdam");

        assertThat(data.get(1).get("stemmen"))
                .isEqualTo(2000);
    }
}
