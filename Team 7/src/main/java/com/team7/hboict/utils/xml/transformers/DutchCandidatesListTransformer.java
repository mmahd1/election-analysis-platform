package com.team7.hboict.utils.xml.transformers;

import com.team7.hboict.model.Election;
import com.team7.hboict.utils.xml.CandidatesListTransformer;
import com.team7.hboict.utils.xml.TagAndAttributeNames;
import com.team7.hboict.model.Candidate;
import com.team7.hboict.model.Party;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DutchCandidatesListTransformer implements CandidatesListTransformer, TagAndAttributeNames {

    private final Election election;
    private final Set<String> registeredPartyNames = new LinkedHashSet<>();

    public DutchCandidatesListTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerParty(Map<String, String> electionData) {
        String partyName = electionData.get("RegisteredName");

        if (partyName != null && !partyName.isBlank()) {
            String cleanPartyName = partyName.trim();

            if (registeredPartyNames.add(cleanPartyName)) {
                election.addParty(new Party(cleanPartyName));
            }
        }
    }

    @Override
    public void registerCandidate(Map<String, String> electionData) {
        String partyName = electionData.get(REGISTERED_NAME);
        String id = electionData.get("CandidateIdentifier-Id");

        if (partyName == null || id == null) return;

        Party party = election.getParty(partyName.trim());
        if (party == null) return;

        String firstName = electionData.get(FIRST_NAME);
        String lastName = electionData.get(LAST_NAME);
        String gender = electionData.get(GENDER);

        if (firstName == null || lastName == null) return;

        Candidate candidate = new Candidate(id, firstName, lastName, gender);

        String shortCode = lastName.replaceAll("\\s+", "") + firstName.charAt(0);
        candidate.setShortCode(shortCode);

        party.addCandidate(candidate);
    }
}