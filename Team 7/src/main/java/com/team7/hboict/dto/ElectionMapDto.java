package com.team7.hboict.dto;

/**
 * DTO voor het tonen van regionale (provincie) uitslagen op een kaart.
 *
 * hcKey sluit aan op Highcharts map-data (bijv. 'nl-gr').
 */
public class ElectionMapDto {

    private final String hcKey;
    private final String regionName;

    private final String winningPartyName;
    private final int winningPartyVotes;
    private final int totalVotes;

    private final String winningPartyColor;

    public ElectionMapDto(String hcKey,
                          String regionName,
                          String winningPartyName,
                          int winningPartyVotes,
                          int totalVotes,
                          String winningPartyColor) {
        this.hcKey = hcKey;
        this.regionName = regionName;
        this.winningPartyName = winningPartyName;
        this.winningPartyVotes = winningPartyVotes;
        this.totalVotes = totalVotes;
        this.winningPartyColor = winningPartyColor;
    }

    public String getHcKey() {
        return hcKey;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getWinningPartyName() {
        return winningPartyName;
    }

    public int getWinningPartyVotes() {
        return winningPartyVotes;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public String getWinningPartyColor() {
        return winningPartyColor;
    }
}

