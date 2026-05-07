# Keycloak local setup

## Full project stack

Run the whole project with Keycloak included:

```bash
docker compose up -d --build
```

Keycloak admin console:

- URL: `http://localhost:8081`
- username: `admin`
- password: `admin`

Project containers from the main compose file:

- `postgres` - application database
- `keycloak-db` - dedicated Keycloak database
- `keycloak` - Keycloak server
- `app` - Spring application

## Keycloak only

Run Keycloak separately when you want to configure realms, clients and users without starting the whole project:

```bash
docker compose -f docker-compose.keycloak-local.yml up -d
```

Stop the standalone Keycloak stack:

```bash
docker compose -f docker-compose.keycloak-local.yml down
```

If you want to delete Keycloak data and start from scratch:

```bash
docker compose -f docker-compose.keycloak-local.yml down -v
```

## Notes

- Keycloak runs with PostgreSQL, not with the embedded dev storage.
- Host port `8081` is used for Keycloak so it does not conflict with the Spring app on `8080`.
- Bootstrap admin credentials are intended only for local development. Change them before using the setup anywhere shared.
