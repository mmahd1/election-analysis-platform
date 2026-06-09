package com.team7.hboict.utils.xml.transformers;


import com.team7.hboict.model.Election;
import com.team7.hboict.model.Municipality;
import com.team7.hboict.model.MunicipalityPartyResult;
import com.team7.hboict.utils.xml.VotesTransformer;

import java.util.Map;

import static com.team7.hboict.utils.xml.TagAndAttributeNames.REGISTERED_NAME;
import static com.team7.hboict.utils.xml.TagAndAttributeNames.VALID_VOTES;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchMunicipalityVotesTransformer implements VotesTransformer {
    private final Election election;

    /**
     * Creates a new transformer for handling the votes at the municipality level. It expects an instance of
     * Election that can be used for storing the results.
     *
     * @param election the election in which the votes wil be stored.
     */
    public DutchMunicipalityVotesTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        if (!aggregated) return;

        String municipalityId = electionData.get("AuthorityIdentifier-Id");
        String municipalityName = electionData.get("AuthorityIdentifier");
        String partyName = electionData.get(REGISTERED_NAME);
        String votesStr = electionData.get(VALID_VOTES);

        if (municipalityId == null || partyName == null || votesStr == null) {
            return;
        }

        Municipality municipality = election.getMunicipalityByNumber(municipalityId);

        if (municipality == null) {
            municipality = new Municipality(municipalityName, municipalityId);
            election.addMunicipality(municipality);
        }

        int votes = Integer.parseInt(votesStr);

        MunicipalityPartyResult pr = new MunicipalityPartyResult(partyName, votes);
        municipality.addPartyResult(pr);
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s candidate votes: %s\n", aggregated ? "Municipality" : "Polling station", electionData);
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s meta data: %s\n", aggregated ? "Municipality" : "Polling station", electionData);
    }

}
