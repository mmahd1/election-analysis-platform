package com.team7.hboict.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity die een politieke partij representeert in de database.
 *
 * Wordt gebruikt door JPA om automatisch een tabel te genereren
 * en data op te slaan en op te halen.
 */
@Entity
public class Party {

    /** Unieke identifier van de partij (primary key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidate> candidates = new ArrayList<>();

    public Party() {
    }

    public Party(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Voegt een kandidaat toe aan de partij indien deze nog niet bestaat.
     *
     * Controleert op dubbele kandidaten op basis van voor- en achternaam.
     *
     * @param candidate De kandidaat die toegevoegd moet worden.
     * @return true als toegevoegd, false als al bestond.
     */
    public boolean addCandidate(Candidate candidate) {
        for (Candidate c : candidates) {
            if (c.getFirstName().equalsIgnoreCase(candidate.getFirstName()) &&
                    c.getLastName().equalsIgnoreCase(candidate.getLastName())) {
                return false;
            }
        }
        return candidates.add(candidate);
    }
}
