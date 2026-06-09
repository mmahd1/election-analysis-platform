package com.team7.hboict.repository;

import com.team7.hboict.model.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {

    /**
     * Controleert of een partij met deze naam al bestaat.
     *
     * @param name naam van de partij
     * @return true als een partij met deze naam bestaat
     */
    boolean existsByName(String name);
}
