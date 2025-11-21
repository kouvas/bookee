#!/usr/bin/env bash
# Simple test runner script

CLOJURE_JAR="/usr/share/java/clojure.jar"
SPEC_JAR="/usr/share/java/spec-alpha-clojure.jar"
CORE_SPECS_JAR="/usr/share/java/core.specs.alpha-clojure.jar"

CP="$CLOJURE_JAR:$SPEC_JAR:$CORE_SPECS_JAR:src:test:resources"

# Find all test namespaces
TEST_NAMESPACES=$(find test -name "*_test.clj" -o -name "*_test.cljs" | sed 's|test/||; s|/|.|g; s|_|-|g; s|\.clj.*||')

if [ -z "$TEST_NAMESPACES" ]; then
  echo "No test files found"
  exit 0
fi

# Build require and run-tests expressions
REQUIRES=""
RUN_TESTS=""
for ns in $TEST_NAMESPACES; do
  REQUIRES="$REQUIRES(require '$ns)"
  RUN_TESTS="$RUN_TESTS'$ns "
done

# Run all tests
java -cp "$CP" clojure.main -e "$REQUIRES(apply clojure.test/run-tests [$RUN_TESTS])"
