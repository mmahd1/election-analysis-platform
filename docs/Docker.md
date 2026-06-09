
Deze applicatie draait in een Docker container.

De Dockerfile:

bouwt de Java applicatie met Maven
maakt een .jar bestand
start de applicatie op poort 8002
Applicatie builden

als je iets hebt gepushed doe dan: "docker compose up --build" om
de applicatie opnieuw te builden en te starten.

voor opstarten zonder build: "docker compose up" of
gebruik docker desktop

Daarna draait de app op:

http://localhost:8002
en de frontend op: https://localhost:5173

