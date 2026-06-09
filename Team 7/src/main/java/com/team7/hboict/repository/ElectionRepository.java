package com.team7.hboict.repository;

import com.team7.hboict.model.Election;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Transactional
@Repository
public class ElectionRepository {
    @PersistenceContext
    private EntityManager em;


    public Collection<Election> retrieveAll() {
        return List.of();
    }

    public Election retrieve(String electionIid) {
        return em.find(Election.class, electionIid);
    }

    public Election store(String electionId, Election election) {
        return em.merge(election);
    }

    public void delete(String electionId) {
        Election election = retrieve(electionId);
        if (election != null) {
            em.remove(election);
            em.flush();
        }
    }
}
