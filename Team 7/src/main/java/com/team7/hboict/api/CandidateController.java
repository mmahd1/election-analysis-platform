package com.team7.hboict.api;

import com.team7.hboict.dto.CandidateDTO;
import com.team7.hboict.service.CandidateDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("elections/{electionId}")
@Tag(name = "Kandidaten", description = "Endpoints voor kandidatenlijsten en kandidaatstemmen")
public class CandidateController {

    private final CandidateDetailService candidateDetailService;

    public CandidateController(CandidateDetailService candidateDetailService) {
        this.candidateDetailService = candidateDetailService;
    }

    @GetMapping("candidates")
    @Operation(
            summary = "Haal alle kandidaten op",
            description = "Geeft alle kandidaten terug die deelnemen aan de opgegeven verkiezing."
    )
    @ApiResponse(responseCode = "200", description = "De kandidatenlijst is opgehaald")
    public List<CandidateDTO> getAllCandidates(
            @Parameter(description = "Verkiezings-id, bijvoorbeeld TK2023 of TK2025", example = "TK2025")
            @PathVariable String electionId) {
        return candidateDetailService.getAllCandidates(electionId);
    }

    @GetMapping("parties/{partyId}/candidates")
    @Operation(
            summary = "Haal kandidaten van een partij op",
            description = "Geeft de kandidaten terug van de opgegeven partij binnen een verkiezing."
    )
    @ApiResponse(responseCode = "200", description = "De kandidatenlijst van de partij is opgehaald")
    public List<CandidateDTO> getCandidatesByParty(
            @Parameter(description = "Verkiezings-id, bijvoorbeeld TK2023 of TK2025", example = "TK2025")
            @PathVariable String electionId,
            @Parameter(description = "Naam van de partij", example = "VVD")
            @PathVariable String partyId) {
        return candidateDetailService.getCandidatesByParty(electionId, partyId);
    }
}
