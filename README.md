# IoTSystem (Backend)

This README introduces the IoTSystem backend — a kit lending/return management system for educational use (Capstone project). It helps a reviewer or hiring manager quickly understand the architecture, how to run the project, API conventions, important data relationships, and common deployment/troubleshooting notes.

---

## Table of Contents

- Overview
- Main features
- Architecture & Technologies
- Project structure
- API response standard and examples
- Quick start (Windows)
- Configuration / application.properties
- Database (Postgres) & Azure deployment notes
- Swagger / API docs (deployed)
- Important data relationships (Wallet, Account, Kit, KitComponent)
- Common errors and quick fixes
- Best practices applied and recommendations
- Contributing
- Contact

---

## Overview

IoTSystem is a Spring Boot backend (Maven) that manages accounts, roles, wallets, kits and kit components. It includes JWT authentication and exposes Swagger API documentation. This README is intended for reviewers and recruiters to run, inspect, and understand the project quickly.

## Main features

- Account management (register, login, role assignment)
- Wallets: auto-created for STUDENT/LECTURER roles; view balance; deposit/withdraw transactions
- Kits & KitComponents: CRUD operations (admin-only)
- Bulk account import from Excel (XLS/XLSX)
- Transaction history, notifications, damage reports, penalty & payment modules
- Swagger UI for interactive API exploration

## Architecture & Technologies

- Java 17+ (see `pom.xml`)
- Spring Boot (Web, Data JPA, Security)
- Hibernate (JPA)
- PostgreSQL
- Apache POI (Excel import)
- JWT for authentication
- Lombok
- Maven (wrapper `mvnw` / `mvnw.cmd`)

## Project structure (important folders)

- `src/main/java/IotSystem/IoTSystem/` - main application code
  - `Controller/` - REST controllers
  - `Service/` - business logic (interfaces & implementations)
  - `Repository/` - JPA repositories
  - `Model/Entities` - JPA entities and enums
  - `Config/` - Security, Swagger, Timezone, Multipart configuration
- `src/main/resources/application.properties` - configuration
- `pom.xml` - build & dependencies

## API response standard and example

This project uses a standard `ApiResponse<T>` wrapper with fields: `status`, `message`, `data`, `timestamp`.

Example (view wallet response):

```json
{
  "status": "Ok",
  "message": "Fetched wallet successfully",
  "data": {
    "walletId": "1de7e30d-b65c-4e40-b991-072b2f7b3ce5",
    "balance": 0,
    "currency": "VND",
    "note": null,
    "active": true
  },
  "timestamp": "2025-10-04T16:28:40.4474057"
}
```

Guidelines: always use DTOs (request/response) rather than returning JPA entities directly, keep a consistent response format for success and errors, and validate request payloads with `@Valid` and Bean Validation.

## Quick start (Windows - cmd.exe)

Requirements: Java 17+, Maven wrapper is included.

1. Clone the repo
2. Configure `src/main/resources/application.properties` (see Database section)
3. Run locally (Windows cmd):

```cmd
cd C:\Users\user\Desktop\Capstone_BE\IoTSystem\IoTSystem
mvnw.cmd clean package -DskipTests
mvnw.cmd spring-boot:run
```

Or build then run the JAR:

```cmd
mvnw.cmd package -DskipTests
java -jar target\<artifact-name>.jar
```

Note: Windows uses `mvnw.cmd` (not `./mvnw`).

## Configuration / application.properties

Essential properties (example):

- `spring.datasource.url=jdbc:postgresql://<HOST>:5432/<DBNAME>`
- `spring.datasource.username=<DB_USER>`
- `spring.datasource.password=<DB_PASSWORD>`
- `spring.jpa.hibernate.ddl-auto=update` (use `validate` or `none` in production)

Important: special characters in passwords (e.g. `#`, `$`, `;`) are allowed in property values after `=`, but when passing secrets via environment variables or command line they may need escaping depending on the shell. When deploying to Azure, prefer storing secrets in Azure App Service configuration or Key Vault rather than in plain `application.properties`.

## Database (Postgres) & Azure deployment notes

This project is frequently deployed to Azure App Service and uses Azure Database for PostgreSQL for production.

Example production configuration for Azure Database for PostgreSQL:

```
spring.datasource.url=jdbc:postgresql://<your-server>.postgres.database.azure.com:5432/<db-name>?sslmode=require
spring.datasource.username=<db-user>@<your-server>
spring.datasource.password=<your-db-password>
```

Common Azure-specific cautions:
- Azure DB often requires the username format `user@servername`.
- Ensure the client IP (or Azure app service) is allowed in the server firewall settings.
- If the server enforces SSL, use `sslmode=require` or configure a proper SSL setup.
- If you receive `FATAL: password authentication failed for user`, verify the username, password, firewall rules, and whether SSL is required.

## Swagger / API docs (deployed)

If the application is deployed to Azure App Service at `https://<your-app>.azurewebsites.net`, the API docs will be available at:

- Swagger UI: `https://<your-app>.azurewebsites.net/swagger-ui.html`
- OpenAPI JSON: `https://<your-app>.azurewebsites.net/v3/api-docs`

If you see a `500` error when fetching `/v3/api-docs`, check application logs for the root cause. Common causes include:
- Missing Bean Validation provider (add `org.hibernate.validator:hibernate-validator` to `pom.xml`)
- Spring Security blocking the swagger endpoints (make sure to `permitAll()` the swagger paths prior to `anyRequest()` in security config)
- Serialization errors caused by entities/enums exposed directly (use DTOs or add proper Jackson annotations)

## Important data relationships (Wallet & Account)

You reported an error when creating an account + wallet at the same time: `null value in column "account_id" of relation "wallet" violates not-null constraint`.

Root cause: one-to-one ownership side is misaligned with how you persist objects. Choose one canonical mapping and persist on the owner side:

Option A (recommended): Wallet is the owner (foreign key `account_id` in `wallet` table)

- Wallet entity:
  ```text
  @OneToOne
  @JoinColumn(name = "account_id", nullable = false, unique = true)
  private Account account;
  ```
- Account entity:
  ```text
  @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
  private Wallet wallet;
  ```
- Creation flow: set both sides (wallet.setAccount(account); account.setWallet(wallet)) and save the owner side (saving account with cascade to wallet is OK if mappings are consistent).

Option B: Account is the owner (foreign key `wallet_id` in `accounts` table)

- Account entity:
  ```text
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "wallet_id", unique = true)
  private Wallet wallet;
  ```
- Wallet entity:
  ```text
  @OneToOne(mappedBy = "wallet")
  private Account account;
  ```

Recommendation: pick Option A for clearer semantics (a wallet belongs to an account). If you change the ownership, apply a proper DB migration (do not rely on `hibernate.ddl-auto=update` for production schema migrations).

Migration example (manual steps):

```sql
ALTER TABLE wallet ADD COLUMN account_id uuid UNIQUE;
-- populate account_id appropriately
ALTER TABLE wallet ALTER COLUMN account_id SET NOT NULL;
ALTER TABLE wallet ADD CONSTRAINT fk_wallet_account FOREIGN KEY (account_id) REFERENCES accounts(id);
```

## Enum mapping and check constraint errors (KitComponent)

You may encounter errors like: `violates check constraint kit_components_component_type_check` or Jackson errors such as `Cannot deserialize value of type ... Enum from String "WHITE"`.

Root causes and fixes:
- If the DB column stores enum names (varchar) and has a check constraint, ensure the entity uses `@Enumerated(EnumType.STRING)` so JPA persists the enum as its name.
- If the DB column is numeric (smallint) and the constraint expects numbers, either change the DB column to varchar or map the enum to an integer value explicitly. Using ordinals is fragile because reordering enums changes DB meaning — prefer `EnumType.STRING`.

Example mapping (recommended):

```text
@Enumerated(EnumType.STRING)
@Column(name = "component_type")
private KitItemsTypes componentType;
```

If Hibernate tries to alter column types automatically and fails with "cannot be cast automatically", write a migration SQL with `USING` or change the column type manually and migrate values.

## Security & `Can't configure mvcMatchers after anyRequest` error

This error happens when matchers are configured after `anyRequest()` in `SecurityFilterChain`. Ensure endpoint matchers (including swagger paths) are declared before `anyRequest().authenticated()`.

Example order (plain text, avoid code-like punctuation):

- First, permit public API paths such as `/api/**`.
- Second, permit Swagger and OpenAPI endpoints such as `swagger-ui.html` and `/v3/api-docs/**`.
- Finally, require authentication for any other request (i.e. `anyRequest` should come last).

Also ensure your JWT filter is added at the correct position and doesn't block swagger endpoints.

## Common errors & quick fixes (summary)

- Swagger 500 -> check stacktrace; add Hibernate Validator; permit swagger paths in security
- Wallet null `account_id` -> fix one-to-one ownership and set both sides before save
- Enum constraint/deserialize errors -> use `@Enumerated(EnumType.STRING)` and align DB column
- Azure DB auth failed -> verify username format, password, firewall, and SSL requirements
- Hibernate automatic DDL errors when changing enum storage -> write migration SQL using `USING` or migrate to varchar

## Tests & quality checks

Run unit/integration tests (if present):

```cmd
mvnw.cmd test
```

If you see `Failed to set up a Bean Validation provider: NoProviderFoundException`, add Hibernate Validator to `pom.xml`:

```xml
<dependency>
  <groupId>org.hibernate.validator</groupId>
  <artifactId>hibernate-validator</artifactId>
</dependency>
```

## Best practices applied and recommendations

- DTOs for request/response; consistent `ApiResponse<T>` wrapper
- Use `@Valid` and Bean Validation for input validation
- Use `@Transactional` in services for multi-step operations (create account + wallet)
- Use CascadeType where appropriate, but always persist from the owner side of relationships
- Do not store secrets in source-controlled config; use Azure App Service configuration or Key Vault
- Use schema migrations (Flyway / Liquibase) for production
- Avoid Enum ordinal persistence; prefer `EnumType.STRING`

## Deployment notes (Azure App Service)

- Configure the application settings in Azure App Service for the database URL, username and password
- Allow the App Service outbound IP(s) in Azure Database firewall
- Prefer `spring.jpa.hibernate.ddl-auto=validate` for production and use migrations
- Store sensitive keys in Azure Key Vault and reference them via App Service settings or managed identities

## Contributing

1. Fork the repository
2. Create a branch: `feature/<your-feature>`
3. Commit changes with clear messages
4. Open a pull request for review
5. Run local checks: `mvnw.cmd clean verify`

## Contact

Open an issue on GitHub or contact me via the profile for further questions.

---

This README is ready for use on GitHub. Replace placeholder values (`<your-app>`, `<your-server>`, `<db-name>`, etc.) with your actual deployment names/values before sharing publicly.
