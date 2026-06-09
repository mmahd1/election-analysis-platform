package com.team7.hboict.dto;

public class ConstituencyResultDto {
    private final String partyName;
    private final int votes;

    public ConstituencyResultDto(String partyName, int votes) {
        this.partyName = partyName;
        this.votes = votes;
    }

    public String getPartyName() {
        return partyName;
    }

    public int getVotes() {
        return votes;
    }
}