# Praca inżynierska [[PL](#aplikacja-internetowa-z-optymalizatorem-harmonogramu-do-wykorzystania-w-salonie-masażu) | [ENG](#web-application-with-schedule-optimizer-for-use-in-massage-salon)]

## Aplikacja internetowa z optymalizatorem harmonogramu do wykorzystania w salonie masażu

### Opis projektu

Repozytorium zawiera projekt pracy inżynierskiej: aplikację webową do zarządzania salonem masażu. System pozwala klientom wyszukiwać firmy i usługi, rejestrować się, rezerwować wizyty oraz otrzymywać rekomendacje masaży na podstawie objawów. Administratorzy firm i pracownicy mogą zarządzać masażami, pokojami, pracownikami, rezerwacjami, dostępnością, statystykami i harmonogramem pracy.

### Struktura projektu i technologie

<div >
  <code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/java.png" alt="Java" title="Java"/></code>
	<code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/spring.png" alt="Spring Boot" title="Spring Boot"/></code>
	<code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/rest.png" alt="REST" title="REST"/></code>
	<code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/angular.png" alt="Angular" title="Angular"/></code>
  <code><img width="45" src="./assets/material.svg" alt="Angular Material" title="Angular Material"/></code>
  <code><img width="48" src="./assets/tailwind.png" alt="Tailwind CSS" title="Tailwind CSS"/></code>
  <code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/docker.png" alt="Docker" title="Docker"/></code>
</div>

- `frontend` - aplikacja Angular 19 uruchamiana na porcie `4200`.
- `backend-root/backend` - główne API Spring Boot 3 uruchamiane na porcie `8080`.
- `backend-root/recommendations` - moduł Spring Boot dla logiki rekomendacji masaży.
- `docker` - konfiguracja Docker Compose dla frontendu, backendu i bazy PostgreSQL.

### Główne funkcje

- Rejestracja, logowanie, wylogowanie, JWT i dostęp oparty o role.
- Rezerwacje wizyt przez klientów z obsługą e-maili.
- Zarządzanie firmami, pracownikami, masażami, pokojami, dostępnością i zmianami.
- Panele administratora i pracownika dla rezerwacji, harmonogramu i statystyk.
- Rekomendacje masaży na podstawie wybranych objawów.
- Obsługa przesyłania zdjęć przez Cloudinary.
- Obsługa Google reCAPTCHA.

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
  userAuthUrl: "http://localhost:8080/api/auth",
  baseUrl: "http://localhost:8080/api",
  recaptcha: {
    siteKey: "twój_recaptcha_site_key",
  },
  cloudinaryCloudName: "twoja_nazwa_cloudinary",
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

4. Uruchom aplikację:

```bash
cd ../../docker
docker-compose up --build
```

Po uruchomieniu aplikacja będzie dostępna pod adresem `http://localhost:4200`.

Zatrzymanie kontenerów:

```bash
docker-compose stop
```

### Licencja

Ten projekt jest udostępniany na licencji **GNU General Public License v3.0** — szczegóły w pliku [`LICENSE`](LICENSE).

### Licencje zewnętrzne

Ten projekt korzysta z następującego oprogramowania zewnętrznego:

- **SMILE (Statistical Machine Intelligence & Learning Engine)**
  Copyright © 2010–2026 Haifeng Li. Licencja: GNU GPL v3.
  Użyte w module `backend-root/recommendations` do algorytmu rekomendacji masaży.
  Źródło: [github.com/haifengl/smile](https://github.com/haifengl/smile)

## Web application with schedule optimizer for use in massage salon

This repository contains an engineering thesis project: a web application for massage salon management. The system lets customers find companies and services, register and book visits, and receive massage recommendations based on symptoms. Company admins and employees can manage massages, rooms, employees, reservations, availability, statistics, and work schedules.

### Project Structure and tech stack

<div >
  <code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/java.png" alt="Java" title="Java"/></code>
	<code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/spring.png" alt="Spring Boot" title="Spring Boot"/></code>
	<code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/rest.png" alt="REST" title="REST"/></code>
	<code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/angular.png" alt="Angular" title="Angular"/></code>
  <code><img width="45" src="./assets/material.svg" alt="Angular Material" title="Angular Material"/></code>
  <code><img width="48" src="./assets/tailwind.png" alt="Tailwind CSS" title="Tailwind CSS"/></code>
  <code><img width="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/docker.png" alt="Docker" title="Docker"/></code>
</div>

- `frontend` - Angular 19 application served on port `4200`.
- `backend-root/backend` - Spring Boot 3 backend API served on port `8080`.
- `backend-root/recommendations` - Spring Boot module for massage recommendation logic.
- `docker` - Docker Compose configuration for the frontend, backend, and PostgreSQL database.

### Main Features

- User registration, login, logout, JWT authentication, and role-based access.
- Customer reservation flow with email support.
- Company, employee, massage, room, availability, and shift management.
- Admin and employee dashboards for reservations, schedules, and statistics.
- Massage recommendations based on selected symptoms.
- Image upload support through Cloudinary.
- Google reCAPTCHA support for protected forms.

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
  userAuthUrl: "http://localhost:8080/api/auth",
  baseUrl: "http://localhost:8080/api",
  recaptcha: {
    siteKey: "your_recaptcha_site_key",
  },
  cloudinaryCloudName: "your_cloudinary_cloud_name",
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

4. Start the application:

```bash
cd ../../docker
docker-compose up --build
```

After startup, the application will be available at `http://localhost:4200`.

Stop the containers with:

```bash
docker-compose stop
```

### License

This project is licensed under the **GNU General Public License v3.0** — see the [`LICENSE`](LICENSE) file for details.

### Third-party licenses

This project uses the following third-party software:

- **SMILE (Statistical Machine Intelligence & Learning Engine)**
  Copyright © 2010–2026 Haifeng Li. License: GNU GPL v3.
  Used in `backend-root/recommendations` for the massage recommendation algorithm.
  Source: [github.com/haifengl/smile](https://github.com/haifengl/smile)
