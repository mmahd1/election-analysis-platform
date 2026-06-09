package com.team7.hboict.repository;

import com.team7.hboict.model.Constituency;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class ConstituencyRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Constituency> findByElectionId(String electionId) {
        return em.createQuery(
                        "SELECT DISTINCT c FROM Constituency c " +
                                "LEFT JOIN FETCH c.constituencyResults " +
                                "WHERE c.election.id = :electionId",
                        Constituency.class
                )
                .setParameter("electionId", electionId)
                .getResultList();
    }
}