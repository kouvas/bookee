# Bookee - Barbershop Booking Application

## Overview

Bookee is a frontend-only web application for booking appointments at a barbershop ("La BarberShop" in Paris). It demonstrates modern ClojureScript development with a functional, declarative approach to UI and state management.

**Live Demo:** http://kouvas-bookee.s3-website.eu-west-2.amazonaws.com/

## Technology Stack

- **[Replicant](https://replicant.fun/)** - Virtual DOM rendering library with a focus on simplicity and performance
- **[Ornament](https://github.com/lambdaisland/ornament)** - CSS-in-Clojure styling solution
- **[Statecharts](https://github.com/fulcrologic/statecharts)** - State machine-based navigation (router-less)
- **[Shadow-CLJS](https://shadow-cljs.github.io/)** - ClojureScript build tool with hot-reload
- **[Tick](https://github.com/juxt/tick)** - Date/time library based on js-joda
- **Java 21** (OpenJDK) - Runtime for Clojure
- **Node.js** - For npm dependencies and shadow-cljs

## Architecture

### State Management

The application uses a single atom `!store` to manage all application state:

```clojure
{:ui               {}                    ; UI-specific state (toggles, scroll positions)
 :nav-wmem         (statechart)          ; Navigation state machine
 :booking-details  {:selected-service   nil
                    :selected-team-member nil
                    :selected-date       nil}}
```

### Navigation Flow

Navigation is implemented using **statecharts** (finite state machines) rather than traditional routing. The booking flow supports two paths:

1. **Service → Team → Calendar → Verification**
2. **Team → Service → Calendar → Verification**

Users can choose their preferred path, and the state machine ensures valid transitions.

**Key Navigation States:**
- `:main` - Landing page with service/team selection
- `:->team` - Team member selection view
- `:->service` - Service selection view
- `:service->team->calendar` - Calendar view (service-first path)
- `:team->service->calendar` - Calendar view (team-first path)
- `:verification` - Final booking confirmation

### Action → Effect Pattern

The application uses a unidirectional data flow:

1. **UI Events** - User interactions trigger Replicant events
2. **Actions** - Events are translated to action keywords (`:select-service`, `:navigate/back`, etc.)
3. **Effects** - Actions are converted to effects that modify state
4. **Re-render** - State changes trigger UI updates via atom watches

```
User Click → Replicant Event → Actions → Effects → State Update → Re-render
```

**Action Interpolation:** Actions can contain placeholders (`:event.target/value`) that are interpolated with actual DOM event values.

**Effect Execution:** Effects are pure data structures that describe state changes, keeping side effects isolated.

### Component Structure

**Main Source Files (`src/bookee/`):**

- `core.cljs` - Application entry point, state initialization, render loop
- `ui.cljc` - Main UI component and view rendering logic
- `navigation.cljs` - Statechart definition and navigation logic
- `actions.cljs` - Action interpolation and action→effect mapping
- `effects.cljs` - Effect execution and state mutations
- `data.cljc` - Static data (services, team members, shop info, reviews)
- `calendar.cljc` - Calendar logic and time slot generation
- `components.cljs` - Reusable UI components
- `css.cljc` - Ornament CSS definitions
- `icons.cljs` - SVG icon components
- `map.cljs` - Leaflet map integration
- `utils.cljc` - Utility functions
- `hooks.clj` - Build hooks for shadow-cljs (CSS generation)

### Data Model

**Services:** Barbershop services with pricing, duration, and descriptions
```clojure
{:id 1
 :service-name "Men's Regular Cut"
 :duration 30
 :price 40
 :currency :usd
 :details "This is a very good service"}
```

**Team Members:** Barbers with their available services
```clojure
{:id 10
 :name "Jeff"
 :surname "Jefferson"
 :details "Jeff is solid!"
 :img "https://..."
 :services-offered [1 2 3 4 5 6]}
```

**Time Slots:** Generated dynamically based on shop hours and selected date
```clojure
{:time "09:00"
 :available? true}
```

**Shop Info:** Location, hours, contact details for the barbershop

## Features

### Current Functionality
- ✅ Service browsing and selection
- ✅ Team member browsing and selection
- ✅ Calendar with available time slots
- ✅ Working hours management (Monday-Saturday, closed Sundays)
- ✅ Interactive map showing shop location (Leaflet.js)
- ✅ Customer reviews display
- ✅ Booking verification screen
- ✅ Responsive design
- ✅ Hot module reloading (HMR) during development

### Booking Flow
1. User lands on main page showing services and team members
2. User selects either a service or team member first
3. User proceeds to select the other option
4. Calendar displays available dates and time slots
5. User selects date and time
6. Verification screen shows booking summary
7. User confirms booking (currently UI-only, no backend)

## Project Structure

```
bookee/
├── src/
│   └── bookee/
│       ├── core.cljs              # App initialization and main loop
│       ├── ui.cljc                # Main UI rendering
│       ├── navigation.cljs        # Statechart-based navigation
│       ├── actions.cljs           # Action handling
│       ├── effects.cljs           # Effect execution
│       ├── data.cljc              # Static data
│       ├── calendar.cljc          # Calendar logic
│       ├── components.cljs        # UI components
│       ├── css.cljc               # CSS definitions
│       ├── icons.cljs             # SVG icons
│       ├── map.cljs               # Map integration
│       ├── utils.cljc             # Utilities
│       └── hooks.clj              # Build hooks
├── test/
│   └── bookee/
│       └── example_test.clj       # Sample tests
├── portfolio/
│   └── src/bookee/
│       ├── portfolio.cljs         # Component showcase entry
│       └── components/
│           └── card_scenes.cljs   # Component examples
├── resources/
│   └── public/                    # Static assets and generated JS
├── deps.edn                       # Clojure dependencies
├── shadow-cljs.edn                # ClojureScript build config
├── package.json                   # npm dependencies
├── Makefile                       # Build shortcuts
├── run-tests.sh                   # Test runner script
├── README.md                      # Basic project info
├── CLAUDE_CODE_SETUP.md           # Development setup guide
└── PROJECT_SUMMARY.md             # This file
```

## Development Workflow

### Starting Development

1. **Install dependencies:**
   ```bash
   npm install
   ```

2. **Start shadow-cljs watch:**
   ```bash
   make shadow
   # or: npx shadow-cljs watch app portfolio
   ```

3. **Open in browser:**
   - Main app: http://localhost:8090
   - Portfolio: http://localhost:8092

4. **Connect REPL (optional):**
   ```bash
   make nrepl
   # Connect your editor to port 9000
   ```

### Making Changes

- Edit `.cljs`, `.cljc`, or `.clj` files in `src/bookee/`
- Changes hot-reload automatically in the browser
- Check browser console for compilation errors
- CSS changes (Ornament) also hot-reload

### Running Tests

```bash
./run-tests.sh
```

Tests use standard `clojure.test` and can be run without external tools.

## Key Design Decisions

### Why Statecharts Instead of Routes?

Traditional routing can lead to invalid states (e.g., calendar page without service selection). Statecharts ensure:
- Only valid navigation paths are possible
- State transitions are explicit and documented
- Easy to visualize the entire navigation flow
- No need for URL manipulation

### Why Replicant?

- Simpler than React/Reagent for small apps
- Direct Clojure data structures for UI
- No JSX or hiccup syntax overhead
- Explicit event handling

### Why No Backend?

This is a demonstration/portfolio project focused on:
- Modern ClojureScript patterns
- Functional UI architecture
- State machine navigation
- Component-driven development

## Future Enhancements

Potential features that could be added:

- [ ] Backend integration (booking persistence)
- [ ] User authentication
- [ ] Email confirmation
- [ ] Payment integration
- [ ] Real-time availability checking
- [ ] SMS notifications
- [ ] Multi-language support
- [ ] Admin panel for managing services/team
- [ ] Booking cancellation/rescheduling
- [ ] More comprehensive test coverage

## Testing

The project uses:
- **clojure.test** for unit tests
- **test.check** for property-based testing (configured but not yet used)

Test files follow the `*_test.clj` naming convention and are located in `test/bookee/`.

## Build and Deployment

### Production Build

```bash
npx shadow-cljs release app
```

Outputs optimized JavaScript to `resources/public/js/`.

### Current Deployment

The app is deployed to AWS S3 as a static website:
http://kouvas-bookee.s3-website.eu-west-2.amazonaws.com/

## Key Libraries and Tools

- **Clojure 1.12.2** - Core language
- **Shadow-CLJS 3.2.0** - Build tool
- **Replicant 2025.06.21** - Rendering
- **Ornament 1.16.141** - CSS
- **Statecharts 1.2.22** - Navigation
- **Tick 1.0** - Date/time
- **Telemere 1.1.0** - Logging
- **Portfolio 2025.08.29** - Component showcase (dev only)

## Learning Resources

To understand this codebase, familiarity with these concepts helps:

- ClojureScript basics
- Functional programming patterns
- State machines / statecharts
- Virtual DOM concepts
- Event-driven architecture
- Unidirectional data flow

## License

Not specified (project is for demonstration purposes).

## Author

Project by kouvas - https://github.com/kouvas/bookee
