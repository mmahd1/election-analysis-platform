package com.team7.hboict.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PartyIdeology {

    @Id
    private String normalizedName;

    private String partyName;

    private String description;

    protected PartyIdeology() {
    }

    public PartyIdeology(String normalizedName, String partyName, String description) {
        this.normalizedName = normalizedName;
        this.partyName = partyName;
        this.description = description;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public String getPartyName() {
        return partyName;
    }

    public String getDescription() {
        return description;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
