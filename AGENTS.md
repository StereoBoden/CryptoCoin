# AI Code Reviewer Guidelines & Instructions

This repository is a **Kotlin Multiplatform (KMP)** project targeting Android and iOS, built with **Compose Multiplatform**, **Ktor**, **Room Multiplatform**, **Koin**, and **Coroutines / Flow**.

When conducting code reviews, AI reviewers must adhere to the rules below to avoid false positive findings and maintain high review quality.

---

## 🛑 Do NOT Flag as Issues (False Positives to Ignore)

### 1. Jetpack Compose & UI
* **Implicit Return Types**: Do NOT flag `@Composable` functions for lacking an explicit return type (`Unit`).
* **Preview Functions**: Do NOT flag `@Preview` or `@LightDarkPreview` annotated functions as unused code.
* **State Hoisting & Lambdas**: Accepting immutable state data classes and event callback lambdas (e.g. `onAction: (CoinListEvent) -> Unit`) is intentional Compose design. Do NOT suggest using `MutableState` fields as parameters.
* **Modifier Parameter Rules**: Standard composables should accept `modifier: Modifier = Modifier` as their first optional parameter. Do NOT flag this default parameter value.

### 2. Kotlin & Multiplatform Conventions
* **Idiomatic Test Method Names**: Do NOT flag backticks in unit test function names (e.g. `` `when search query changes then state updates` ``).
* **Expect / Actual Declarations**: Do NOT flag `expect` class/function declarations in `commonMain` as missing implementations. Their `actual` implementations reside in platform-specific modules (`androidMain`, `iosMain`).
* **Koin Modules**: Do NOT flag declarations inside Koin `module { ... }` blocks in `Module.kt` as unused variables or unreferenced code.

### 3. Coroutines & Data Flows
* **ViewModel StateFlow**: `StateFlow` and `SharedFlow` exposed by ViewModels are meant to be observed by UI components using `collectAsStateWithLifecycle()` or `collectAsState()`. Do NOT flag them as unhandled flows.
* **HttpClient Lifecycle**: Ktor `HttpClient` instances managed by Koin DI or application-scoped singletons do not need manual `close()` calls in every data source.

### 4. Room Database
* **Room DAOs**: Do NOT flag DAO interfaces or abstract classes (`PortfolioDao`, `UserBalanceDao`) as lacking implementation classes. Room generates implementations at compile time.

### 5. GitHub Workflows & CI Configuration (`.github/workflows/ai-reviewer.yml`)
* **`review-on-synchronize` Setting**: Do NOT flag `review-on-synchronize: true` or warn about API token consumption/cost on PR synchronization. This behavior is intentional to provide immediate review feedback on every push.
* **Workflow Permissions**: Do NOT flag `actions: read` or other standard workflow permissions as least-privilege violations or unnecessary permissions. This is because the repo "Robin" requires this permission at minimum to run.
* **LLM Environment Variable Comments**: Do NOT flag documentation comments explaining `LLM_BASE_URL`, `LLM_MODEL_NAME`.

---

## ✅ DO Focus Code Reviews On

1. **Concurrency & Thread Safety**: Flag heavy synchronous calculations or blocking IO performed on `Dispatchers.Main`.
2. **Error Handling**: Ensure network and database operations handle failures gracefully (e.g., using `Result<T, DataError>`).
3. **Localization & Resources**: Check for hardcoded UI strings that should be moved to `composeResources/values/strings.xml`.
4. **Security**: Flag any hardcoded API keys, tokens, or unencrypted sensitive data.
5. **Test Coverage**: Suggest unit tests for new business logic, domain use cases, or ViewModels.
