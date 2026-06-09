package com.team7.hboict.dto;

/**
 * DTO voor het tonen van kieskring-uitslagen op een kaart.
 *
 * NB: in tegenstelling tot provincies gebruiken we hier geen hcKey (polygon join),
 * maar een kieskringnummer. De front-end kan zelf coördinaten/markers mappen.
 */
public class ConstituencyMapDto {

    private final String constituencyNumber;
    private final String regionName;

    private final String winningPartyName;
    private final int winningPartyVotes;
    private final int totalVotes;

    private final String winningPartyColor;

    public ConstituencyMapDto(String constituencyNumber,
                             String regionName,
                             String winningPartyName,
                             int winningPartyVotes,
                             int totalVotes,
                             String winningPartyColor) {
        this.constituencyNumber = constituencyNumber;
        this.regionName = regionName;
        this.winningPartyName = winningPartyName;
        this.winningPartyVotes = winningPartyVotes;
        this.totalVotes = totalVotes;
        this.winningPartyColor = winningPartyColor;
    }

    public String getConstituencyNumber() {
        return constituencyNumber;
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

