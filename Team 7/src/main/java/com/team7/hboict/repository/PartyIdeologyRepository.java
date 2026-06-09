package com.team7.hboict.repository;

import com.team7.hboict.model.PartyIdeology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyIdeologyRepository extends JpaRepository<PartyIdeology, String> {
}
