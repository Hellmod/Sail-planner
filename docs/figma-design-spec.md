# Sail Planner – Figma Design Specification

> Ten dokument opisuje makiety UI dla każdego ekranu aplikacji.
> Zaimplementuj je w Figma korzystając z poniższych specyfikacji,
> lub zaimportuj gotowy JSON do Figma Community (link poniżej).

---

## Paleta kolorów

| Token | Kolor | Hex | Zastosowanie |
|---|---|---|---|
| `ocean-blue` | 🔵 | `#006994` | Primary akcje, AppBar |
| `deep-navy` | 🔵 | `#002147` | Gradient tła auth |
| `sea-foam` | 🩵 | `#4ECDC4` | Secondary, aktywny rejs |
| `sunset-orange` | 🟠 | `#FF6B35` | FAB, akcje CTA |
| `wave-white` | ⬜ | `#F5F9FC` | Background jasny |
| `dark-sea` | ⬛ | `#1A2B3C` | Background ciemny, tekst |
| `coral-red` | 🔴 | `#E63946` | Błędy, ostrzeżenia |
| `golden-sand` | 🟡 | `#FFD700` | Gwiazdki, wyróżnienia |

## Typografia

| Styl | Font | Rozmiar | Weight |
|---|---|---|---|
| Display | Inter | 57sp | Bold |
| Headline L | Inter | 32sp | SemiBold |
| Headline M | Inter | 28sp | SemiBold |
| Title L | Inter | 22sp | Bold |
| Title M | Inter | 16sp | SemiBold |
| Body L | Inter | 16sp | Regular |
| Body M | Inter | 14sp | Regular |
| Label L | Inter | 14sp | Medium |

---

## Screen 1 – Auth / Onboarding

```
┌──────────────────────────────────┐
│                                  │
│   [Gradient: deep-navy → ocean-  │
│    blue → sea-foam, vertical]    │
│                                  │
│         ┌──────────┐             │
│         │  ☠️ Logo  │  96×96dp   │
│         │ rounded  │  opacity 20%│
│         └──────────┘             │
│                                  │
│       Sail Planner               │  Headline L, white
│   Plan your sailing adventure    │  Body L, white 80%
│                                  │
│   ┌──────────────────────────┐   │
│   │  [G]  Continue with      │   │  52dp height
│   │       Google             │   │  white bg, #333 text
│   └──────────────────────────┘   │
│                                  │
│   ┌──────────────────────────┐   │
│   │  🍎  Continue with       │   │  52dp height
│   │       Apple              │   │  black bg, white text
│   └──────────────────────────┘   │
│                                  │
│       Continue as Guest          │  TextButton, white 80%
│                                  │
└──────────────────────────────────┘
```

**Animacje:**
- Logo: fade-in + scale 0.8→1.0 na starcie
- Przyciski: slide-up z opóźnieniem 300ms

---

## Screen 2 – Lista Rejsów

```
┌──────────────────────────────────┐
│ ░░░░░ My Trips        [👤]       │  AppBar ocean-blue
├──────────────────────────────────┤
│                                  │
│ ┌────────────────────────────┐   │
│ │ 🟢 ACTIVE NOW              │   │  sea-foam bg
│ │ Baltic Summer 2024         │   │  Title L
│ │ Gdańsk → Sztokholm         │   │  Body M, 60% opacity
│ └────────────────────────────┘   │
│                                  │
│ ┌────────────────────────────┐   │  Card elevation 2dp
│ │ Adriatic Dream             │   │  Title M
│ │ Split → Dubrovnik   [Planned]│ │  chip ocean-blue 15%
│ └────────────────────────────┘   │
│                                  │
│ ┌────────────────────────────┐   │
│ │ North Sea Adventure        │   │
│ │ Amsterdam → Oslo  [Done]   │   │  chip grey 15%
│ └────────────────────────────┘   │
│                                  │
│                        ⊕         │  FAB sunset-orange, CircleShape
└──────────────────────────────────┘
```

**Interakcje:**
- Karta aktywnego rejsu: pulse animation na zielonym kółku
- Swipe left na karcie → opcje: Archiwizuj / Usuń

---

## Screen 3 – Dashboard Rejsu

```
┌──────────────────────────────────┐
│ ← Baltic Summer 2024             │  AppBar ocean-blue
├──────────────────────────────────┤
│                                  │
│  ┌──────────┐  ┌──────────┐      │
│  │   🛒     │  │   ⏰     │      │  120dp height
│  │ Shopping │  │ Watches  │      │  rounded 16dp
│  │  Lists   │  │ (Wachty) │      │  kolor 12% opacity
│  └──────────┘  └──────────┘      │
│                                  │
│  ┌──────────┐  ┌──────────┐      │
│  │   🗺️     │  │   📷     │      │
│  │  Route   │  │  Photos  │      │
│  │ Tracking │  │          │      │
│  └──────────┘  └──────────┘      │
│                                  │
│  ┌──────────┐  ┌──────────┐      │
│  │   💰     │  │   🌊     │      │
│  │ Expenses │  │ Memory   │      │
│  │(Splitwise│  │ Collage  │      │
│  └──────────┘  └──────────┘      │
│                                  │
└──────────────────────────────────┘
```

**Kolory kafelków:**
- Shopping: `#4A90D9`
- Watches: `#006994` (ocean-blue)
- Route: `#2ECC71`
- Photos: `#FF6B35` (sunset-orange)
- Expenses: `#9B59B6`
- Collage: `#4ECDC4` (sea-foam)

---

## Screen 4 – Listy Zakupów

```
┌──────────────────────────────────┐
│ ← Shopping Lists                 │  AppBar
├──────────────────────────────────┤
│ Estimated: 450 PLN  Act: 380 PLN │  summary bar, ocean-blue 8%
├─────────────┬──────────┬─────────┤
│  Provisions │ Safety   │Hardware │  ScrollableTabRow
├──────────────────────────────────┤
│                                  │
│ ☐  Bread                1 pcs   │  item row
│    Quantity: 2 loaves            │
│ ☐  Fresh water     20 L  8 PLN  │
│ ☐  Life jacket    1 pcs 120 PLN │
│ ──────────────────────────────   │
│ Purchased (3)                    │  section header 50% opacity
│ ☑  Coffee (strikethrough)        │  40% opacity
│ ☑  Sunscreen (strikethrough)     │
│                                  │
│                        ⊕         │  FAB sunset-orange
└──────────────────────────────────┘
```

---

## Screen 5 – Wachty (Sailing Watches)

```
┌──────────────────────────────────┐
│ ← Watches                [+]     │
├──────────────────────────────────┤
│                                  │
│  TODAY                           │  Label, 60% opacity
│                                  │
│  ┌──────────────────────────┐    │
│  │ ██ 00:00 – 04:00          │   │  Aktywna wachta: tło sea-foam
│  │ Helmsman: Kowalski        │   │
│  │ Crew: Nowak, Wiśniewska   │   │
│  │ Wind: 15 kn NW  Sea: 1.5m│   │
│  └──────────────────────────┘    │
│                                  │
│  ┌──────────────────────────┐    │
│  │ ▒▒ 04:00 – 08:00          │   │  Przyszła wachta: tło jasnoszary
│  │ Helmsman: Janowski        │   │
│  └──────────────────────────┘    │
│                                  │
│  Log Entries (last watch)        │
│  ├ 02:34  Position: 54.2N 18.7E  │
│  ├ 02:34  Speed: 6.2 kn          │
│  └ 03:15  Visibility reduced     │
│                                  │
└──────────────────────────────────┘
```

---

## Screen 6 – Śledzenie Trasy (Route Map)

```
┌──────────────────────────────────┐
│ ← Route Tracking                 │  AppBar z przezroczystością
├──────────────────────────────────┤
│                                  │
│  ┌────────────────────────────┐  │
│  │                            │  │
│  │   [FULLSCREEN MAP]         │  │  Google Maps / MapKit
│  │                            │  │
│  │   🚢 Stateczek (aktualna   │  │  Custom ship marker
│  │      pozycja)              │  │
│  │                            │  │
│  │   ~~~~ Trasa (linia)       │  │  Polyline, ocean-blue
│  │    ⚓ Port startowy        │  │
│  │              ⚓ Port docel. │  │
│  │                            │  │
│  └────────────────────────────┘  │
│                                  │
│ ┌────────────────────────────┐   │  Floating card dolny
│ │ 🟢 Tracking: ON            │   │
│ │ Speed: 5.4 kn              │   │
│ │ Interval: 30s    [STOP]    │   │
│ └────────────────────────────┘   │
└──────────────────────────────────┘
```

---

## Screen 7 – Zdjęcia

```
┌──────────────────────────────────┐
│ ← Photos              [📷] [🗺️] │  AppBar
├──────────────────────────────────┤
│                                  │
│  ┌──────┐ ┌──────┐ ┌──────┐     │  Grid 3 kolumny
│  │ 📍   │ │ 📍   │ │ 📍   │     │  104dp × 104dp
│  │[img1]│ │[img2]│ │[img3]│     │  pin indicator jeśli ma GPS
│  └──────┘ └──────┘ └──────┘     │
│  ┌──────┐ ┌──────┐ ┌──────┐     │
│  │[img4]│ │[img5]│ │[+add]│     │  ostatnia kafelka = dodaj
│  └──────┘ └──────┘ └──────┘     │
│                                  │
│  [MAP VIEW]  • 12 photos         │  toggle: grid / mapa z pinami
│                                  │
└──────────────────────────────────┘
```

**Widok szczegółu zdjęcia:**
- Pełnoekranowe zdjęcie
- Poniżej: data, lokalizacja (reverse geocoded), opis
- Chip: "Include in collage" toggle

---

## Screen 8 – Wydatki

```
┌──────────────────────────────────┐
│ ← Expenses                       │
├──────────────────────────────────┤
│ ┌────────────────────────────┐   │  summary card
│ │ Total: 2 340 PLN  Members:4│   │
│ └────────────────────────────┘   │
├────────┬────────┬─────────────────┤
│Expenses│Balances│  Settle Up      │  TabRow
├──────────────────────────────────┤
│                                  │
│ [F]  Fuel – Marina Gdańsk  200PLN│  F = Fuel icon circle
│      Paid by: Jan  · 4 members   │
│                                  │
│ [E]  Equipment – Liny     450 PLN│
│      Paid by: Ania · 3 members   │
│                                  │
│ [Balances tab]                   │
│  Jan Kowalski    +340 PLN 🟢      │
│  Anna Nowak      -120 PLN 🔴      │
│  Piotr Wiśniewski  0 PLN  ⚪      │
│                                  │
│ [Settle Up tab]                  │
│  Anna → Jan        120 PLN       │  Card rounded 12dp
│  Marek → Jan       220 PLN       │
│                                  │
│                        ⊕         │
└──────────────────────────────────┘
```

---

## Screen 9 – Kolaż Trasy (Memory Collage)

```
┌──────────────────────────────────┐
│ ← Memory Collage                 │
├──────────────────────────────────┤
│                                  │
│  ┌────────────────────────────┐  │
│  │                            │  │
│  │   [MAPA ŚWIATA]            │  │  Stylizowana mapa (dot pattern)
│  │                            │  │
│  │  ⚓ Gdańsk                 │  │  Port startowy
│  │    ~~~~~~~~~~              │  │  Linia trasy, animowana
│  │              ⚓ Sztokholm  │  │  Port docelowy
│  │                            │  │
│  │  [📸] [📸] [📸]           │  │  Miniatury zdjęć na trasie
│  │                            │  │
│  │  🚢  (animacja stateczka)  │  │
│  │                            │  │
│  └────────────────────────────┘  │
│                                  │
│  Baltic Summer 2024              │  Title
│  14 dni · 340 Mm · 12 portów    │  stats
│  47 zdjęć                        │
│                                  │
│  [  Generate Collage  ]          │  Button ocean-blue
│  [  Share / Export    ]          │  OutlinedButton
│                                  │
└──────────────────────────────────┘
```

**Animacje kolażu:**
- Mapa: fade-in segmentów trasy
- Stateczek: lerp po punktach trasy, 8–12 sekund
- Zdjęcia: pojawiają się w momencie, gdy stateczek mija lokalizację
- Efekt: Ken Burns na zdjęciach (powolny pan/zoom)

---

## Komponenty wielokrotnego użytku

### TripStatusChip
```
Planned  →  ocean-blue  15% opacity
Active   →  #00AA33     15% opacity
Done     →  grey        15% opacity
Archived →  light-grey  15% opacity
```

### BottomSheet (Add Expense / Add Item)
- Rounded corners top: 24dp
- Handle bar: 32×4dp, grey, centered
- Pola formularza z OutlinedTextField
- Przycisk Save: full-width, ocean-blue

### EmptyState
```
[Emoji 48sp]
Tytuł (Title M)
Podtytuł (Body M, 50% opacity)
[CTA Button]
```

---

## Figma – Kroki do implementacji

1. **Utwórz nowy plik Figma**
   - Nazwa: `Sail Planner – Mobile App`
   - Frame: `390 × 844` (iPhone 14 Pro)
   - Auto Layout włączone

2. **Utwórz bibliotekę stylów:**
   - Color Styles: ocean-blue, deep-navy, sea-foam, sunset-orange, itd.
   - Text Styles: zgodnie z tabelą typografii
   - Effect Styles: card-shadow (`0 2dp 8dp rgba(0,0,0,0.10)`)

3. **Utwórz komponenty:**
   - `Button/Primary` (ocean-blue fill)
   - `Button/Secondary` (outlined)
   - `Button/Danger` (coral-red)
   - `Card/Trip` (z wariantami: Active, Planned, Archived)
   - `ListItem/Shopping` (z Checkbox wariant)
   - `ListItem/Expense`
   - `NavigationBar` (5 tabs)
   - `BottomSheet`
   - `AppBar` (z wariantami: z back / bez back)

4. **Kolejność ekranów w Figma:**
   1. Onboarding (3 slajdy)
   2. Auth
   3. Trip List (empty state + filled)
   4. Create Trip
   5. Trip Dashboard
   6. Shopping → List → Add Item
   7. Watches → Detail → Add Log
   8. Route Map
   9. Photos → Photo Detail
   10. Expenses → Add Expense → Balances → Settle Up
   11. Collage (generating + final)
   12. Profile / Settings

5. **Prototyp:**
   - Połącz ekrany strzałkami
   - Dodaj Smart Animate dla transitions
   - Zdefiniuj Gestures: swipe left na kartach
