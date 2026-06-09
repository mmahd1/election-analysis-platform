package com.team7.hboict.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class MunicipalityPartyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partyName;
    private int votes;

    @ManyToOne
    @JoinColumn(name = "municipality_region_number")
    private Municipality municipality;

    public MunicipalityPartyResult() {
    }

    public MunicipalityPartyResult(String partyName, int votes) {
        this.partyName = partyName;
        this.votes = votes;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public String getPartyName() {
        return partyName;
    }

    public int getVotes() {
        return votes;
    }
}