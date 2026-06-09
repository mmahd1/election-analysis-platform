package com.team7.hboict.service;

import com.team7.hboict.dto.PartyDTO;
import com.team7.hboict.interfaces.PartyInterface;
import com.team7.hboict.model.Election;
import com.team7.hboict.model.Party;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Service voor het ophalen van partijen van een verkiezing.
 *
 * Leunt op {@link DutchElectionService} voor het inladen van verkiezingsdata
 * en beperkt zich zelf tot party-specifieke logica zoals mapping naar DTO's.
 */
@Service
public class PartyService implements PartyInterface {

    private final DutchElectionService dutchElectionService;
    private final PartyIdeologyService partyIdeologyService;

    public PartyService(DutchElectionService dutchElectionService,
                        PartyIdeologyService partyIdeologyService) {
        this.dutchElectionService = dutchElectionService;
        this.partyIdeologyService = partyIdeologyService;
    }

    @Override
    public List<PartyDTO> getAllParties(String electionId) {
        Election election = loadElectionIfNeeded(electionId);

        if (election == null) {
            return List.of();
        }

        return mapParties(election.getParties());
    }

    private List<PartyDTO> mapParties(List<Party> parties) {
        List<PartyDTO> partyDTOs = new ArrayList<>();
        Set<String> seenPartyNames = new LinkedHashSet<>();

        if (parties == null) {
            return partyDTOs;
        }

        parties.stream()
                .filter(Objects::nonNull)
                .filter(party -> party.getName() != null && !party.getName().isBlank())
                .sorted(Comparator.comparing(Party::getName, String.CASE_INSENSITIVE_ORDER))
                .filter(party -> seenPartyNames.add(normalizePartyKey(party.getName())))
                .forEach(party -> {
                    String description = partyIdeologyService.getDescriptionForParty(party.getName());
                    partyDTOs.add(new PartyDTO(party.getName(), description));
                });

        return partyDTOs;
    }

    private Election loadElectionIfNeeded(String electionId) {
        Election election = dutchElectionService.getElection(electionId);

        if (election != null && !election.getParties().isEmpty()) {
            return election;
        }

        return dutchElectionService.readResults(electionId, "electionData/" + electionId, null);
    }

    private String normalizePartyKey(String partyName) {
        return partyName.trim().toUpperCase();
    }
}
