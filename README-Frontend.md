Frontend README
Frontend starten---

Node moet up to date zijn!!!

Doe dit in terminal:

cd frontend
npm install
npm install vue-router
npm run dev

cd 
De frontend draait dan op: http://localhost:5173/
Dus zet dat ook in aplication.properties

Projectstructuur---
index.html is de start van Vite. De browser opent dus als eerst
index.html. index.html laadt dan src/main.ts in. Dat start Vue op, Vue
Mount op #app. src/App.vue is de hoofdcontainer van de app en
src/router.ts bepaalt welke pagina ingeladen wordt op App.vue
App.vue toont <RouterView/>
De router laadt daarin steeds een pagina-component uit de component map
Pagina’s zijn Vue-componenten, gekoppeld aan routes

Nieuwe pagina toevoegen---
Maak een nieuw .vue bestand in src/components
Voeg de route toe in src/router.ts
Navigeer ernaartoe met <RouterLink> (kijk voorbeeld die ik al gemaakt heb
van Home.vue en Constituencies.vue)

Committen---
Als je nieuwe frontendbestanden hebt toegevoegd doe dan
in de terminal:

git add frontend

Daarmee voeg je de frontendbestanden
toe aan Git zodat ze mee kunnen in een commit.
