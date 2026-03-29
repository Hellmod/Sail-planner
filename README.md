# ⚓ Sail Planner

Aplikacja mobilna dla żeglarzy napisana w **Kotlin Multiplatform** (Android + iOS) z architekturą **MVI** i UI w **Compose Multiplatform**.

---

## Funkcjonalności

| Moduł | Opis |
|---|---|
| 🔐 **Logowanie** | Google Sign-In, Apple Sign-In, tryb gość |
| 🗓️ **Rejsy** | Tworzenie, dołączanie przez kod invite, archiwizacja |
| 👥 **Grupy** | Zapraszanie członków, role (kapitan / załoga / gość) |
| 🛒 **Listy zakupów** | Wielokrotne listy per rejs, kategorie, szacowane koszty |
| ⏰ **Wachty** | Harmonogram wacht, dziennik pokładowy, warunki pogodowe |
| 🗺️ **Trasa** | Śledzenie GPS co 30 s (oszczędność baterii), mapa na żywo |
| 📸 **Zdjęcia** | Upload z geolokalizacją i opisem |
| 💰 **Wydatki** | Split-wise: podziały, bilanse, sugestie rozliczeń |
| 🌊 **Kolaż** | Mapa świata ze stateczkiem, portami i zdjęciami z trasy |

---

## Architektura

```
┌─────────────────────────────────────────────────────────────────┐
│                        Compose UI Layer                         │
│  AuthScreen │ TripList │ Dashboard │ Shopping │ Expenses │ ...  │
└──────────────────────────┬──────────────────────────────────────┘
                           │ State / Intent / Effect
┌──────────────────────────▼──────────────────────────────────────┐
│                    Presentation Layer (MVI)                      │
│  AuthViewModel │ TripListViewModel │ ShoppingViewModel │ ...     │
│                                                                  │
│  BaseViewModel<State, Intent, Effect>                            │
│  ├── StateFlow<State>  (UI observes)                             │
│  ├── dispatch(Intent)  (UI sends events)                         │
│  └── Flow<Effect>      (one-shot navigation / snackbar)          │
└──────────────────────────┬──────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────────┐
│                      Domain Layer                                │
│  Repository interfaces │ Domain models │ LocationService         │
└────────────┬──────────────────────────────────────┬─────────────┘
             │ Local                                │ Remote
┌────────────▼──────────────┐          ┌────────────▼─────────────┐
│     Data Layer (Local)     │          │    Data Layer (Remote)   │
│  SQLDelight (.sq schemas)  │          │  Ktor + Firebase         │
│  RoutePoint, ShoppingItem  │          │  Firestore, Storage, Auth│
│  Expense, Trip             │          │                          │
└───────────────────────────┘          └──────────────────────────┘
```

### MVI Flow

```
User Action
    │
    ▼
dispatch(Intent)
    │
    ▼
handleIntent()  ──── suspend fun ──── Repository
    │                                      │
    ▼                                      ▼
updateState()                       Result<T>
    │
    ▼
StateFlow<State>  ──── collectAsState() ──── Composable re-render
    │
    ▼ (side effects)
emitEffect(Effect)  ──── LaunchedEffect ──── Navigation / Snackbar
```

---

## Stos technologiczny

| Kategoria | Biblioteka | Wersja |
|---|---|---|
| Language | Kotlin Multiplatform | 2.1.0 |
| UI | Compose Multiplatform | 1.7.3 |
| DI | Koin | 4.0.0 |
| Networking | Ktor | 3.0.3 |
| Local DB | SQLDelight | 2.0.2 |
| Navigation | Decompose | 3.2.2 |
| Async | Kotlinx Coroutines | 1.10.1 |
| Serialization | Kotlinx Serialization | 1.7.3 |
| Date/Time | Kotlinx DateTime | 0.6.1 |
| Settings | Multiplatform Settings | 1.3.0 |
| Images | Coil 3 | 3.0.4 |
| Logging | Napier | 2.7.1 |
| Maps (Android) | Maps Compose | 6.2.1 |
| Auth (Android) | Firebase Auth + Credentials | - |
| Backend | Firebase (Firestore + Storage) | BOM 33.7.0 |

---

## Struktura projektu

```
Sail-planner/
├── build.gradle.kts                  # Root build
├── settings.gradle.kts
├── gradle/
│   └── libs.versions.toml            # Version catalog
│
├── shared/                           # Wspólny kod KMP
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/com/hellmod/sailplanner/
│       │   │   ├── di/               # Koin modules
│       │   │   ├── domain/
│       │   │   │   ├── model/        # Trip, User, Watch, Expense, TripPhoto...
│       │   │   │   ├── repository/   # Interfaces
│       │   │   │   └── service/      # LocationService interface
│       │   │   └── presentation/
│       │   │       ├── mvi/          # BaseViewModel, State, Intent, Effect
│       │   │       ├── auth/         # AuthViewModel
│       │   │       ├── trips/        # TripListViewModel
│       │   │       ├── shopping/     # ShoppingViewModel
│       │   │       ├── watch/        # WatchViewModel
│       │   │       ├── expenses/     # ExpenseViewModel
│       │   │       ├── photos/       # PhotoViewModel
│       │   │       ├── route/        # RouteViewModel
│       │   │       ├── navigation/   # RootComponent (Decompose)
│       │   │       └── ui/
│       │   │           ├── theme/    # SailTheme, colors, typography
│       │   │           └── screens/  # Composable screens
│       │   └── sqldelight/           # .sq schema files
│       ├── androidMain/
│       └── iosMain/
│
├── androidApp/                       # Android app module
│   └── src/main/kotlin/
│       ├── SailPlannerApp.kt         # Application class + Koin init
│       ├── MainActivity.kt           # Entry point
│       ├── di/AndroidModule.kt       # Android-specific DI
│       └── service/
│           └── AndroidLocationService.kt
│
└── iosApp/                           # iOS app (Xcode project)
    └── iosApp/
        ├── SailPlannerApp.swift      # @main entry point
        └── ContentView.swift         # Compose bridge
```

---

## Śledzenie lokalizacji

Aplikacja śledzi pozycję użytkownika co **30 sekund** (domyślnie, konfigurowalne).

- Android: `FusedLocationProviderClient` z `PRIORITY_BALANCED_POWER_ACCURACY`
- iOS: `CLLocationManager` z `desiredAccuracy = kCLLocationAccuracyNearestTenMeters`
- Punkty są zapisywane lokalnie (SQLDelight) i synchronizowane do Firestore
- Foreground Service (Android) / Background Modes (iOS) dla ciągłego śledzenia

---

## Kolaż podróży

Po zakończeniu rejsu aplikacja generuje:

1. **Mapę świata** z narysowaną trasą (linia portów → port)
2. **Animowanego stateczka** poruszającego się po trasie
3. **Miniatury zdjęć** przypiętych do lokalizacji wykonania
4. Możliwość eksportu jako obraz lub video timelapse

---

## Wydatki (Split-wise)

- Każdy wydatek ma płatnika i listę uczestników z kwotami
- Algorytm minimalizuje liczbę transferów przy rozliczaniu
- Obsługa wielu walut z przelicznikiem
- Historia wszystkich rozliczeń per rejs

---

## Logowanie

| Provider | Android | iOS |
|---|---|---|
| Google | ✅ Credential Manager | ✅ GoogleSignIn SDK |
| Apple | ❌ (tylko iOS) | ✅ ASAuthorizationController |
| Gość | ✅ Firebase Anonymous | ✅ Firebase Anonymous |

---

## Konfiguracja

### 1. Firebase
1. Utwórz projekt w [Firebase Console](https://console.firebase.google.com)
2. Pobierz `google-services.json` → umieść w `androidApp/`
3. Pobierz `GoogleService-Info.plist` → umieść w `iosApp/iosApp/`
4. Włącz: **Authentication** (Google, Apple, Anonymous), **Firestore**, **Storage**

### 2. Google Maps (Android)
```properties
# local.properties (nie commituj!)
MAPS_API_KEY=AIza...
```

### 3. Google Sign-In Web Client ID
```properties
# local.properties
GOOGLE_WEB_CLIENT_ID=xxx.apps.googleusercontent.com
```

### 4. Uruchomienie
```bash
# Android
./gradlew :androidApp:installDebug

# iOS (z macOS)
cd iosApp && xcodebuild -scheme iosApp -destination 'platform=iOS Simulator,...'
```

---

## Figma Design

Makiety dostępne w pliku `docs/figma-design-spec.md`.

Kluczowe ekrany:
- **Onboarding / Auth** – gradient ocean, logowanie social
- **Lista rejsów** – karty z aktywnym rejsem na górze
- **Dashboard rejsu** – siatka kafelków (6 modułów)
- **Listy zakupów** – zakładki per lista, checkbox items
- **Wachty** – timeline dzienny, aktualna wachta podświetlona
- **Mapa trasy** – fullscreen mapa, floating tracker status
- **Galeria zdjęć** – grid z miniaturami + mapa z pinami
- **Wydatki** – 3 zakładki: lista / bilanse / rozliczenia
- **Kolaż** – animowana mapa z trasą i zdjęciami

---

## Roadmap

- [ ] Implementacja Firebase repositories
- [ ] Ekrany: Wachty, Mapa trasy, Galeria, Kolaż
- [ ] iOS: CoreLocation service
- [ ] Push notifications (nowe wydatki, zmiana wachty)
- [ ] Offline-first sync z Firestore
- [ ] Eksport PDF raportu rejsu
- [ ] Widget na ekran główny (aktywny rejs)
- [ ] Apple Watch companion app
