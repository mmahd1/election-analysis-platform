package com.team7.hboict.utils.xml.transformers;



import com.team7.hboict.model.Election;
import com.team7.hboict.model.Party;
import com.team7.hboict.utils.xml.DefinitionTransformer;
import com.team7.hboict.utils.xml.TagAndAttributeNames;
import com.team7.hboict.model.Constituency;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchDefinitionTransformer implements DefinitionTransformer, TagAndAttributeNames {
    private final Election election;

    /**
     * Creates a new transformer for handling the structure of the constituencies, municipalities and the parties.
     * It expects an instance of Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchDefinitionTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerElection(Map<String, String> electionData) {
        // This method is called many times, and we only want to add the information when it is missing, e.a.
        // only when started processing the XML files.
        if (election.getElectionDate() == null) {
            election.setName(electionData.get(ELECTION_NAME));
            election.setElectionDate(LocalDate.parse(electionData.get(ELECTION_DATE), DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }

    /**
     * Filtert kieskringen
     *
     * maak een kieskring object waar de kieskringen gefilterd worden en
     * de nummers en namen in worden opgeslagen, en voegt dat toe aan election
     *
     */

    @Override
    public void registerRegion(Map<String, String> electionData) {
        String regionCategory = electionData.get(REGION_REGION_CATEGORY);
        String regionName = electionData.get(REGION_NAME);
        String regionNumber = electionData.get(REGION_REGION_NUMBER);

        if ("KIESKRING".equals(regionCategory)) {
            election.addConstituency(new Constituency(regionName, regionNumber));
        }
    }

    @Override
    public void registerParty(Map<String, String> electionData) {
        System.out.println("Party: " + electionData);

        Party party = new Party(electionData.get(REGISTERED_APPELLATION));
        election.addParty(party);
    }
}
