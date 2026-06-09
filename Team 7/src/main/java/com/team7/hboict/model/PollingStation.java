package com.team7.hboict.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PollingStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationNumber;

    private String stationName;

    @ManyToOne
    @JoinColumn(name = "municipality_region_number")
    private Municipality municipality;

    protected PollingStation() {
    }

    public PollingStation(String stationNumber, String stationName) {
        this.stationNumber = stationNumber;
        this.stationName = stationName;
    }

    public Long getId() {
        return id;
    }

    public String getStationNumber() {
        return stationNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }
}
