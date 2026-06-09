package com.team7.hboict.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Municipality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String regionNumber;

    private String regionName;

    @OneToMany(mappedBy = "municipality", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MunicipalityPartyResult> partyResults = new ArrayList<>();

    @OneToMany(mappedBy = "municipality", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollingStation> pollingStations = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election election;

    public Municipality() {
    }

    public Municipality(String regionName, String regionNumber) {
        this.regionName = regionName;
        this.regionNumber = regionNumber;
    }

    public void setElection(Election election) {
        this.election = election;
    }

    public void addPartyResult(MunicipalityPartyResult result) {
        result.setMunicipality(this);
        partyResults.add(result);
    }

    public Long getId() {
        return id;
    }

    public String getRegionNumber() {
        return regionNumber;
    }

    public String getRegionName() {
        return regionName;
    }

    public List<MunicipalityPartyResult> getPartyResults() {
        return partyResults;
    }

    public List<PollingStation> getPollingStations() {
        return pollingStations;
    }

    public void addPollingStation(PollingStation pollingStation) {
        if (pollingStation == null) {
            return;
        }

        boolean exists = pollingStations.stream()
                .anyMatch(existing -> existing.getStationNumber().equals(pollingStation.getStationNumber()));

        if (exists) {
            return;
        }

        pollingStation.setMunicipality(this);
        pollingStations.add(pollingStation);
    }
}
