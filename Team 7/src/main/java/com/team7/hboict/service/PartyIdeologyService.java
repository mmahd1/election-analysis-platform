package com.team7.hboict.service;

import com.team7.hboict.model.PartyIdeology;
import com.team7.hboict.repository.PartyIdeologyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class PartyIdeologyService {

    private static final Map<String, String> PARTY_IDEOLOGIES = Map.ofEntries(
            Map.entry("50PLUS", "Belangenpartij voor ouderen; richt zich op pensioenen, koopkracht en zorg voor senioren."),
            Map.entry("BBB", "Agrarisch-populistisch; verdedigt boerenbelangen en pleit voor minder afstand tussen burger en politiek."),
            Map.entry("BELANG VAN NEDERLAND", "Rechts-liberaal; wil minder overheid, lagere belastingen en meer nationale soevereiniteit."),
            Map.entry("BELANG VAN NEDERLAND (BVNL)", "Rechts-liberaal; wil minder overheid, lagere belastingen en meer nationale soevereiniteit."),
            Map.entry("BVNL / GROEP VAN HAGA", "Rechts-liberaal; wil minder overheid, lagere belastingen en meer nationale soevereiniteit."),
            Map.entry("BVNL", "Rechts-liberaal; wil minder overheid, lagere belastingen en meer nationale soevereiniteit."),
            Map.entry("BIJ1", "Radicaal-progressief; legt nadruk op antiracisme, gelijkwaardigheid en sociale rechtvaardigheid."),
            Map.entry("CDA", "Christendemocratisch; combineert solidariteit, rentmeesterschap en gedeelde verantwoordelijkheid."),
            Map.entry("CHRISTENUNIE", "Christelijk-sociaal; combineert sociale zorg met ethisch conservatieve standpunten."),
            Map.entry("D66", "Sociaal-liberaal; sterk gericht op onderwijs, democratische vernieuwing en Europese samenwerking."),
            Map.entry("DE LINIE", "Nationalistisch-conservatief; legt de nadruk op orde, veiligheid en nationale identiteit."),
            Map.entry("DENK", "Progressief en multicultureel; focust op inclusiviteit, anti-discriminatie en gelijke kansen."),
            Map.entry("ELLECT", "Democratisch vernieuwend; zet in op burgerparticipatie, inspraak en bestuurlijke innovatie."),
            Map.entry("FORUM VOOR DEMOCRATIE", "Rechts-conservatief en eurosceptisch; benadrukt nationale soevereiniteit en traditionele waarden."),
            Map.entry("FORUM VOOR DEMOCRATIE (FVD)", "Rechts-conservatief en eurosceptisch; benadrukt nationale soevereiniteit en traditionele waarden."),
            Map.entry("FVD", "Rechts-conservatief en eurosceptisch; benadrukt nationale soevereiniteit en traditionele waarden."),
            Map.entry("FNP", "Regionaal-nationalistisch; zet zich in voor Friese taal, cultuur en autonomie."),
            Map.entry("GROENLINKS / PARTIJ VAN DE ARBEID (PVDA)", "Progressief en sociaal-democratisch; focus op klimaat, bestaanszekerheid en sociale gelijkheid."),
            Map.entry("GROENLINKS / PVDA", "Progressief en sociaal-democratisch; focus op klimaat, bestaanszekerheid en sociale gelijkheid."),
            Map.entry("GROENLINKS/PVDA", "Progressief en sociaal-democratisch; focus op klimaat, bestaanszekerheid en sociale gelijkheid."),
            Map.entry("GL-PVDA", "Progressief en sociaal-democratisch; focus op klimaat, bestaanszekerheid en sociale gelijkheid."),
            Map.entry("JA21", "Conservatief-liberaal; voor streng migratiebeleid, economische vrijheid en een kleinere overheid."),
            Map.entry("LEF - VOOR DE NIEUWE GENERATIE", "Jong-progressief; richt zich op de stem van nieuwe generaties, bestuurlijke vernieuwing en toekomstgericht beleid."),
            Map.entry("LP", "Libertarisch; wil maximale individuele vrijheid en minimale overheidsbemoeienis."),
            Map.entry("LP (LIBERTAIRE PARTIJ)", "Libertarisch; wil maximale individuele vrijheid en minimale overheidsbemoeienis."),
            Map.entry("LIBERTAIRE PARTIJ", "Libertarisch; wil maximale individuele vrijheid en minimale overheidsbemoeienis."),
            Map.entry("LIBERTAIRE PARTIJ (LP)", "Libertarisch; wil maximale individuele vrijheid en minimale overheidsbemoeienis."),
            Map.entry("NL PLAN", "Pragmatisch-populistisch; richt zich op praktische oplossingen voor maatschappelijke en economische problemen."),
            Map.entry("NEDERLAND MET EEN PLAN", "Pragmatisch-populistisch; richt zich op praktische oplossingen voor maatschappelijke en economische problemen."),
            Map.entry("NIEUW SOCIAAL CONTRACT", "Sociaal-conservatief; legt nadruk op goed bestuur, bestaanszekerheid en herstel van vertrouwen in de overheid."),
            Map.entry("NIEUW SOCIAAL CONTRACT (NSC)", "Sociaal-conservatief; legt nadruk op goed bestuur, bestaanszekerheid en herstel van vertrouwen in de overheid."),
            Map.entry("NSC", "Sociaal-conservatief; legt nadruk op goed bestuur, bestaanszekerheid en herstel van vertrouwen in de overheid."),
            Map.entry("PARTIJ VOOR DE DIEREN", "Ecologisch en progressief; dierenrechten, natuur en duurzaamheid staan centraal."),
            Map.entry("PARTIJ VOOR DE DIEREN (PVDD)", "Ecologisch en progressief; dierenrechten, natuur en duurzaamheid staan centraal."),
            Map.entry("PVDD", "Ecologisch en progressief; dierenrechten, natuur en duurzaamheid staan centraal."),
            Map.entry("PARTIJVDSPORT", "Sportgericht en maatschappelijk; zet sport, beweging en vitaliteit centraal in beleid."),
            Map.entry("PARTIJ VOOR DE RECHTSSTAAT", "Rechtsstatelijk-conservatief; legt de nadruk op wetshandhaving, veiligheid en juridische orde."),
            Map.entry("PIRATENPARTIJ", "Digitaal-progressief; richt zich op privacy, transparantie en digitale burgerrechten."),
            Map.entry("PIRATEN PARTIJ", "Digitaal-progressief; richt zich op privacy, transparantie en digitale burgerrechten."),
            Map.entry("PIRATENPARTIJ - DE GROENEN", "Digitaal-progressief en groen; richt zich op privacy, transparantie, duurzaamheid en burgerrechten."),
            Map.entry("POLITIEKE PARTIJ VOOR BASISINKOMEN", "Sociaal-progressief; pleit voor een onvoorwaardelijk basisinkomen en bestaanszekerheid."),
            Map.entry("PVV", "Rechts-populistisch; sterk gericht op anti-immigratie, nationale identiteit en kritiek op islam en EU."),
            Map.entry("PVV (PARTIJ VOOR DE VRIJHEID)", "Rechts-populistisch; sterk gericht op anti-immigratie, nationale identiteit en kritiek op islam en EU."),
            Map.entry("PARTIJ VOOR DE VRIJHEID", "Rechts-populistisch; sterk gericht op anti-immigratie, nationale identiteit en kritiek op islam en EU."),
            Map.entry("SAMEN VOOR NEDERLAND", "Nationalistisch-conservatief; benadrukt nationale identiteit, gemeenschapszin en maatschappelijke samenhang."),
            Map.entry("SGP", "Orthodox-protestants en conservatief; baseert haar politiek op Bijbelse normen en waarden."),
            Map.entry("SPLINTER", "Progressief en inclusief; legt nadruk op representatie, gelijke kansen en sociale rechtvaardigheid."),
            Map.entry("STAATKUNDIG GEREFORMEERDE PARTIJ", "Orthodox-protestants en conservatief; baseert haar politiek op Bijbelse normen en waarden."),
            Map.entry("STAATKUNDIG GEREFORMEERDE PARTIJ (SGP)", "Orthodox-protestants en conservatief; baseert haar politiek op Bijbelse normen en waarden."),
            Map.entry("SP", "Socialistisch; strijdt tegen ongelijkheid en voor een sterke publieke sector."),
            Map.entry("SP (SOCIALISTISCHE PARTIJ)", "Socialistisch; strijdt tegen ongelijkheid en voor een sterke publieke sector."),
            Map.entry("SOCIALISTISCHE PARTIJ", "Socialistisch; strijdt tegen ongelijkheid en voor een sterke publieke sector."),
            Map.entry("SOCIALISTISCHE PARTIJ (SP)", "Socialistisch; strijdt tegen ongelijkheid en voor een sterke publieke sector."),
            Map.entry("VOLT", "Pan-Europees progressief; wil meer Europese samenwerking, innovatie en duurzaamheid."),
            Map.entry("VOLT NEDERLAND", "Pan-Europees progressief; wil meer Europese samenwerking, innovatie en duurzaamheid."),
            Map.entry("VREDE VOOR DIEREN", "Richt zich op dierenwelzijn en vredespolitiek, met nadruk op compassie en bescherming van leven."),
            Map.entry("VVD", "Liberaal-conservatief; legt nadruk op vrije markt, individuele verantwoordelijkheid en economische groei."),
            Map.entry("VRIJ VERBOND", "Libertair-liberaal; staat voor individuele vrijheid en minder overheidsingrijpen.")
    );

    private final PartyIdeologyRepository partyIdeologyRepository;

    public PartyIdeologyService(PartyIdeologyRepository partyIdeologyRepository) {
        this.partyIdeologyRepository = partyIdeologyRepository;
    }

    public String getDescriptionForParty(String partyName) {
        PartyIdeology ideology = getOrCreatePartyIdeology(partyName);
        return ideology.getDescription();
    }

    public String updateDescriptionForParty(String partyName, String description) {
        PartyIdeology ideology = getOrCreatePartyIdeology(partyName);
        ideology.setPartyName(cleanPartyName(partyName));
        ideology.setDescription(cleanDescription(description));
        return partyIdeologyRepository.save(ideology).getDescription();
    }

    private String normalizePartyName(String partyName) {
        if (partyName == null) {
            return "";
        }

        String normalized = partyName.trim().toUpperCase();
        normalized = normalized.replaceAll("\\s+", " ");
        normalized = normalized.replace("–", "-");

        if (normalized.equals("BELANG VAN NEDERLAND (BVNL)")) return "BVNL";
        if (normalized.equals("BVNL / GROEP VAN HAGA")) return "BVNL";
        if (normalized.equals("FORUM VOOR DEMOCRATIE (FVD)")) return "FVD";
        if (normalized.equals("FORUM VOOR DEMOCRATIE")) return "FVD";
        if (normalized.equals("PARTIJ VOOR DE DIEREN (PVDD)")) return "PVDD";
        if (normalized.equals("STAATKUNDIG GEREFORMEERDE PARTIJ (SGP)")) return "SGP";
        if (normalized.equals("GROENLINKS / PVDA") || normalized.equals("GROENLINKS/PVDA")) return "GROENLINKS/PVDA";
        if (normalized.equals("GROENLINKS / PARTIJ VAN DE ARBEID (PVDA)")) return "GROENLINKS/PVDA";
        if (normalized.equals("NIEUW SOCIAAL CONTRACT (NSC)")) return "NSC";
        if (normalized.equals("LIBERTAIRE PARTIJ (LP)") || normalized.equals("LP (LIBERTAIRE PARTIJ)")) return "LP";
        if (normalized.equals("PARTIJ VOOR DE VRIJHEID (PVV)") || normalized.equals("PVV (PARTIJ VOOR DE VRIJHEID)")) return "PVV";
        if (normalized.equals("VOLT NEDERLAND")) return "VOLT";
        if (normalized.equals("PIRATEN PARTIJ")) return "PIRATENPARTIJ";
        if (normalized.equals("PIRATENPARTIJ - DE GROENEN")) return "PIRATENPARTIJ - DE GROENEN";
        if (normalized.equals("NEDERLAND MET EEN PLAN")) return "NL PLAN";
        if (normalized.equals("SOCIALISTISCHE PARTIJ (SP)") || normalized.equals("SP (SOCIALISTISCHE PARTIJ)")) return "SP";

        return normalized;
    }

    private PartyIdeology getOrCreatePartyIdeology(String partyName) {
        String normalizedName = normalizePartyName(partyName);

        return partyIdeologyRepository.findById(normalizedName)
                .orElseGet(() -> partyIdeologyRepository.save(
                        new PartyIdeology(
                                normalizedName,
                                cleanPartyName(partyName),
                                PARTY_IDEOLOGIES.getOrDefault(normalizedName, "Ideologie niet beschikbaar.")
                        )
                ));
    }

    private String cleanPartyName(String partyName) {
        if (partyName == null || partyName.isBlank()) {
            return "";
        }

        return partyName.trim();
    }

    private String cleanDescription(String description) {
        if (description == null || description.isBlank()) {
            return "Ideologie niet beschikbaar.";
        }

        return description.trim();
    }
}
