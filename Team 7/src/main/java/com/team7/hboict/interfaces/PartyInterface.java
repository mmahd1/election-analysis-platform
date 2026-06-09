package com.team7.hboict.interfaces;

import com.team7.hboict.dto.PartyDTO;

import java.util.List;

/**
 * Interface die de contracten definieert voor partijgerelateerde operaties.
 *
 * Deze interface zorgt voor loskoppeling tussen de controller en de service-implementatie,
 * en maakt het mogelijk om verschillende implementaties te gebruiken (bijv. voor testen).
 */
public interface PartyInterface {
    /**
     * Haalt alle partijen op voor een specifieke verkiezing.
     *
     * @param electionId De identificatie van de verkiezing (bijv. "TK2023").
     * @return Een lijst van PartyDTO objecten geschikt voor de frontend.
     */
    List<PartyDTO> getAllParties(String electionId);
}
