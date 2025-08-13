# Praca inżynierska

## "Aplikacja internetowa z optymalizatorem harmonogramu do wykorzystania w salonie masażu"

## Opis
Celem pracy jest implementacja aplikacji webowej umożliwiającej firmom oferującym usługi masażu zarządzanie harmonogramami masażystów, rezerwacjami klientów oraz dostarczanie spersonalizowanych
rekomendacji masaży w oparciu o zgłaszane symptomy. Aplikacja będzie służyć zarówno
właścicielom firm, jak i klientom, ułatwiając proces organizacji i rezerwacji wizyt.

## Instalacja
1. W terminalu:
    ```bash
    git clone git@github.com:emilia-nodz/praca-inzynierska.git
    ```
    
2. W folderze *docker* stwórz plik *.env* i uzupełnij go zawartością z pliku *.env.dist*, wpisując swoje dane konfiguracyjne do bazy
    ```bash
    cp .env.dist .env
    ```
    Przykładowe uzupełnienie:    
    > POSTGRES_USER=my_user_name

3. W folderze *backend* skompiluj projekt
    ```bash
    ./mvnw clean package -DskipTests
    ```

4. Zbuduj obraz w folderze *docker*
    ```bash
    cd docker
    docker-compose up --build
    ```

5. W celu zatrzymania projektu
    ```bash
    docker-compose stop
    ```

# Engineering thesis

## "Web application with schedule optimizer for use in massage salon"

## Description
The goal of this project is to implement a web application that will enable massage businesses to manage massage therapist schedules, client bookings, and provide personalized massage recommendations based on reported symptoms. The application will serve both business owners and clients, simplifying the process of arranging and booking appointments.

## Installation
1. In terminal:
    ```bash
    git clone git@github.com:emilia-nodz/praca-inzynierska.git
    ```

2. In folder *docker* create file *.env* and into the file write the contents of *.env.dist* file, writing your own credentials to connect to the database
    ```bash
    cp .env.dist .env
    ```
    Example configuration:
    > POSTGRES_USER=my_user_name


3. In folder *backend* compile the Java project
    ```bash
    ./mvnw clean package -DskipTests
    ```

4. Build the docker container in *docker* folder
    ```bash
    cd docker
    docker-compose up --build
    ```

5. In order to stop the container use:
    ```bash
    docker-compose stop
    ```