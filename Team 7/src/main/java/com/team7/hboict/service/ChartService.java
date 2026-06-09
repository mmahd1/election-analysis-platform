package com.team7.hboict.service;

import com.team7.hboict.model.*;
import com.team7.hboict.repository.ElectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChartService {

    @Autowired
    private ElectionRepository electionRepository;

    public List<Map<String, Object>> getPartyChartData(String partyName) {
        Election election = electionRepository.retrieve("TK2023");
        if (election == null) {
            return null;
        }
        List<Map<String, Object>> data = new ArrayList<>();
        for (Constituency constituency : election.getConstituencies()) {
            for (ConstituencyResult result : constituency.getPartyResults()) {
                if (result.getPartyName().equals(partyName)) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("kieskring", constituency.getName());
                    entry.put("stemmen", result.getVotes());
                    data.add(entry);
                    break;
                }
            }
        }
        return data;
    }
}