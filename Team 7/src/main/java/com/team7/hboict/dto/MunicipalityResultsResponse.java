package com.team7.hboict.dto;

import java.util.Map;

public class MunicipalityResultsResponse {

    private String regionName;
    private String regionNumber;
    private Map<String, Integer> votesPerParty;

    public MunicipalityResultsResponse(String regionName, String regionNumber, Map<String, Integer> votesPerParty) {
        this.regionName = regionName;
        this.regionNumber = regionNumber;
        this.votesPerParty = votesPerParty;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getRegionNumber() {
        return regionNumber;
    }

    public Map<String, Integer> getVotesPerParty() {
        return votesPerParty;
    }
}
