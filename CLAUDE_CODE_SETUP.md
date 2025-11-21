# Claude Code Setup Guide for Bookee

## Prerequisites

This project requires:
- Java 11 or higher (OpenJDK 21 is installed)
- Node.js and npm (for shadow-cljs)
- Clojure CLI tools

## Clojure Installation

The Clojure CLI tools have been set up with a custom wrapper that uses the system Clojure jar located at `/usr/share/java/clojure.jar`.

### Installed Components
- Clojure 1.11.1 (`/usr/share/java/clojure-1.11.1.jar`)
- Wrapper script at `/usr/local/bin/clojure`
- Test runner script at `./run-tests.sh`

### Installing Clojure (if needed)

If you need to reinstall Clojure on a fresh system:

```bash
# Download and install Clojure packages
cd /tmp
apt-get download clojure libclojure-java libcore-specs-alpha-clojure \
  libspec-alpha-clojure libjsr166y-java rlwrap
dpkg -i *.deb
```

If the clojure package fails to configure due to missing dependencies, the core library (`libclojure-java`) will still be installed and usable with the custom wrapper.

## Project Setup

### 1. Install npm dependencies

```bash
npm install
```

This installs:
- shadow-cljs (ClojureScript compiler)
- @js-joda libraries (date/time)
- snabbdom (virtual DOM)

### 2. Running the Development Server

```bash
# Start shadow-cljs watch with both app and portfolio
make shadow

# Or manually:
npx shadow-cljs watch app portfolio
```

The development server will be available at:
- Main app: http://localhost:8090
- Portfolio (component library): http://localhost:8092

### 3. Running the nREPL

```bash
make nrepl

# Or manually:
clojure -M:nrepl
```

The nREPL server will start on port 9000 (configured in `shadow-cljs.edn`).

## Running Tests

Tests are located in the `test/` directory and follow the standard Clojure test structure.

### Run all tests:

```bash
./run-tests.sh
```

### Run tests manually:

```bash
java -cp "/usr/share/java/clojure.jar:/usr/share/java/spec-alpha-clojure.jar:/usr/share/java/core.specs.alpha-clojure.jar:src:test:resources" \
  clojure.main -e "(require 'bookee.example-test)(clojure.test/run-tests 'bookee.example-test)"
```

## Project Structure

```
bookee/
├── src/bookee/          # Main application source
├── test/bookee/         # Test files
├── portfolio/           # Component portfolio/examples
├── resources/public/    # Static assets
├── deps.edn            # Clojure dependencies
├── shadow-cljs.edn     # ClojureScript build config
├── package.json        # npm dependencies
└── Makefile            # Build shortcuts
```

## Development Workflow

1. Start shadow-cljs watch: `make shadow`
2. Open http://localhost:8090 in your browser
3. Make changes to source files
4. Changes will hot-reload automatically
5. Check the browser console for any errors

## Troubleshooting

### Clojure command not found
Ensure `/usr/local/bin` is in your PATH:
```bash
export PATH="/usr/local/bin:$PATH"
```

### Shadow-cljs errors
Clear the shadow-cljs cache:
```bash
rm -rf .shadow-cljs
npx shadow-cljs watch app portfolio
```

### Port already in use
Change the ports in `shadow-cljs.edn` under `:dev-http`.

## Additional Resources

- [Replicant](https://replicant.fun/) - Rendering library
- [Ornament](https://github.com/lambdaisland/ornament) - CSS styling
- [Shadow-CLJS](https://shadow-cljs.github.io/docs/UsersGuide.html) - ClojureScript compiler
- [Statecharts](https://github.com/fulcrologic/statecharts) - State management
