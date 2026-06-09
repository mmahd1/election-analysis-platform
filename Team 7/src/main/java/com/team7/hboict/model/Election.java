package com.team7.hboict.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Election {

    @Id
    private String id;

    private String name;

    private LocalDate electionDate;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "election_id")
    private List<Party> parties = new ArrayList<>();

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Constituency> constituencies = new ArrayList<>();

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Municipality> municipalities = new ArrayList<>();

    protected Election() {
    }

    public Election(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(LocalDate electionDate) {
        this.electionDate = electionDate;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void addParty(Party party) {
        if (party == null) {
            return;
        }
        parties.add(party);
    }

    public void addConstituency(Constituency constituency) {
        if (constituency == null) {
            return;
        }

        String number = constituency.getNumber();
        if (number != null) {
            Constituency existing = getConstituencyByNumber(number);
            if (existing != null) {
                return;
            }
        }

        constituency.setElection(this);
        constituencies.add(constituency);
    }

    public List<Constituency> getConstituencies() {
        return constituencies;
    }

    public Constituency getConstituencyByNumber(String number) {
        for (Constituency constituency : constituencies) {
            if (constituency.getNumber().equals(number)) {
                return constituency;
            }
        }
        return null;
    }

    public Party getParty(String partyId) {
        for (Party party : parties) {
            if (party.getName().equals(partyId)) {
                return party;
            }
        }
        return null;
    }

    public List<Candidate> getAllCandidates() {
        List<Candidate> allCandidates = new ArrayList<>();
        for (Party party : parties) {
            allCandidates.addAll(party.getCandidates());
        }
        return allCandidates;
    }


    // Municipality
    public void addMunicipality(Municipality m) {
        m.setElection(this);
        municipalities.add(m);
    }

    public List<Municipality> getMunicipalities() {
        return municipalities;
    }

    public Municipality getMunicipalityByNumber(String number) {
        return municipalities.stream()
                .filter(m -> m.getRegionNumber().equals(number))
                .findFirst()
                .orElse(null);
    }
}