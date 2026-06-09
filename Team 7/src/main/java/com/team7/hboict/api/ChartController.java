package com.team7.hboict.api;

import com.team7.hboict.service.ChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chart")
public class ChartController {

    @Autowired
    private ChartService chartService;

    @GetMapping("/party/{partyName}/data")
    public ResponseEntity<List<Map<String, Object>>> getPartyChartData(@PathVariable String partyName) {

        List<Map<String, Object>> data = chartService.getPartyChartData(partyName);

        if (data.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(data);
    }
}