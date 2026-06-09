package com.team7.hboict.model;

import jakarta.persistence.*;

@Entity public class ConstituencyResult {

    @Id
    @GeneratedValue
    private long id;
    private String partyName;
    private int votes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constituency_id")
    private Constituency constituency;

    protected ConstituencyResult() {};

    public ConstituencyResult(String partyName, int votes) {
        this.partyName = partyName;
        this.votes = votes;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
    public String getPartyName() {
        return partyName;
    }
    public int getVotes() {
        return votes;
    }
    
    public void setConstituency(Constituency constituency) {
        this.constituency = constituency;
    }
}