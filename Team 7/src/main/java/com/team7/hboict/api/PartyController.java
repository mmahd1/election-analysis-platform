package com.team7.hboict.api;

import com.team7.hboict.dto.PartyDTO;
import com.team7.hboict.interfaces.PartyInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller die HTTP-requests voor partijen verwerkt
 * en JSON-data terugstuurt naar de frontend.
 */
@RestController
@RequestMapping("/api/parties")
@Tag(
        name = "Parties",
        description = "Operations related to political parties"
)
public class PartyController {

    /** Logger voor het loggen van requests en debug-informatie. */
    private static final Logger logger = LoggerFactory.getLogger(PartyController.class);

    /**
     * Service die de logica bevat voor het ophalen en verwerken van partijen.
     */
    private final PartyInterface partyService;

    public PartyController(PartyInterface partyService) {
        this.partyService = partyService;
    }

    /**
     * Haalt alle partijen op voor een specifieke verkiezing.
     *
     * Ondersteunde verkiezingen:
     * - TK2023
     * - TK2025
     *
     * @param electionId verkiezings-ID
     * @return lijst met partijen
     */
    @GetMapping
    @Operation(
            summary = "Retrieve all parties",
            description = "Returns all political parties for the specified election. Supported elections: TK2023 and TK2025."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parties retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error while retrieving parties")
    })
    public List<PartyDTO> getParties(
            @Parameter(
                    description = "Election identifier. Supported values: TK2023 and TK2025.",
                    example = "TK2025"
            )
            @RequestParam(defaultValue = "TK2025")
            String electionId
    ) {
        logger.info("Fetching all parties for election {}", electionId);
        List<PartyDTO> parties = partyService.getAllParties(electionId);
        logger.debug("Fetched {} parties for election {}", parties.size(), electionId);
        return parties;
    }
}
