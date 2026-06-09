## UML Class diagram 

```mermaid
classDiagram
    direction TB

    class Election {
        -String id
        -String name
        -String category
        -Date electionDate
    }

    class Contest {
        -String id
        -String name
    }

    class Party {
        -String id
        -String name
    }

    class Candidate {
        -String id
        -String firstName
        -String lastName
        -String gender
        -String locality
    }

    class CandidateList {
        -String listNumber
    }

    class VoteResult {
        -int validVotes
    }

    class VoteStatistics {
        -int totalCounted
        -int rejectedInvalid
        -int rejectedBlank
    }

    class Municipality {
        -String regionNumber
        -String regionName
        -String superiorRegionNumber
    }

    class PollingStation {
        -String id
        -String name
    }

    class Constituency {
        -String regionNumber
        -String regionName
    }

    Election "1" --* "*" Contest
    Contest "1" -- "1" Constituency
    Contest "1" -- "*" Party
    Contest "1" --o "1" VoteStatistics
    Party "1" -- "*" CandidateList
    CandidateList "1" -- "*" Candidate
    Party "1" -- "*" VoteResult
    Candidate "0..1" -- "*" VoteResult
    PollingStation "1" -- "*" VoteResult
    Constituency "1" --o "*" Municipality : includes
    Municipality "1" --o "*" PollingStation : includes
```

We hadden feedback gekregen dat de class diagram niet zou werken bij een gemeenteraads verkiezing,
dat de benaaming van de realties beter verwoord konden worden en dat de kandidaten boven de partijen
moesten staan om dat meerdere kandidaten een partij vormen. We hebben die feedback verwerkt en de 
benaming van de relaties aangepast. Maar we hebben ervoor gekozen om de andere feedback niet aan te
passen omdat we hebben gekeken naar de xml sturctuur en daar staan de partijen boven de kandidaten.
Kandidaten zijn daar dus onderdeel van een partij. Ook gaan we het diagram niet aanpassen naar 
gemeenteraadsverkiezingen omdat dat niet is waar we met onze applicatie op focussen.