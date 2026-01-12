# FitTrack

A distributed dockerized FitTrack-ing application

## Prerequisites

- **Docker and Docker compose**
- **Git**

## Database Initialization & Application Execution

### 1. Database Initialization (DEV profile)

The database initialization process (creation of dummy data) is executed only when the application is run with the Spring profile dev.

#### In this mode:

* Phone number validation is bypassed

* SMS sending is mocked

* Dummy data are generated automatically

#### The initialization creates:

* 100 trainers (PersonType.TRAINER) with corresponding TrainerProfile

* 100 clients (PersonType.CLIENT) with corresponding ClientProfile

#### Steps

1. Ensure that the database fittrackdb is empty

(either a fresh database or all tables have been removed).
2. Run the application using the dev Spring profile with docker:

```docker
docker compose -f docker-compose.yml -f docker-compose-dev.yml up --build
```

#### During startup:

* InitializationService is executed automatically

* Dummy entities are inserted into the database

When the process completes:

* The database is ready for use

* Initialization will not run again if trainer profiles already exist

Note:
The dev profile is intended strictly for development and testing purposes.

### 2. Normal Application Run (Default profile)

After the database has been initialized, the application should be executed without the dev profile.

#### In this mode:

* Phone number validation is enabled

* SMS notifications are enabled

* No dummy data are created

#### Run the application

```docker
docker compose up --build
```

## External Services

- **Phone number authentication**
- **Geolocation Service**
- **Weather Tracking Service**
