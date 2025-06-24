# Tech Screening Java

## Welkom

Welkom bij je eigen Team Rockstars IT TechScreening repo! Dit is je kans om ons te laten zien welke Rockstarwaardige development skills jij bezit en
pak gewoon de kans om te shinen.

## Spelregels

* Houd je aan de regels vermeld in de techcase, bijvoorbeeld besteed niet meer dan 4 uur aan je case en lever deze tenminste 48 uur van tevoren in.

* Alleen jij, de recruiter en de TechScreener hebben toegang tot deze repo. Houd deze dus ook private zodat niemand anders kan piggybacken op jouw Dev
  skills.

## Tips

Als je rekening houdt met het volgende gaat zeker goed komen:

* Lees de case echt goed door, je kunt op vele manieren indruk op ons maken.

* Je bent vrij de repo naar eigen inzicht in te richten.

* Probeer echt een staaltje te laten zien van wat jij in huis hebt.

* Kwaliteit is in deze beter dan kwantiteit, dus zorg dat hetgeen dat af is ook goed is.

* De json bestanden die je nodig hebt staan hier:

    * [artists.json](https://raw.githubusercontent.com/Team-Rockstars-IT/MusicLibrary/v1.0/artists.json)

    * [songs.json](https://raw.githubusercontent.com/Team-Rockstars-IT/MusicLibrary/v1.0/songs.json)

Succes! 🤘

---

## Deployment

The project includes a convenient deployment script (`deploy.sh`) that automatically sets up the entire application stack with PostgreSQL database.

### Prerequisites

**Pre-requisites**

- Java 21+ (OpenJDK or Oracle JDK)
- Maven 3.9+
- Docker & Docker Compose

**Installation (MacOS with Homebrew):**

### Java 21

> ```bash 
> brew install openjdk@21

### Maven

> ```bash 
> brew install maven

### Docker

> ```bash 
> brew install docker docker-compose

### Verify installations

> ```bash 
> java --version # Should show Java 21 mvn --version # Should show Maven 3.9+ docker --version python3 --version

## Running the Project

> **⚠️ IMPORTANT:** Before running the script, make it executable:

> ```bash
> chmod +x deploy.sh 
>

> ```bash
> ./deploy.sh --app-port=8080 --swagger=true --clean
>

**Deploy script options:**

Options:  
--app-port=<port> Set the port for the API (default: 8080)  
--swagger=true/false Enable Swagger UI (default: true)      
--clean Start with clean database state

### OpenAPI & Swagger Documentation

The project includes comprehensive API documentation through OpenAPI 3.0.3 specification:

- **OpenAPI Definition**: Located at `rockstarsit/src/main/resources/api.yaml`
- **Swagger UI**: Interactive documentation available at http://localhost:8080/swagger-ui/index.html
- **API Testing**: Use Swagger UI to test endpoints directly in your browser