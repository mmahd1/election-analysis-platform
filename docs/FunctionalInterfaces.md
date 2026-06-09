# Onderzoeksopdracht

**Ryan Tuijp - 500958318**

## Main Question

How are functional interfaces used in the Java Collection Framework, and what advantages do they give when processing data?

## Sub-questions

- What are functional interfaces in Java, and what are their main features?
- What are lambda expressions, and how do they make using functional interfaces easier?
- How are functional interfaces used in the Java Collection Framework?
- How do functional interfaces help with processing data within collections?

---

## Introductie

In softwareontwikkeling speelt het efficiënt verwerken van data een grote rol. Daarom hebben programmeertalen zoals Java zich zo ontwikkeld om ontwikkelaars een flexibele manier te bieden om met data te werken. Java heeft functies zoals functional interfaces en lambda’s toegevoegd zodat je makkelijker en korter kunt werken met data, zonder veel ingewikkelde code.

Het Java Collection Framework is de basis voor het beheren en opslaan van data, zoals lijsten, maps en sets. Door dit framework te combineren met functional interfaces, kun je data op een meer leesbare en efficiënte manier verwerken. Denk hierbij bijvoorbeeld aan sorteren of het filteren van gegevens zonder dat je hiervoor gebruik hoeft te maken van uitgebreide code.

In dit onderzoek is de hoofdvraag hoe functional interfaces worden toegepast binnen het Java Collection Framework en welke voordelen dit biedt bij het verwerken van gegevens. Om deze vraag te beantwoorden wordt eerst uitgelegd wat functional interfaces eigenlijk zijn en wat hun belangrijkste eigenschappen zijn. Daarna wordt uitgelegd wat lambda-expressies zijn en hoe deze het gebruik van functional interfaces makkelijker maken. Vervolgens wordt besproken hoe functional interfaces binnen het Java Collection Framework worden toegepast. Tot slot wordt gekeken naar hoe functional interfaces en lambda-expressies helpen om data binnen collections efficiënt te verwerken.


# Hoofdvraag

Hoe worden functional interfaces toegepast binnen het Java Collection Framework en welke voordelen bieden ze voor het verwerken van data?

## Deelvragen

- Wat zijn functional interfaces in Java en wat zijn hun belangrijkste eigenschappen?
- Wat zijn lambda-expressies en hoe maken ze het gebruik van functional interfaces eenvoudiger?
- Welke functional interfaces worden gebruikt binnen het Java Collection Framework en hoe worden ze toegepast?
- Hoe dragen functional interfaces bij aan efficiënter en overzichtelijker werken met data binnen collections?

---

## 1. Wat zijn functional interfaces in Java en wat zijn hun belangrijkste eigenschappen?

Wat is een interface? Een interface in Java is een soort “contract” waarin staat welke methodes een klasse moet hebben, zonder dat er in de code staat hoe deze methodes uitgevoerd moeten worden. Een klasse die een interface implementeert, moet zelf bepalen hoe die methodes werken.

Wat is een functional interface? Een functional interface is een speciale soort interface met precies één abstracte methode. Dat betekent dat er één methode is zonder implementatie; alleen de naam, parameters en het returntype zijn gedefinieerd.

Hierdoor kan de interface gebruikt worden met lambda-expressies. Omdat er maar één methode ingevuld hoeft te worden, kan de code korter en duidelijker geschreven worden.

Andere methodes, zoals `default` en `static` methods, mogen wel worden toegevoegd. omdat die al een vaste implementatie hebben en het lambda-gedrag niet beïnvloeden.

![alternatieve tekst](Onderzoek/afbeeldingenOnderzoek/Schermafbeelding%202026-05-07%20225654.png)

Daarnaast wordt vaak de annotatie `@FunctionalInterface` gebruikt. Deze is niet verplicht, maar helpt om fouten te voorkomen. Wanneer er per ongeluk meer dan één abstracte methode wordt toegevoegd, geeft Java een foutmelding.


## 2. Wat zijn lambda-expressies en hoe maken ze het gebruik van functional interfaces eenvoudiger?

Een lambda-expressie is een korte manier om een methode te schrijven zonder dat een programmeur zelf een volledige klasse hoeft te maken. In werkelijkheid maakt Java achter de schermen wel een klasse en een methode aan, maar dit gebeurt automatisch door de compiler.

Hierdoor hoeft de ontwikkelaar alleen de logica van de methode te schrijven. Dit maakt de code korter en overzichtelijker. Omdat een lambda altijd wordt gebruikt in combinatie met een functional interface, weet Java precies welke methode uitgevoerd moet worden.

Een lambda-expressie bestaat uit drie onderdelen. Eerst schrijf je de parameters, dat zijn de waarden die je invoert. Daarna komt een pijl (`->`) die aangeeft dat de invoer wordt gekoppeld aan een bepaalde actie. Vervolgens schrijf je de body, dat is het gedeelte waarin staat wat er met de invoer moet gebeuren.

![alternatieve tekst](Onderzoek/afbeeldingenOnderzoek/Schermafbeelding%202026-05-07%20225604.png)

Lambda-expressies maken het gebruik van functional interfaces eenvoudiger omdat je geen aparte klasse of anonieme klasse meer hoeft te schrijven om een methode te implementeren. In plaats daarvan schrijf je de code voor de methode precies op de plek waar je die nodig hebt. Dit zorgt voor minder code, betere leesbaarheid en snellere ontwikkeling.

## 3. Wat zijn lambda-expressies en hoe maken ze het gebruik van functional interfaces eenvoudiger?

Binnen het Java Collection Framework worden verschillende functional interfaces gebruikt om bewerkingen op verzamelingen (zoals lijsten en sets) eenvoudiger en flexibeler te maken. Deze interfaces worden vaak toegepast samen met een lambda-expressie en komen vooral uit het pakket java.util.function.

Een veelgebruikte functional interface is predicate. Deze wordt gebruikt om elementen te testen op een voorwaarde en geeft een boolean (true/false) terug. Bijvoorbeeld bij het filteren van een lijst: 

![alternatieve tekst](Onderzoek/afbeeldingenOnderzoek/Schermafbeelding%202026-05-07%20225527.png)


Hier wordt geen Predicate in de code geschreven, maar de lambda-expressie x -> x < 10 wordt automatisch door Java geïnterpreteerd als een Predicate. Dit komt doordat de methode removeIf een Predicate als parameter verwacht. Een Predicate is dus een functionele interface die bepaalt of een element aan een bepaalde voorwaarde voldoet. In dit geval wordt gecontroleerd of een getal kleiner is dan 10; als dat true is, wordt het element uit de lijst verwijderd. Dit is een predicate interface omdat het controleert of iets wel of niet waar is.



Een andere belangrijke interface is function. Deze wordt gebruikt om een waarde om te zetten naar een andere waarde. Deze interface wordt vaak gebruikt met stream. 

![alternatieve tekst](Onderzoek/afbeeldingenOnderzoek/Schermafbeelding%202026-05-07%20225458.png)

Een stream kun je zien als een soort verwerkingslijn waarin alle elementen 1 voor 1 worden bewerkt. Het is geen nieuwe lijst, maar een manier om data te verwerken.

De lijst wordt dus eerst omgezet naar een stream,  .map betekend dat elk element moet worden aangepast en x -> x * 2) is de functie die de input verdubbeld. En met toList  wordt de stream weer omgezet naar een lijst. Dit is een function interface omdat  het een waarde omzet naar een andere waarde.



Daarnaast is er consumer, een functional interface die een element verwerkt zonder iets terug te geven. Je gebruikt dit wanneer je iets met een waarde wilt doen, maar geen nieuw resultaat nodig hebt. Bijvoorbeeld als je elementen uit een lijst 1 voor 1 wilt printen. Consumer wordt vaak gebruikt in combinatie met forEach. 

![alternatieve tekst](Onderzoek/afbeeldingenOnderzoek/Schermafbeelding%202026-05-07%20225402.png)

List.forEach loopt door alle elementen heen van de lijst, en de functie print alle elementen uit. Dit is een Consumer interface omdat de functie alleen iets doet met een waarde, maar niets teruggeeft.





Een andere belangrijke functional interface is comparator. Deze wordt gebruikt om elementen met elkaar te vergelijken, bijvoorbeeld om een lijst te sorteren. 

![alternatieve tekst](Onderzoek/afbeeldingenOnderzoek/Schermafbeelding%202026-05-07%20225317.png)

De methode sort sorteerd de elementen in de lijst. Maar om dat te doen heeft java een comparator nodig, dat is een functie die twee waardes met elkaar vergelijkt.

(a, b) -> a – b) is een lambda expressie die een comparator implementeert. Deze functie zorgt ervoor dat de elementen in de lijst op een oplopende manier gesorteerd worden, dus 1,2,3,4,5... Java begrijpt zelf dat jouw lambda een Comparator is, omdat sort alleen met een Comparator kan werken. Dit is een comparator interface omdat het 2 waardes met elkaar vergelijkt. 



## 4. Hoe dragen functional interfaces bij aan efficiënter en overzichtelijker werken met data binnen collections?

Functional interfaces helpen bij het verwerken van data binnen collections doordat ze het werken met sets, lijsten en andere verzamelingen korter, makkelijker en duidelijker kunnen maken. Samen met lambda expressies en streams kun je met minder code toch veel verschillende bewerkingen op data uitvoeren.

Een groot voordeel van functional interfaces is dat je minder code hoeft te schrijven. Je kunt bijvoorbeeld in plaats van lange loops of klassen, met een lambda expressie direct aangeven wat er moet gebeuren met bepaalde data. Dit maakt code overzichtelijker en sneller te schrijven.

Ook zorgen functional interfaces ervoor dat beter kunt aangeven wat je met de data wilt gaan doen, zonder dat je zelf hoeft te programmeren hoe dat moet. Je kunt bijvoorbeeld in plaat van zelf door een lijst te lopen kun je met methodes zoals filter() aangeven welke gegevens je wilt selecteren en met map() hoe je gegevens wilt aanpassen.

Functional interfaces kunnen code ook herbruikbaar maken. Je kunt dezelfde methode meerdere keren gebruiken met verschilende lambda expressies. Je kunt bijvoorbeeld een lijst filteren om verschillende voorwaarden, zoder dat je daarvoor verschillende methodes hoeft te schrijven

Verder werken functional interfaces goed samen met streams. Daardoor kun je data stap voor stap verwerken. je kunt bijvoorbeeld eerst bepaalde gegevens selecteren met filter(), daarna de waarden aanpassen met map(), en vervolgens iets doen met de uitkomst, zoals printen. Doordat deze stappen achter elkaar worden uitgevoerd, blijft de code overzichtelijk. 


## DEMO

Ik heb ook een demo gemaakt waar ik het verschil vergelijk tussen een filter methode met een for-loop, en een filter methode met gebruik van een functional interface en lambda expressies. Ik heb gekeken naar het verschil in tijd en code: 

![alternatieve tekst](Onderzoek/afbeeldingenOnderzoek/Schermafbeelding%202026-05-07%20190148.png)

Het verschil tussen de twee manieren zit vooral in de manier van programmeren en de hoeveelheid code. Bij de for-loop wordt stap voor stap beschreven hoe de lijst wordt doorlopen. Elke waarde wordt handmatig gecontroleerd met een if-statement en daarna eventueel toegevoegd aan een nieuwe lijst. Bij de lambda-expressie met een stream wordt dit anders gedaan. Hier wordt niet meer handmatig door de lijst gelopen, maar wordt met filter() aangegeven welke waarden behouden moeten blijven.

Voor een andere test heb ik een lijst gemaakt van 0 t/m 999.999. Daarna heb ik beide methodes deze lijst laten filteren op getallen die hoger zijn dan 500.000, en die getallen in een andere lijst laten zetten:  

![alternatieve tekst](Onderzoek/afbeeldingenOnderzoek/Schermafbeelding%202026-05-07%20204241.png)

Het resultaat van de verschillende methodes zijn hetzelfde, maar een for-loop is meestal iets sneller dan een lambda-expressie met streams, omdat de for-loop direct door de lijst heen gaat zonder extra stappen. Bij een for-loop wordt elk element direct gecontroleerd met een if-statement en eventueel toegevoegd aan een nieuwe lijst. Dit maakt de uitvoering eenvoudig en snel.

Bij een stream met een lambda-expressie zijn er meer stappen nodig. Eerst wordt de lijst omgezet naar een stream, daarna wordt de lambda-expressie via een functional interface uitgevoerd en wordt elk element verwerkt volgens de filter-voorwaarde. Vervolgens wordt het resultaat weer omgezet naar een lijst met toList(). Deze extra stappen zorgen voor een kleine hoeveelheid extra verwerkingstijd.



## Conclusie

Functional interfaces spelen een grote rol binnen het java collection Framework doordat ze het mogelijk maken om gegevens op een duidelijke en kortere manier te verwerken. Samen met lambda expressies en streams kunnen ze ervoor zorgen dat bewerkingen zoals filteren, sorteren en aanpassen, simpeler kan worden uitgevoerd.

Uit dit onderzoek blijkt dat lambda-expressies de code korter en overzichtelijker maken dan andere methodes zoals de for-loop. De for-loop is vaak iets sneller omdat deze direct door de lijst heen gaat zonder extra stappen, terwijl streams meer interne verwerking heeft.

Toch is het snelheidsverschil in de meeste gevallen klein. Het grootste voordeel van functional interfaces en lambda-expressies zit in de leesbaarheid, flexibiliteit en onderhoudbaarheid van de code. 


