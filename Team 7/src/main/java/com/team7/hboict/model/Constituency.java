package com.team7.hboict.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"election_id", "number"})
})
public class Constituency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    private Election election;

    @OneToMany(mappedBy = "constituency", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ConstituencyResult> constituencyResults = new ArrayList<>();

//    JPA only
    protected Constituency() {
    }

    public Constituency(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public List<ConstituencyResult> getPartyResults() {
        return constituencyResults;
    }

    public void addPartyResult(ConstituencyResult constituencyResult) {
        if (constituencyResult != null) {
            constituencyResult.setConstituency(this);
            constituencyResults.add(constituencyResult);
        }
    }

    public void setElection(Election election) {
        this.election = election;
    }
}
