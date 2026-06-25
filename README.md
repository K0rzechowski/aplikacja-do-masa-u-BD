# Praca inżynierska [[PL](#aplikacja-internetowa-z-optymalizatorem-harmonogramu-do-wykorzystania-w-salonie-masażu) | [ENG](#web-application-with-schedule-optimizer-for-use-in-massage-salon)]

## Aplikacja internetowa z optymalizatorem harmonogramu do wykorzystania w salonie masażu

### Opis projektu

Repozytorium zawiera projekt pracy inżynierskiej: aplikację webową do zarządzania salonem masażu. System pozwala klientom wyszukiwać firmy i usługi, rejestrować się, rezerwować wizyty oraz otrzymywać rekomendacje masaży na podstawie objawów. Administratorzy firm i pracownicy mogą zarządzać masażami, pokojami, pracownikami, rezerwacjami, dostępnością, statystykami i zoptymalizowanym harmonogramem pracy.

### Struktura projektu

- `frontend` - aplikacja Angular 19 uruchamiana na porcie `4200`.
- `backend-root/backend` - główne API Spring Boot 3 uruchamiane na porcie `8080`.
- `backend-root/recommendations` - moduł Spring Boot dla logiki rekomendacji masaży.
- `docker` - konfiguracja Docker Compose dla frontendu, backendu, bazy PostgreSQL i usługi OptaPlanner.

### Główne funkcje

- Rejestracja, logowanie, wylogowanie, JWT i dostęp oparty o role.
- Rezerwacje wizyt przez klientów z obsługą e-maili.
- Zarządzanie firmami, pracownikami, masażami, pokojami, dostępnością i zmianami.
- Panele administratora i pracownika dla rezerwacji, harmonogramu i statystyk.
- Rekomendacje masaży na podstawie wybranych objawów.
- Obsługa przesyłania zdjęć przez Cloudinary.
- Obsługa Google reCAPTCHA.
- Optymalizacja harmonogramu przez zewnętrzną usługę OptaPlanner.

### Wymagania

- Java 21 i Maven (lub dołączony Maven Wrapper).
- Docker i Docker Compose.

### Konfiguracja

Przed uruchomieniem aplikacji utwórz i uzupełnij wszystkie wymagane pliki konfiguracyjne. Nie zostawiaj wartości przykładowych typu `XXX` albo `XXXX`.

#### Środowisko Docker

Utwórz `docker/.env` na podstawie `docker/.env.dist`:

```bash
cd docker
cp .env.dist .env
```

Uzupełnij dane bazy używane przez PostgreSQL i backend Spring:

```dotenv
POSTGRES_USER=twój_użytkownik_bazy
POSTGRES_PASSWORD=twoje_hasło_bazy
POSTGRES_DB=twoja_nazwa_bazy
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/${POSTGRES_DB}
SPRING_DATASOURCE_USERNAME=twój_użytkownik_bazy
SPRING_DATASOURCE_PASSWORD=twoje_hasło_bazy
```

Docker Compose przekazuje też dane datasource do usługi OptaPlanner/Quarkus. Dodaj do `docker/.env` również:

```dotenv
QUARKUS_DATASOURCE_URL=jdbc:postgresql://db:5432/${POSTGRES_DB}
QUARKUS_DATASOURCE_USERNAME=twój_użytkownik_bazy
QUARKUS_DATASOURCE_PASSWORD=twoje_hasło_bazy
```

#### Środowisko backendu

Utwórz `backend-root/backend/src/main/resources/.env` na podstawie szablonu:

```bash
cd backend-root/backend/src/main/resources
cp .env.dist .env
```

Uzupełnij wszystkie wartości używane przez `application.properties`:

```dotenv
ENCRYPTION_KEY=twój_256_bitowy_klucz_hex
EMAIL=example@gmail.com
PASSWORD=twoje_hasło_aplikacji_gmail
CLOUDINARY_API_KEY=twój_cloudinary_api_key
CLOUDINARY_API_SECRET=twój_cloudinary_api_secret
CLOUDINARY_CLOUD_NAME=twój_cloudinary_cloud_name
RECAPTCHA_KEY_SITE=twój_recaptcha_site_key
RECAPTCHA_KEY_SECRET=twój_recaptcha_secret_key
```

#### Środowisko frontendu

Utwórz plik środowiskowy Angulara na podstawie szablonu:

```bash
cd frontend/src/environments
cp environment.ts.dist environment.ts
```

Ustaw adresy backendu, klucz reCAPTCHA i nazwę Cloudinary:

```ts
export const environment = {
  production: true,
  userAuthUrl: 'http://localhost:8080/api/auth',
  baseUrl: 'http://localhost:8080/api',
  recaptcha: {
    siteKey: 'twój_recaptcha_site_key',
  },
  cloudinaryCloudName: 'twoja_nazwa_cloudinary',
};
```

### Uruchomienie przez Docker

Obraz backendu oczekuje skompilowanego pliku JAR w `backend-root/backend/target`, dlatego przed uruchomieniem Dockera trzeba skompilować backend.

1. Sklonuj repozytorium:

```bash
git clone git@github.com:emilia-nodz/praca-inzynierska.git
cd praca-inzynierska
```

2. Uzupełnij wszystkie pliki `.env`, `environment.ts` i wartości `application.properties` opisane wyżej.

3. Skompiluj backend:

```bash
cd backend-root/backend
./mvnw clean package -DskipTests
```

4. Przygotuj zewnętrzną usługę OptaPlanner employee scheduling.

   Usługa pochodzi z repozytorium [emilia-nodz/optaplanner-quickstarts](https://github.com/emilia-nodz/optaplanner-quickstarts/tree/myProject) na branchu `myProject`.

   Sklonuj repozytorium jako folder sąsiedni do `praca-inzynierska` (Docker Compose używa ścieżki `../../optaplanner-quickstarts/use-cases/employee-scheduling` jako build context z poziomu folderu `docker`):

```bash
cd ../../..
git clone -b myProject https://github.com/emilia-nodz/optaplanner-quickstarts.git
cd optaplanner-quickstarts/use-cases/employee-scheduling
mvn clean package -Dnet.bytebuddy.experimental=true -DskipTests
```

Oczekiwana struktura katalogów po klonowaniu:

```
katalog_nadrzędny/
├── praca-inzynierska/
│   └── docker/
└── optaplanner-quickstarts/
    └── use-cases/
        └── employee-scheduling/
```

5. Uruchom aplikację:

```bash
cd ../../praca-inzynierska/docker
docker-compose up --build
```

Po uruchomieniu aplikacja będzie dostępna pod adresem `http://localhost:4200`.

Zatrzymanie kontenerów:

```bash
docker-compose stop
```

### Uwagi

- Backend wywołuje serwis OptaPlanner wewnętrznie pod adresem `http://optaplanner:8081` — nie zmieniaj tej wartości w konfiguracji.

## Web application with schedule optimizer for use in massage salon

This repository contains an engineering thesis project: a web application for massage salon management. The system lets customers find companies and services, register and book visits, and receive massage recommendations based on symptoms. Company admins and employees can manage massages, rooms, employees, reservations, availability, statistics, and optimized work schedules.

### Project Structure

- `frontend` - Angular 19 application served on port `4200`.
- `backend-root/backend` - Spring Boot 3 backend API served on port `8080`.
- `backend-root/recommendations` - Spring Boot module for massage recommendation logic.
- `docker` - Docker Compose configuration for the frontend, backend, PostgreSQL database, and OptaPlanner employee scheduling service.

### Main Features

- User registration, login, logout, JWT authentication, and role-based access.
- Customer reservation flow with email support.
- Company, employee, massage, room, availability, and shift management.
- Admin and employee dashboards for reservations, schedules, and statistics.
- Massage recommendations based on selected symptoms.
- Image upload support through Cloudinary.
- Google reCAPTCHA support for protected forms.
- Schedule optimization through an external OptaPlanner employee scheduling service.

### Requirements

- Java 21 and Maven (or the included Maven wrapper).
- Docker and Docker Compose.

### Configuration

Before running the application, create and fill every required configuration file. Do not leave placeholder values such as `XXX` or `XXXX`.

#### Docker Environment

Create `docker/.env` from `docker/.env.dist`:

```bash
cd docker
cp .env.dist .env
```

Fill database values used by PostgreSQL and the Spring backend:

```dotenv
POSTGRES_USER=your_database_user
POSTGRES_PASSWORD=your_database_password
POSTGRES_DB=your_database_name
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/${POSTGRES_DB}
SPRING_DATASOURCE_USERNAME=your_database_user
SPRING_DATASOURCE_PASSWORD=your_database_password
```

The Docker Compose file also passes datasource values to the OptaPlanner/Quarkus service. Add these to `docker/.env` as well:

```dotenv
QUARKUS_DATASOURCE_URL=jdbc:postgresql://db:5432/${POSTGRES_DB}
QUARKUS_DATASOURCE_USERNAME=your_database_user
QUARKUS_DATASOURCE_PASSWORD=your_database_password
```

#### Backend Environment

Create `backend-root/backend/src/main/resources/.env` from the provided template:

```bash
cd backend-root/backend/src/main/resources
cp .env.dist .env
```

Fill all values used by `application.properties`:

```dotenv
ENCRYPTION_KEY=your_256_bit_hex_key
EMAIL=example@gmail.com
PASSWORD=your_gmail_application_password
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
RECAPTCHA_KEY_SITE=your_recaptcha_site_key
RECAPTCHA_KEY_SECRET=your_recaptcha_secret_key
```

#### Frontend Environment

Create the Angular environment file from the template:

```bash
cd frontend/src/environments
cp environment.ts.dist environment.ts
```

Set the backend URLs, reCAPTCHA site key, and Cloudinary cloud name:

```ts
export const environment = {
  production: true,
  userAuthUrl: 'http://localhost:8080/api/auth',
  baseUrl: 'http://localhost:8080/api',
  recaptcha: {
    siteKey: 'your_recaptcha_site_key',
  },
  cloudinaryCloudName: 'your_cloudinary_cloud_name',
};
```

### Running With Docker

The Docker backend image expects a compiled Spring Boot JAR in `backend-root/backend/target`, so compile the backend before starting Docker.

1. Clone this repository:

```bash
git clone git@github.com:emilia-nodz/praca-inzynierska.git
cd praca-inzynierska
```

2. Configure all `.env`, `environment.ts`, and `application.properties` values described above.

3. Compile the backend:

```bash
cd backend-root/backend
./mvnw clean package -DskipTests
```

4. Prepare the external OptaPlanner employee scheduling service.

   The service comes from the [emilia-nodz/optaplanner-quickstarts](https://github.com/emilia-nodz/optaplanner-quickstarts/tree/myProject) repository, branch `myProject`.

   Clone it as a sibling folder next to `praca-inzynierska` (Docker Compose uses `../../optaplanner-quickstarts/use-cases/employee-scheduling` as the build context from inside the `docker` directory):

```bash
cd ../../..
git clone -b myProject https://github.com/emilia-nodz/optaplanner-quickstarts.git
cd optaplanner-quickstarts/use-cases/employee-scheduling
mvn clean package -Dnet.bytebuddy.experimental=true -DskipTests
```

Expected directory structure after cloning:

```
parent_directory/
├── praca-inzynierska/
│   └── docker/
└── optaplanner-quickstarts/
    └── use-cases/
        └── employee-scheduling/
```

5. Start the application:

```bash
cd ../../praca-inzynierska/docker
docker-compose up --build
```

After startup, the application will be available at `http://localhost:4200`.

Stop the containers with:

```bash
docker-compose stop
```

### Notes

- The backend calls the OptaPlanner service internally at `http://optaplanner:8081` — do not change this value in configuration.
