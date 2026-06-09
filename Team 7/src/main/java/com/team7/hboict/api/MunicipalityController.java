package com.team7.hboict.api;

import com.team7.hboict.dto.ComparisonResult;
import com.team7.hboict.model.Municipality;
import com.team7.hboict.service.MunicipalityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for handling municipality-related operations.
 *
 * <p>This controller provides endpoints to retrieve, search, and compare municipalities
 * based on election data. All operations are delegated to the {@link MunicipalityService}.</p>
 *
 * <p><b>Base URL:</b> /api/municipalities</p>
 *
 * @author Edris Zanikhil
 */
@RestController
@RequestMapping("/api/municipalities")
@Tag(name = "Municipalities", description = "Operations for retrieving, searching and comparing municipalities")
public class MunicipalityController {

    private static final Logger logger = LoggerFactory.getLogger(MunicipalityController.class);

    private final MunicipalityService municipalityService;

    /**
     * Constructor injection of MunicipalityService.
     *
     * @param municipalityService service layer responsible for municipality business logic
     */
    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }

    /**
     * Retrieve all municipalities for a given election.
     */
    @Operation(
            summary = "Get all municipalities",
            description = "Returns all municipalities for a given electionId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Municipalities retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Municipality.class)))
    })
    @GetMapping
    public List<Municipality> getAllMunicipalities(
            @Parameter(description = "Election identifier (e.g. 2023, 2024)")
            @RequestParam String electionId) {

        logger.info("Successfully fetched all municipalities for electionId={}", electionId);
        return municipalityService.getMunicipalities(electionId);
    }

    /**
     * Search municipalities by name within a specific election.
     */
    @Operation(
            summary = "Search municipalities",
            description = "Search municipalities by (partial) name within a given election."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully",
                    content = @Content(schema = @Schema(implementation = Municipality.class)))
    })
    @GetMapping("/search")
    public List<Municipality> searchMunicipalities(
            @Parameter(description = "Election identifier")
            @RequestParam String electionId,

            @Parameter(description = "Region or municipality name to search for")
            @RequestParam String regionName) {

        logger.info("Searching municipalities for electionId={} with regionName={}", electionId, regionName);
        return municipalityService.searchByName(electionId, regionName);
    }

    /**
     * Compare two municipalities based on election results.
     */
    @Operation(
            summary = "Compare municipalities",
            description = "Compares two municipalities based on election results and returns comparison metrics."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comparison successful",
                    content = @Content(schema = @Schema(implementation = ComparisonResult.class)))
    })
    @GetMapping("/compare")
    public List<ComparisonResult> compareMunicipalities(
            @Parameter(description = "Election identifier")
            @RequestParam String electionId,

            @Parameter(description = "First municipality ID")
            @RequestParam String idA,

            @Parameter(description = "Second municipality ID")
            @RequestParam String idB) {

        logger.info("Comparing municipalities: {} vs {} for electionId={}", idA, idB, electionId);
        return municipalityService.compareMunicipalities(electionId, idA, idB);
    }
}