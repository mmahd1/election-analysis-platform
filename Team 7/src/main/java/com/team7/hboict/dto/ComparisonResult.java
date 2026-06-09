package com.team7.hboict.dto;

public class ComparisonResult {
    private String party;
    private int votesA;
    private int votesB;

    public ComparisonResult(String party, int votesA, int votesB) {
        this.party = party;
        this.votesA = votesA;
        this.votesB = votesB;
    }

    public String getParty() {
        return party;
    }

    public int getVotesA() {
        return votesA;
    }

    public int getVotesB() {
        return votesB;
    }
}
