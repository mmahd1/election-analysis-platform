package com.team7.hboict.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String gender;

    private int votes = 0;
    private String shortCode;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;

    // DO NOT USE, JPA ONLY!
    protected Candidate() {
    }

    public Candidate(String firstName, String lastName, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public Candidate(String id, String firstName, String lastName, String gender) {
        this(firstName, lastName, gender);
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public int getVotes() {
        return votes;
    }

    public String getShortCode() {
        return shortCode;
    }

    public Party getParty() {
        return party;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void addVotes(int votes) {
        this.votes += votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", name='" + firstName + " " + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", votes=" + votes +
                '}';
    }
}