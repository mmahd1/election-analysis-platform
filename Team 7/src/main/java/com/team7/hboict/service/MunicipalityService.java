package com.team7.hboict.service;

import com.team7.hboict.dto.ComparisonResult;
import com.team7.hboict.model.Election;
import com.team7.hboict.model.Municipality;
import com.team7.hboict.repository.MunicipalityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class MunicipalityService {

    private final DutchElectionService dutchElectionService;
    private final MunicipalityRepository municipalityRepository;

    @Autowired
    public MunicipalityService(DutchElectionService dutchElectionService,
                               MunicipalityRepository municipalityRepository) {
        this.dutchElectionService = dutchElectionService;
        this.municipalityRepository = municipalityRepository;
    }

    public MunicipalityService(MunicipalityRepository municipalityRepository) {
        this(null, municipalityRepository);
    }

    public List<Municipality> getMunicipalities(String electionId) {
        loadElectionIfNeeded(electionId);
        return municipalityRepository.findByElectionId(electionId);
    }

    public List<Municipality> searchByName(String electionId, String regionName) {
        loadElectionIfNeeded(electionId);
        return municipalityRepository.searchByElectionAndName(electionId, regionName);
    }

    public List<ComparisonResult> compareMunicipalities(String electionId, String idA, String idB) {
        loadElectionIfNeeded(electionId);

        Municipality a = municipalityRepository.findByRegionNumber(electionId, idA);
        Municipality b = municipalityRepository.findByRegionNumber(electionId, idB);

        Map<String, Integer> mapA = a.getPartyResults().stream().collect(Collectors.toMap(p -> p.getPartyName(), p -> p.getVotes()));

        Map<String, Integer> mapB = b.getPartyResults().stream().collect(Collectors.toMap(p -> p.getPartyName(), p -> p.getVotes()));

        Set<String> parties = new HashSet<>();
        parties.addAll(mapA.keySet());
        parties.addAll(mapB.keySet());

        List<ComparisonResult> result = new ArrayList<>();

        for (String party : parties) {
            int votesA = mapA.getOrDefault(party, 0);
            int votesB = mapB.getOrDefault(party, 0);

            result.add(new ComparisonResult(party, votesA, votesB));
        }

        return result;
    }

    private void loadElectionIfNeeded(String electionId) {
        if (dutchElectionService == null) {
            return;
        }

        Election election = dutchElectionService.getElection(electionId);
        if (election == null) {
            dutchElectionService.readResults(electionId, "electionData/" + electionId, null);
        }
    }
}
