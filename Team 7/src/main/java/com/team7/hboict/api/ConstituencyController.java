package com.team7.hboict.api;

import com.team7.hboict.dto.ConstituencyDto;
import com.team7.hboict.service.ConstituencyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/constituencies")
public class ConstituencyController {
    private final ConstituencyService constituencyService;

    public ConstituencyController(ConstituencyService constituencyService) {
        this.constituencyService = constituencyService;
    }

    /**
     * Haalt alle constituencies op voor een verkiezing.
     *
     * @param electionId id van de verkiezing
     * @return lijst met constituencies
     */

    @GetMapping("/{electionId}")
    public List<ConstituencyDto> getConstituencies(@PathVariable String electionId) {
        return constituencyService.getConstituencies(electionId);
    }
}