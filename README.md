# CryptoCoin

CryptoCoin is a modern Kotlin Multiplatform (KMP) application designed to monitor cryptocurrency markets, track coin details and price histories, and manage local crypto portfolios.

## 🚀 Platforms
- **Android**
- **iOS**

---

## ✨ Features
- **Cryptocurrency Market Tracking**: Browse live cryptocurrency prices, rankings, details, and price history charts via the CoinRanking API.
- **Portfolio Management**: Securely track your portfolio holdings and user balance using local persistence powered by **Room**.
- **Interactive UI**: Built with **Jetpack Compose Multiplatform** and Material 3, featuring custom performance charts and light/dark theme support.
- **Dependency Injection**: Modular architecture using **Koin**.
- **Robust Error Handling**: Domain-driven Result types and error mappers.

---

## 🛠️ Tech Stack
- **Kotlin Multiplatform (KMP)** & **Compose Multiplatform** (UI)
- **Ktor Client** (Networking & JSON serialization)
- **Room & SQLite** (Local database & persistence)
- **Koin** (Dependency Injection)
- **BuildKonfig** (API key & configuration management)
- **KotlinX Coroutines** & **Kotlin Test**
- **KotlinX Kover** (Code coverage analysis)

---

## 🤖 CI/CD & Testing

This project utilizes **GitHub Actions** for continuous integration. The CI workflow automatically runs unit tests and measures code coverage using **KotlinX Kover** on every push and pull request.

- **Workflow File**: [.github/workflows/ci.yml](.github/workflows/ci.yml)
- **Steps executed in CI**:
  1. Checks out the repository.
  2. Sets up JDK 17 (Temurin) with Gradle caching.
  3. Grants execution permissions to `gradlew`.
  4. Runs all unit tests and generates code coverage reports (`./gradlew testDebugUnitTest koverHtmlReport`).
  5. Uploads the Kover HTML coverage report as a workflow artifact.

To run unit tests and generate coverage reports locally:
```bash
./gradlew testDebugUnitTest koverHtmlReport
```
Coverage reports will be generated at `composeApp/build/reports/kover/html/index.html`.

---

## 📂 Project Structure

- `composeApp/`: Shared Kotlin Multiplatform module containing domain logic, data sources, viewmodels, and Compose UI.
  - `commonMain/`: Shared business logic, UI screens, viewmodels, Room DAOs, and Ktor data sources.
  - `commonTest/`: Unit tests for ViewModels, repositories, and data mappers.
- `iosApp/`: iOS entry point application (SwiftUI integration).

---

## ⚙️ Configuration

API keys (such as `COIN_RANKING_API_KEY`) are managed via [BuildKonfig](https://github.com/codingfeline/BuildKonfig). You can supply your API key in a root `local.properties` file:
```properties
COIN_RANKING_API_KEY=your_api_key_here
```
