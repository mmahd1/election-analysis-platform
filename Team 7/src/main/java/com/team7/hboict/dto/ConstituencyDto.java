package com.team7.hboict.dto;

import java.util.List;

public class ConstituencyDto {
    private final String name;
    private final String number;
    private final List<ConstituencyResultDto> partyResults;

    public ConstituencyDto(String name, String number, List<ConstituencyResultDto> partyResults) {
        this.name = name;
        this.number = number;
        this.partyResults = partyResults;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public List<ConstituencyResultDto> getPartyResults() {
        return partyResults;
    }
}