# 🗺️ Hunter of Sights

A mobile city exploration game that turns real-world walking into an immersive discovery experience, focusing on mapping and fog-of-war mechanics inspired by classic strategy games. The primary focus of this project is to implement a robust and scalable Android architecture.

## ✨ Core Concept

The player explores the city of Tbilisi, Georgia, revealing the map from a digital "fog of war" by physically walking. Discovering new areas and Points of Interest (POIs) contributes to the player's progress and unlocks in-game features.

## 🏗️ Architectural Approach

This project is built using modern Android best practices to ensure maintainability, scalability, and clean separation of concerns.

### 1. Multi-Module Structure

The application is split into distinct Gradle modules for a clear dependency graph and faster build times:
* `app`: The entry point and main application module.
* `feature:map`: The main feature module containing the map UI and logic.
* `core:common`: Utility classes and base MVI contracts (UiState, UiEvent, UiEffect).
* `core:model`: Pure Kotlin data models (GeoPoint, Poi).

### 2. State Management (MVI)

We use the **Model-View-Intent (MVI)** pattern for unidirectional data flow, making the state predictable and traceable:
* **State:** The single source of truth (`MapState`) rendered by the UI.
* **Event:** User actions or system inputs (e.g., `OnPoiClick`, `OnLocationUpdate`).
* **Effect:** One-off actions that do not change the state (e.g., `ShowToast`).

### 3. Technology Stack

* **Language:** Kotlin (with Kotlin Flow and Coroutines for asynchronous operations).
* **UI:** Jetpack Compose (Modern declarative UI toolkit).
* **DI (Dependency Injection):** Hilt.
* **Mapping:** Google Maps Platform (using the `maps-compose` library).

## 🚀 Next Planned Steps

1.  **Real-Time Location Tracking:** Implement Location Services using Fused Location Provider to stream real user coordinates to the ViewModel.
2.  **Fog of War Logic:** Develop a robust grid system to calculate which map cells are "discovered" and update the discovered areas set.
3.  **Data Persistence:** Implement a local database (Room/SQLite) to save discovered areas and POIs.

---
*(Created using Kotlin, Jetpack Compose, Hilt, and MVI)*
