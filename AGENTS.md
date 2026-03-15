# Repository Guidelines

## Project Structure & Module Organization
`web/` contains the React + TypeScript frontend built with Vite. Keep page-level screens in `web/src/pages`, shared UI in `web/src/components`, API helpers in `web/src/api`, hooks in `web/src/hooks`, and CSS modules in `web/src/styles`.

`api/` is a Maven multi-module backend. `api/core` holds domain models and core business rules, `api/app` implements use cases, `api/database` manages JDBC and Flyway migrations, and `api/web` exposes the Spring Boot HTTP layer. Project notes live in `doc/`, and local infrastructure is defined in `docker-compose.yml` and `nginx/`.

## Build, Test, and Development Commands
Run `make setup` once to create `.env` from `.env.example`.

Use `docker-compose up -d` to start MySQL, API, and web services together, and `docker-compose down` to stop them. For frontend-only work, run `cd web && npm install && npm run dev`. Build production assets with `cd web && npm run build`, lint with `cd web && npm run lint`, and format with `cd web && npm run format`.

For backend work, run `cd api && mvn test` for the full module test suite. Start the API locally with `cd api && mvn spring-boot:run`, or start only MySQL with `docker-compose up -d mysql` if you are running the API from an IDE.

## Coding Style & Naming Conventions
Frontend code uses Prettier and ESLint. Follow the checked-in Prettier rules: single quotes, no semicolons, trailing commas where valid, and a 100-character print width. Use PascalCase for React components and pages (`GroupPage.tsx`), camelCase for hooks and utilities (`useGroup.ts`), and keep styles in `*.module.css`.

Java code uses standard 4-space indentation, package names under `com.godutch`, and descriptive class names ending with their role, such as `*Controller`, `*Presenter`, `*Repository`, and `*Impl`.

## Testing Guidelines
Backend tests use JUnit 5, with Mockito in `api/app`. Place tests under each module’s `src/test/java` and name them `*Test.java`. Cover domain rules in `api/core` and persistence behavior in `api/database`. No frontend test runner is configured yet, so at minimum run `npm run lint` and manually verify key flows in the browser.

## Commit & Pull Request Guidelines
Recent history follows short, imperative prefixes such as `doc:`, `release:`, and merge commits from feature branches. Prefer `type: summary` messages, for example `feat: add settlement endpoint` or `fix: validate duplicate member names`.

Pull requests should include a concise description, linked issue or task if applicable, local verification steps, and screenshots for UI changes. Call out schema or environment changes explicitly.
