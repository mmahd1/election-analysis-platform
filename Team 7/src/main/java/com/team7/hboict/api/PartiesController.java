package com.team7.hboict.api;


import com.team7.hboict.model.Election;
import com.team7.hboict.model.Party;
import com.team7.hboict.service.DutchElectionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * A controller that is responsible for handling Party related requests for a given election, based on the
 * election id.
 */
@RestController
@RequestMapping("elections/{electionId}")
public class PartiesController {
    private final DutchElectionService electionService;

    public PartiesController(DutchElectionService electionService) {
        this.electionService = electionService;
    }

    /**
     * Returns a list of all the parties involved in a specific election.
     * @param electionId the election-id for which the list of parties is requested.
     * @return a list with all the parties for the specified election, or an empty list
     * in case there is no election with the specified id.
     */
    @GetMapping("parties")
    public List<Party> getParties(@PathVariable String electionId) {
        Election election = electionService.getElection(electionId);
        return election == null ? List.of() : election.getParties();
    }
}
