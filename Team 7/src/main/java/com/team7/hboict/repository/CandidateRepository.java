package com.team7.hboict.repository;

import com.team7.hboict.model.Candidate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class CandidateRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Candidate> findByElectionId(String electionId) {
        return em.createQuery(
                        "SELECT c FROM Election e JOIN e.parties p JOIN p.candidates c WHERE e.id = :electionId",
                        Candidate.class
                )
                .setParameter("electionId", electionId)
                .getResultList();
    }

    public List<Candidate> findByElectionIdAndPartyName(String electionId, String partyName) {
        return em.createQuery(
                        "SELECT c FROM Election e JOIN e.parties p JOIN p.candidates c " +
                                "WHERE e.id = :electionId AND p.name = :partyName",
                        Candidate.class
                )
                .setParameter("electionId", electionId)
                .setParameter("partyName", partyName)
                .getResultList();
    }
}
