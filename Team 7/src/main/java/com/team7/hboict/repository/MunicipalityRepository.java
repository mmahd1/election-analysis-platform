package com.team7.hboict.repository;

import com.team7.hboict.model.Municipality;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Transactional
@Repository
public class MunicipalityRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Municipality> findByElectionId(String electionId) {
        return em.createQuery("SELECT m FROM Municipality m WHERE m.election.id = :electionId", Municipality.class).setParameter("electionId", electionId).getResultList();
    }

    public List<Municipality> searchByElectionAndName(String electionId, String regionName) {
        return em.createQuery("SELECT m FROM Municipality m " + "WHERE m.election.id = :electionId " + "AND LOWER(m.regionName) LIKE LOWER(CONCAT('%', :regionName, '%'))", Municipality.class).setParameter("electionId", electionId).setParameter("regionName", regionName).getResultList();
    }

    public Municipality findByRegionNumber(String electionId, String regionNumber) {
        return em.createQuery("SELECT m FROM Municipality m WHERE m.election.id = :electionId AND m.regionNumber = :regionNumber", Municipality.class).setParameter("electionId", electionId).setParameter("regionNumber", regionNumber).getSingleResult();
    }
}
