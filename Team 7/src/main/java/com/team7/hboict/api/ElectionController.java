package com.team7.hboict.api;


import com.team7.hboict.dto.ConstituencyMapDto;
import com.team7.hboict.dto.ElectionMapDto;
import com.team7.hboict.model.Election;
import com.team7.hboict.service.DutchElectionService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import java.util.List;

/**
 * A controller that is responsible for handling Election related requests.
 */
@RestController
@RequestMapping("elections")
public class ElectionController {
    private final DutchElectionService electionService;

    public ElectionController(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Returns a list which holds, per election, the high-level data, like election-id, election date and so on.
     * @return per election the high-level data.
     */
    @GetMapping
    public Collection<Election> getElections() {
        return electionService.getElections();
    }

    /**
     * Returns for a specific election the high-level data, like election-id, election date and so on.
     * @param electionId the election-id for which the high-level data is being requested.
     * @return the high-level data.
     */
    @GetMapping("{electionId}")
    public Election getElection(@PathVariable String electionId) {
        return electionService.getElection(electionId);
    }

    /**
     * Processes the result for a specific election.
     * @param electionId the id of the election, e.g. the value of the Id attribute from the ElectionIdentifier tag.
     * @param folderName the name of the folder that contains the XML result files. If none is provided the value from
     *                   the electionId is used.
     * @return Election if the results have been processed successfully. Please be sure yoy don't output all the data!
     * Just the general data about the election should be sent back to the front-end!<br/>
     * <i>If you want to return something else please feel free to do so!</i>
     */

    /**
     * krijgt de post binnen
     *
     * Krijgt de post binnen en geeft de electionId en foldername door aan DutchElectionService
     */
    @PostMapping("{electionId}")
    public Election readResults(@PathVariable String electionId,
                                @RequestParam(required = false) String folderName,
                                @RequestParam(required = false) String mode,
                                @RequestParam(defaultValue = "false") boolean force) {
        if (folderName == null) {
            return electionService.readResults(electionId, electionId, mode, force);
        } else {
            return electionService.readResults(electionId, folderName, mode, force);
        }
    }

    /**
     * Provinciale samenvatting voor de kaart (Highcharts).
     *
     * Let op: voor nu worden kieskringresultaten geaggregeerd naar provincie.
     */
    @GetMapping("{electionId}/regional/map/summary")
    public List<ElectionMapDto> getRegionalElectionMapData(@PathVariable String electionId) {
        return electionService.getRegionalElectionMapData(electionId);
    }

    /**
     * Kieskring samenvatting voor de kaart (Highcharts).
     */
    @GetMapping("{electionId}/regional/map/constituency/summary")
    public List<ConstituencyMapDto> getConstituencyElectionMapData(@PathVariable String electionId) {
        return electionService.getConstituencyElectionMapData(electionId);
    }
}
