# Upgrade Plan: upload-tugas (20260916081504)

- **Generated**: 2026-09-16 08:15
- **HEAD Branch**: master
- **HEAD Commit ID**: unavailable from repository status

## Available Tools

**JDKs**
- JDK 25.0.4.1: `C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot\bin` (target runtime and build)

**Build Tools**
- Maven 3.9.16: `C:\Program Files\apache-maven-3.9.16\bin` (build and test)

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20260916081504
- Run tests before and after the upgrade: true

## Upgrade Goals

- Upgrade Java runtime from 17 to 25, the latest LTS release.

## Technology Stack

| Technology/Dependency | Current | Min Compatible Version | Why Incompatible |
| --------------------- | ------- | ---------------------- | ---------------- |
| Java | 17 | 25 | User requested Java 25 LTS |
| Spring Boot | 3.2.5 | 3.2.5 | No framework upgrade requested; retain application behavior |
| Maven | 3.9.16 | 3.9.16 | Compatible with Java 25 |
| spring-boot-maven-plugin | 3.2.5 | 3.2.5 | Managed by Spring Boot parent; no change required |
| Docker build image | `maven:3.8.1-jdk-17` | Java 25 Maven image | Uses the old Java 17 runtime and Maven image |
| Docker runtime image | `openjdk:17-jdk-slim` | Java 25 runtime image | Uses the old Java 17 runtime |

## Derived Upgrades

- Update the Maven `java.version` property to `25` so compiler and Spring Boot plugin configuration target Java 25.
- Update Docker build and runtime base images to Java 25. Maven 3.9.16 is already available locally and is used for verification.
- No Kotlin, Jakarta namespace migration, build-wrapper, CI, or source API changes were detected.

## Impact Analysis

### Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
|------|------------|---------|--------|--------|--------|
| `pom.xml` | `java.version` | `17` | upgrade | `25` | Set Maven compiler and Spring Boot build configuration to the requested LTS |
| `Dockerfile.dockerfile` | Maven build image | `maven:3.8.1-jdk-17` | replace | `maven:3.9.16-eclipse-temurin-25` | Build the container with Java 25 and a current Maven release |
| `Dockerfile.dockerfile` | Runtime image | `openjdk:17-jdk-slim` | replace | `eclipse-temurin:25-jre` | Run the packaged application on Java 25 |

### Source Code Changes

| File | Location | Current | Required Change | Reason |
|------|----------|---------|-----------------|--------|
| None | N/A | No Java 17-specific APIs or internal JDK imports found in the application entry point and configured source surface | No source changes | The requested change is a runtime/compiler target upgrade |

### Configuration Changes

| File | Property/Setting | Current | Required Change | Reason |
|------|------------------|---------|-----------------|--------|
| `pom.xml` | `<java.version>` | `17` | Change to `25` | Maven and Spring Boot compiler configuration must target Java 25 |

### CI/CD Changes

| File | Location | Current | Required Change |
|------|----------|---------|-----------------|
| `Dockerfile.dockerfile` | build and runtime `FROM` lines | Java 17 images | Use Maven/JRE Java 25 images |

### Risks & Warnings

- **Spring Boot 3.2.5 on Java 25**: This framework line predates Java 25 and may expose bytecode, test, or runtime compatibility issues. **Mitigation**: run clean test compilation and the complete test suite on JDK 25; preserve the existing Spring Boot version unless verification proves an upgrade is required.
- **Docker image tag availability**: The selected Java 25 image tags must be available to the deployment environment. **Mitigation**: build verification covers the Maven project locally; validate the Docker image separately if Docker is installed.
- **Pre-existing worktree changes**: The repository has staged project files and an unstaged `.gitignore` modification. **Mitigation**: use the upgrade version-control preparation operation, which preserves uncommitted work before creating the upgrade branch.

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: Confirm Java 25 and Maven 3.9.16 are available before changing project files.
  - **Changes to Make**: No project-file changes; use the already installed toolchain.
  - **Verification**: JDK and Maven discovery; expected Java 25 and Maven 3.9.16 available.

- Step 2: Setup Baseline
  - **Rationale**: Establish the current Java 17 test baseline when the base JDK is available.
  - **Changes to Make**: No project-file changes.
  - **Verification**: `mvn clean compile test-compile -q && mvn clean test -q` using Java 17. Base JDK is not installed, so this step is skipped and no pre-upgrade pass rate can be recorded.

- Step 3: Upgrade Maven Java Target and Container Runtime
  - **Rationale**: Apply all required Java 25 configuration changes in one compilable step.
  - **Changes to Make**: Apply Dependency Changes and Configuration Changes above in `pom.xml` and `Dockerfile.dockerfile`.
  - **Verification**: `mvn clean test-compile -q` using JDK 25; expected main and test sources compile successfully.

- Step 4: Final Validation
  - **Rationale**: Confirm the Java 25 build and tests meet the upgrade success criteria.
  - **Changes to Make**: Resolve any compilation or test failures caused by the upgrade; ensure no temporary TODOs or workarounds remain.
  - **Verification**: `mvn clean test-compile -q` and `mvn clean test -q` using JDK 25; expected 100% test pass rate.

- Step 5: CVE Validation and Fix
  - **Rationale**: Check resolved direct dependencies for known vulnerabilities after the upgrade.
  - **Changes to Make**: Upgrade only vulnerable dependencies with available compatible patches; retain Spring Boot-managed versions unless a CVE requires a change.
  - **Verification**: Dependency scan, clean test compilation, and a second dependency scan confirming fixes.

- Step 6: Final Validation and Summary
  - **Rationale**: Record final build, test, coverage, and security results.
  - **Changes to Make**: Generate progress and summary artifacts; remove temporary files and empty placeholders.
  - **Verification**: `mvn clean verify -Djacoco.skip=false` using JDK 25 where coverage support is available; all required project steps completed successfully.
