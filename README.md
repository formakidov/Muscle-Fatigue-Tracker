# Muscle Fatigue Tracker

## Overview
**Muscle Fatigue Tracker** is an Android application designed to help users **monitor and track muscle fatigue levels and recovery times**. This project demonstrates modern Android development best practices, including **MVVM, Clean Architecture, Jetpack Compose**, and **GitHub Actions** for continuous integration.

## Technology Stack
This project utilizes a modern Android development stack, emphasizing **maintainability, testability, and a reactive UI approach**.

### Core Technologies
- **Kotlin** – Primary programming language
- **Jetpack Compose** – Modern UI toolkit for building declarative, reactive UIs.

### Architecture
- **Clean Architecture** – Organizes the application into distinct layers:
  - **Presentation Layer (UI):** Built with Jetpack Compose, responsible for UI state management and user interactions.
  - **Domain Layer:** Houses business logic and use cases, ensuring testability and separation of concerns.
  - **Data Layer:** Manages local data persistence via **Room** (with potential for future remote data sources).
  - **Infrastructure Layer (Out of Scope for Now):** Would typically handle networking, API interactions, and external data sources. This layer is currently **not implemented** but may be considered for future development when remote data synchronization is introduced.
- **MVVM (Model-View-ViewModel):**
  - **ViewModel** – Fetches data from the domain layer and exposes it to the UI.
  - **View** – Composable UI components observe data from ViewModel.
- **Dependency Injection (Koin):** Lightweight DI framework for easy dependency management and testability.

## Libraries Used
### Android Jetpack
- **Room** – Local database management.
- **Lifecycle** – ViewModel & Flow for reactive state management.
- **Jetpack Compose** – Declarative UI framework.

### Asynchronous & Reactive Programming
- **Kotlin Coroutines** – For efficient background tasks.
- **Flow** – Handles real-time data streams asynchronously.

### Testing
- **JUnit 5** – Core testing framework.
- **MockK** – Mocking library for unit testing.
- **Kotlin Coroutines Test** – For testing coroutine-based code.
- **Koin Test** – Dependency injection testing utilities.

## Continuous Integration (CI)
### GitHub Actions Workflow
The project is integrated with **GitHub Actions** for automated testing and builds.

## License
This project is open-source and available under the **MIT License**.

---

*Developed with modern Android best practices.*

