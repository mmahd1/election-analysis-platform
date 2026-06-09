package com.team7.hboict.service;

import com.team7.hboict.dto.CandidateDTO;
import com.team7.hboict.model.Candidate;
import com.team7.hboict.repository.CandidateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateDetailService {

    private final CandidateRepository candidateRepository;

    public CandidateDetailService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public List<CandidateDTO> getAllCandidates(String electionId) {
        return candidateRepository.findByElectionId(electionId).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<CandidateDTO> getCandidatesByParty(String electionId, String partyId) {
        return candidateRepository.findByElectionIdAndPartyName(electionId, partyId).stream()
                .map(this::toDTO)
                .toList();
    }

    private CandidateDTO toDTO(Candidate candidate) {
        return new CandidateDTO(
                candidate.getId(),
                candidate.getFirstName(),
                candidate.getLastName(),
                candidate.getGender(),
                candidate.getVotes(),
                candidate.getShortCode()
        );
    }
}
