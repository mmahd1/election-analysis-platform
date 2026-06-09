package com.team7.hboict.utils.xml.transformers;



import com.team7.hboict.model.ConstituencyResult;
import com.team7.hboict.model.Election;
import com.team7.hboict.utils.xml.VotesTransformer;
import com.team7.hboict.model.Constituency;
import com.team7.hboict.utils.xml.TagAndAttributeNames;

import java.util.Map;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchConstituencyVotesTransformer implements VotesTransformer, TagAndAttributeNames {
    private final Election election;

    /**
     * Creates a new transformer for handling the votes at the constituency level. It expects an instance of
     * Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchConstituencyVotesTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        if (!aggregated) {
            return;
        }

        /**
         * Haalt party votes op
         *
         * Slaat de stemmen van de partijen op uit verschillende kieskringen
         */

        String constituencyNumber = electionData.get(CONTEST_IDENTIFIER_ID);;
        String partyName = electionData.get(REGISTERED_NAME);
        String partyVotes = electionData.get(VALID_VOTES);

        if (constituencyNumber == null || partyName == null || partyVotes == null) {
            return;
        }

//        zoekt de juiste kieskring op in de election
        Constituency constituency = election.getConstituencyByNumber(constituencyNumber);

        if (constituency == null) {
            return;
        }

//        maakt een partyResult aan en voegd die toe aan de bijbehorende kieskring
        int votes = Integer.parseInt(partyVotes);
        constituency.addPartyResult(new ConstituencyResult(partyName, votes));
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s candidate votes: %s\n", aggregated ? "Constituency" : "Municipality", electionData);
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s meta data: %s\n", aggregated ? "Constituency" : "Municipality", electionData);
    }
}
