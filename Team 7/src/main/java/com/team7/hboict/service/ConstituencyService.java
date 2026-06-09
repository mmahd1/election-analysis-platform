package com.team7.hboict.service;

import com.team7.hboict.dto.ConstituencyDto;
import com.team7.hboict.dto.ConstituencyResultDto;
import com.team7.hboict.model.Constituency;
import com.team7.hboict.repository.ConstituencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConstituencyService {
    private final ConstituencyRepository constituencyRepository;

    public ConstituencyService(ConstituencyRepository constituencyRepository) {
        this.constituencyRepository = constituencyRepository;
    }

    /**
     * Haalt data op
     *
     * Haalt de geparste kieskring data op
     */

    public List<ConstituencyDto> getConstituencies(String electionId) {
        return constituencyRepository.findByElectionId(electionId).stream()
                .map(this::toDto)
                .toList();
    }

    private ConstituencyDto toDto(Constituency constituency) {
        List<ConstituencyResultDto> partyResults = constituency.getPartyResults().stream()
                .map(result -> new ConstituencyResultDto(result.getPartyName(), result.getVotes()))
                .toList();

        return new ConstituencyDto(
                constituency.getName(),
                constituency.getNumber(),
                partyResults
        );
    }
}