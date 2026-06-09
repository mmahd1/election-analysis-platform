package com.team7.hboict.utils.xml;

import java.util.Map;

/**
 * A <code>CandidateTransformer</code> is responsible for transforming the data into the appropriate model classes.
 */
public interface CandidatesListTransformer {

    /**
     * Called whenever a ListData open tag is parsed. The first candidate appears right after the ListData tag.
     *
     * @param electionData the tags and values.
     */
    void registerParty(Map<String, String> electionData);

    /**
     * Called whenever a Candidate closing tag is parsed.
     *
     * @param electionData the tags and values.
     */
    void registerCandidate(Map<String, String> electionData);
}
