# Muscle Fatigue Tracker

## Overview
**Muscle Fatigue Tracker** is a Kotlin Multiplatform (KMP) application designed to help users **monitor and track muscle fatigue levels and recovery times**. This project demonstrates modern development best practices, including **MVVM, Clean Architecture, and Jetpack/JetBrains Compose**, and **GitHub Actions** for continuous integration. The app targets **Android, iOS, and Desktop**.

## Key Features
- **Fatigue & Recovery Tracking:** Log fatigue levels for different muscles after workouts.
- **Dynamic Sorting & Filtering:** Users can sort the muscle list based on fatigue, recovery time, or name, and filter by muscle groups to quickly find what they're looking for.

## Technology Stack
This project utilizes a modern Kotlin Multiplatform development stack, emphasizing **maintainability, testability, and a reactive UI approach**.

### Core Technologies
- **Kotlin** – Primary programming language
- **Jetpack/JetBrains Compose** – Modern UI toolkit for building declarative, reactive UIs across multiple platforms.

### Architecture
- **Clean Architecture** – Organizes the application into distinct layers:
  - **Presentation Layer (UI):** Built with Compose, responsible for UI state management and user interactions.
  - **Domain Layer:** Houses business logic and use cases, ensuring testability and separation of concerns.
  - **Data Layer:** Manages local data persistence via **Room** and **DataStore**.
- **MVVM (Model-View-ViewModel):**
  - **ViewModel** – Fetches data from the domain layer and exposes it to the UI.
  - **View** – Composable UI components observe data from ViewModel.
- **Dependency Injection (Koin):** Lightweight DI framework for easy dependency management and testability.

### Architectural Highlights
- **Platform-Agnostic Domain Layer:** The Clean Architecture approach ensures that all business logic (how fatigue is calculated, how data is sorted) is written in pure Kotlin in the `commonMain` source set. This is the core benefit of KMP—this critical code is written once and shared across Android, iOS, and Desktop without modification.
- **Shared ViewModels:** Thanks to Koin and lifecycle libraries, the `ViewModels` are also located in `commonMain`, allowing you to share the UI state management and presentation logic across all platforms.
- **Multiplatform Data Persistence:** You're using the KMP-compatible versions of **Room** and **DataStore**, which is a modern and robust way to handle local data storage in a shared codebase.

## Developer Experience & Code Quality
- **Automated Code Quality Checks:** The project uses pre-commit hooks to automatically format code and run static analysis. This enforces a consistent style and catches potential bugs before they are even committed to the repository.
- **Compose Hot Reload:** The project is configured with Compose Hot Reload, which allows for near-instantaneous UI updates during development without needing to restart the application, significantly speeding up the development workflow.

## Libraries Used
### UI
- **Jetpack/JetBrains Compose** – Declarative UI framework for Android, iOS, and Desktop.
- **Koalaplot** - A Compose multiplatform library for creating charts.

### Data
- **Room** – Local database management.
- **DataStore** - For storing simple key-value pairs.
- **SQLite** - Bundled for local storage.

### Asynchronous & Reactive Programming
- **Kotlin Coroutines** – For efficient background tasks.
- **Flow** – Handles real-time data streams asynchronously.

### Other
- **Kotlinx DateTime** - For handling dates and times in a multiplatform way.

### Testing
- **JUnit 5** – Core testing framework.
- **Kotlin Coroutines Test** – For testing coroutine-based code.
- **Koin Test** – Dependency injection testing utilities.
- **Mokkery** - A mocking library for Kotlin Multiplatform.

## Continuous Integration (CI)
### GitHub Actions Workflow
The project is integrated with **GitHub Actions** for automated testing and builds.

## License
This project is open-source and available under the **MIT License**.

---
*Developed with modern Kotlin Multiplatform best practices.*
