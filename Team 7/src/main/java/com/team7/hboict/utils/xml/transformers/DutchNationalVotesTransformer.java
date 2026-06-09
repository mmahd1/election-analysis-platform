package com.team7.hboict.utils.xml.transformers;

import com.team7.hboict.model.Election;
import com.team7.hboict.model.Candidate;
import com.team7.hboict.model.Party;
import com.team7.hboict.utils.xml.VotesTransformer;

import java.util.Map;

public class DutchNationalVotesTransformer implements VotesTransformer {

    private final Election election;
    private Party currentParty = null;

    public DutchNationalVotesTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        String partyName = electionData.get("RegisteredName");

        if (partyName != null) {
            currentParty = election.getParty(partyName.trim());
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        String shortCode = electionData.get("CandidateIdentifier-ShortCode");
        String votesStr = electionData.get("ValidVotes");

        if (shortCode == null || votesStr == null || currentParty == null) return;

        int votes;
        try {
            votes = Integer.parseInt(votesStr);
        } catch (NumberFormatException e) {
            return;
        }

        for (Candidate candidate : currentParty.getCandidates()) {
            if (shortCode.startsWith(candidate.getLastName())) {
                candidate.addVotes(votes);
                return;
            }
        }
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {}
}