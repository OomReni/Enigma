# Enigma I Java Core

A Java 17 Maven project implementing the core mechanics of a three-moving-rotor, plugboard-equipped Enigma I-style machine. The first deliverable intentionally focuses on the independently testable encryption engine; the JavaFX wood-and-brass interface will be layered on later.

## Requirements

- JDK 17+
- Maven 3.9+

## Run tests

```bash
mvn test
```

## Run the demonstration

```bash
mvn -q package
java -cp target/classes com.enigma.Main
```

The demo encrypts `AAAAA` using rotors I-II-III, reflector B, ring settings AAA, starting positions AAA, and an empty plugboard.

## Configuration conventions

- Rotor order is passed **left to right** (slow, middle, fast), e.g. I-II-III.
- Position and ring settings use A=0 through Z=25.
- Ring settings are separate from displayed rotor positions.
- Rotor stepping happens before the electrical path for each alphabetic character.
- Non-letters are preserved and do not advance the rotors.
- Text is processed case-insensitively; alphabetic output is uppercase.
- Plugboard pairs are supplied as strings such as `["AV", "BS", "CG"]`; letters may appear in at most one pair.

This is an educational historical cipher simulation, not modern secure encryption.
