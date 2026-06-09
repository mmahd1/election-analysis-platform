package com.team7.hboict.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PartyColorService {

    private static final String DEFAULT_COLOR = "#94a3b8";

    private static final Map<String, String> PARTY_COLORS = Map.of(
            "PVV", "#1d4ed8",
            "VVD", "#2563eb",
            "D66", "#10b981",
            "GL-PVDA", "#16a34a",
            "GROENLINKS-PVDA", "#16a34a",
            "CDA", "#f59e0b",
            "SP", "#dc2626",
            "FVD", "#7c3aed",
            "BBB", "#65a30d",
            "NSC", "#0ea5e9"
    );

    public String getColorHex(String partyName) {
        if (partyName == null || partyName.isBlank()) {
            return DEFAULT_COLOR;
        }

        String key = normalize(partyName);

        String knownColor = PARTY_COLORS.get(key);
        if (knownColor != null) {
            return knownColor;
        }

        return generateColorFromString(key);
    }

    private String normalize(String partyName) {
        return partyName
                .trim()
                .toUpperCase()
                .replace("\u00A0", " ")
                .replaceAll("\\s+", " ");
    }

    private String generateColorFromString(String value) {
        int hash = value.hashCode();

        int r = (hash >> 16) & 0xFF;
        int g = (hash >> 8) & 0xFF;
        int b = hash & 0xFF;

        return String.format("#%02x%02x%02x", r, g, b);
    }
}