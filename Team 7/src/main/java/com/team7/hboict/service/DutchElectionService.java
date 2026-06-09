package com.team7.hboict.service;

import com.team7.hboict.dto.ElectionMapDto;
import com.team7.hboict.dto.ConstituencyMapDto;
import com.team7.hboict.model.Constituency;
import com.team7.hboict.model.ConstituencyResult;
import com.team7.hboict.model.Election;
import com.team7.hboict.repository.ConstituencyRepository;
import com.team7.hboict.repository.ElectionRepository;
import com.team7.hboict.utils.PathUtils;
import com.team7.hboict.utils.xml.DutchElectionParser;
import com.team7.hboict.utils.xml.transformers.DutchCandidatesListTransformer;
import com.team7.hboict.utils.xml.transformers.DutchConstituencyVotesTransformer;
import com.team7.hboict.utils.xml.transformers.DutchDefinitionTransformer;
import com.team7.hboict.utils.xml.transformers.DutchMunicipalityVotesTransformer;
import com.team7.hboict.utils.xml.transformers.DutchNationalVotesTransformer;
import com.team7.hboict.utils.xml.transformers.DutchResultTransformer;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Transactional
@Service
public class DutchElectionService {

    private final ElectionRepository electionRepository;
    private final ConstituencyRepository constituencyRepository;
    private final PartyColorService partyColorService;

    public DutchElectionService(ElectionRepository electionRepository,
                               ConstituencyRepository constituencyRepository,
                               PartyColorService partyColorService) {
        this.electionRepository = electionRepository;
        this.constituencyRepository = constituencyRepository;
        this.partyColorService = partyColorService;
    }

    public Collection<Election> getElections() {
        return electionRepository.retrieveAll();
    }

    public Election getElection(String electionId) {
        return electionRepository.retrieve(electionId);
    }

    public Election readResults(String electionId, String folderName, String mode) {
        return readResults(electionId, folderName, mode, false);
    }

    public Election readResults(String electionId, String folderName, String mode, boolean force) {
        Election existingElection = getElection(electionId);

        if (existingElection != null && !force && hasRequestedData(existingElection, mode)) {
            System.out.println("Election bestaat al: " + electionId);
            return existingElection;
        }

        if (existingElection != null) {
            System.out.println("Election bestaat al maar wordt opnieuw geimporteerd: " + electionId);
            electionRepository.delete(electionId);
        }

        Election election = new Election(electionId);


        boolean constituencyOnly = "constituencyOnly".equals(mode);

        DutchElectionParser parser;

        if (constituencyOnly) {
            parser = new DutchElectionParser(
                    new DutchDefinitionTransformer(election),
                    null,
                    null,
                    null,
                    new DutchConstituencyVotesTransformer(election),
                    null
            );
        } else {
            parser = new DutchElectionParser(
                    new DutchDefinitionTransformer(election),
                    new DutchCandidatesListTransformer(election),
                    new DutchResultTransformer(election),
                    new DutchNationalVotesTransformer(election),
                    new DutchConstituencyVotesTransformer(election),
                    new DutchMunicipalityVotesTransformer(election)
            );
        }

        try {
            String resourcePath = PathUtils.getResourcePath(folderName);

            if (resourcePath == null) {
                resourcePath = PathUtils.getResourcePath("electiondata/" + folderName);
            }

            if (resourcePath == null) {
                throw new IllegalArgumentException(
                        "Kon election data map niet vinden: " + folderName +
                                "\nGeprobeerd: " + folderName +
                                "\nEn ook: electiondata/" + folderName
                );
            }

            System.out.println("Loading election data from: " + resourcePath);

            parser.parseResults(electionId, resourcePath);

            System.out.println("Aantal municipalities: " + election.getMunicipalities().size());

            electionRepository.store(electionId, election);

            return election;

        } catch (IOException | XMLStreamException | ParserConfigurationException | SAXException e) {
            System.err.println("Failed to process the election results!");
            e.printStackTrace();
            return null;
        }
    }

    private boolean hasRequestedData(Election election, String mode) {
        if (election == null) {
            return false;
        }

        boolean hasConstituencies = !election.getConstituencies().isEmpty();

        if ("constituencyOnly".equals(mode)) {
            return hasConstituencies;
        }

        return hasConstituencies
                && !election.getParties().isEmpty()
                && !election.getMunicipalities().isEmpty();
    }


    public List<ElectionMapDto> getRegionalElectionMapData(String electionId) {
        Election election = getElection(electionId);
        if (election == null) {
            election = readResults(electionId, "electionData/" + electionId, "constituencyOnly");
        }

        if (election == null) {
            return List.of();
    }

        // Kieskringnummers volgens Verkiezingsdefinitie_* (TK2023/TK2025)
        // 20 = Bonaire valt buiten de provincies-kaart (nl-all) en wordt hier bewust genegeerd.
        Map<String, String> constituencyToProvince = Map.ofEntries(
                Map.entry("1", "nl-gr"),  // Groningen
                Map.entry("2", "nl-fr"),  // Leeuwarden (Friesland)
                Map.entry("3", "nl-dr"),  // Assen (Drenthe)
                Map.entry("4", "nl-ov"),  // Zwolle (Overijssel)
                Map.entry("5", "nl-fl"),  // Lelystad (Flevoland)
                Map.entry("6", "nl-ge"),  // Nijmegen (Gelderland)
                Map.entry("7", "nl-ge"),  // Arnhem (Gelderland)
                Map.entry("8", "nl-ut"),  // Utrecht
                Map.entry("9", "nl-nh"),  // Amsterdam (Noord-Holland)
                Map.entry("10", "nl-nh"), // Haarlem (Noord-Holland)
                Map.entry("11", "nl-nh"), // Den Helder (Noord-Holland)
                Map.entry("12", "nl-zh"), // 's-Gravenhage (Zuid-Holland)
                Map.entry("13", "nl-zh"), // Rotterdam (Zuid-Holland)
                Map.entry("14", "nl-zh"), // Dordrecht (Zuid-Holland)
                Map.entry("15", "nl-zh"), // Leiden (Zuid-Holland)
                Map.entry("16", "nl-ze"), // Middelburg (Zeeland)
                Map.entry("17", "nl-nb"), // Tilburg (Noord-Brabant)
                Map.entry("18", "nl-nb"), // 's-Hertogenbosch (Noord-Brabant)
                Map.entry("19", "nl-li")  // Maastricht (Limburg)
        );

                Map<String, String> provinceNames = Map.ofEntries(
                Map.entry("nl-gr", "Groningen"),
                Map.entry("nl-fr", "Friesland"),
                Map.entry("nl-dr", "Drenthe"),
                Map.entry("nl-ov", "Overijssel"),
                Map.entry("nl-fl", "Flevoland"),
                Map.entry("nl-ge", "Gelderland"),
                Map.entry("nl-ut", "Utrecht"),
                Map.entry("nl-nh", "Noord-Holland"),
                Map.entry("nl-zh", "Zuid-Holland"),
                Map.entry("nl-ze", "Zeeland"),
                Map.entry("nl-nb", "Noord-Brabant"),
                Map.entry("nl-li", "Limburg")
        );

        Map<String, Map<String, Integer>> votesPerProvince = new HashMap<>();
        for (String provinceKey : provinceNames.keySet()) {
            votesPerProvince.put(provinceKey, new HashMap<>());
        }

        List<Constituency> constituencies = constituencyRepository.findByElectionId(electionId);
        for (Constituency constituency : constituencies) {
            String constituencyNumber = normalizeConstituencyNumber(constituency.getNumber());
            String provinceKey = constituencyToProvince.get(constituencyNumber);
            if (provinceKey == null) {
                continue;
            }

            Map<String, Integer> provinceVotes = votesPerProvince.get(provinceKey);
            if (provinceVotes == null) {
                provinceVotes = new HashMap<>();
                votesPerProvince.put(provinceKey, provinceVotes);
            }

            for (ConstituencyResult result : constituency.getPartyResults()) {
                if (result == null || result.getPartyName() == null) {
                    continue;
                }
                int votes = result.getVotes();
                provinceVotes.merge(result.getPartyName(), votes, Integer::sum);
            }
        }

        List<ElectionMapDto> response = new ArrayList<>();
        for (Map.Entry<String, String> entry : provinceNames.entrySet()) {
            String hcKey = entry.getKey();
            String provinceName = entry.getValue();
            Map<String, Integer> provinceVotes = votesPerProvince.getOrDefault(hcKey, Map.of());

            int totalVotes = provinceVotes.values().stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();

            String winningPartyName = "Onbekend";
            int winningPartyVotes = 0;
            for (Map.Entry<String, Integer> pv : provinceVotes.entrySet()) {
                int v = pv.getValue() == null ? 0 : pv.getValue();
                if (v > winningPartyVotes) {
                    winningPartyVotes = v;
                    winningPartyName = pv.getKey();
                }
            }

            String color = partyColorService.getColorHex(winningPartyName);
            response.add(new ElectionMapDto(hcKey, provinceName, winningPartyName, winningPartyVotes, totalVotes, color));
        }

        response.sort((a, b) -> a.getRegionName().compareToIgnoreCase(b.getRegionName()));
        return response;
    }

    /**
     * Kieskring-samenvatting voor de kaart (Highcharts).
     *
     * Front-end kan deze data als markers (mappoint) op de NL-kaart plotten.
     */
    public List<ConstituencyMapDto> getConstituencyElectionMapData(String electionId) {
        Election election = getElection(electionId);
        if (election == null) {
            election = readResults(electionId, "electionData/" + electionId, "constituencyOnly");
        }

        if (election == null) {
            return List.of();
        }

        List<Constituency> constituencies = constituencyRepository.findByElectionId(electionId);
        List<ConstituencyMapDto> response = new ArrayList<>();

        for (Constituency constituency : constituencies) {
            if (constituency == null) {
                continue;
            }

            String constituencyNumber = normalizeConstituencyNumber(constituency.getNumber());
            String constituencyName = constituency.getName() == null ? "Onbekend" : constituency.getName();

            int totalVotes = 0;
            String winningPartyName = "Onbekend";
            int winningPartyVotes = 0;

            for (ConstituencyResult result : constituency.getPartyResults()) {
                if (result == null || result.getPartyName() == null) {
                    continue;
                }
                int votes = result.getVotes();
                totalVotes += votes;

                if (votes > winningPartyVotes) {
                    winningPartyVotes = votes;
                    winningPartyName = result.getPartyName();
                }
            }

            String color = partyColorService.getColorHex(winningPartyName);
            response.add(new ConstituencyMapDto(constituencyNumber, constituencyName, winningPartyName, winningPartyVotes, totalVotes, color));
        }

        response.sort((a, b) -> {
            try {
                return Integer.compare(Integer.parseInt(a.getConstituencyNumber()), Integer.parseInt(b.getConstituencyNumber()));
            } catch (NumberFormatException ignored) {
                return a.getConstituencyNumber().compareToIgnoreCase(b.getConstituencyNumber());
            }
        });
        return response;
    }

    private String normalizeConstituencyNumber(String raw) {
        if (raw == null) {
            return "";
        }

        String cleaned = raw.trim();
        cleaned = cleaned.replaceFirst("^0+(?!$)", "");
        return cleaned;
    }
}





