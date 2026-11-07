# Enigma Machine I

A Java 21 desktop application that simulates the core operation and operator interface of a three-rotor Enigma I machine. The project combines a testable encryption model with a JavaFX interface styled around the machine's historical wood, brass, and mechanical design.

## Requirements

- JDK 21
- Apache Maven 3.9+
- A graphical desktop environment for launching the JavaFX application and running its UI tests

## Build and test

Run the full test suite:

```bash
mvn clean test
```

The tests cover the machine's rotor and ring configuration, plugboard behaviour, and JavaFX controller/UI integration. JavaFX UI tests require a working graphical display; on headless Linux systems, run them with a configured virtual display such as Xvfb.

## Run the application

Launch the desktop application with the JavaFX Maven plugin:

```bash
mvn javafx:run
```

## Application features

### Main operator view

- On-screen keyboard and physical keyboard input.
- Live character-by-character encryption.
- Input and output text displays.
- Lampboard feedback for encrypted characters.
- Rotor window position indicators and manual position adjustment.
- A screw-style access control in the lower-right corner for opening the machine interior.

### Interior configuration view

Open the interior using the screw control on the main view.

- Select the left, middle, and right rotor types.
- Configure each rotor's ring setting independently.
- Install the selected rotor and ring configuration while preserving the current visible rotor positions.
- Configure the plugboard by dragging one letter socket onto another.
- Remove an individual plugboard connection with a right-click, or clear all connections.
- Plugboard changes are applied to the shared machine immediately. Rotor and ring selections are installed with **INSTALL CONFIGURATION**; closing the interior also attempts to install the current valid selections.
- Only one interior window is kept open at a time. Reopening it displays the machine's current configuration.

The interior controls and the main operator view use the same `EnigmaMachine` instance, so configuration changes affect subsequent encryption.

## Configuration conventions

- Rotor order is specified left to right: left (slow), middle, right (fast).
- The available rotor set is I–V.
- Reflector B is used by the default machine configuration.
- Ring settings and visible rotor positions are separate values, each represented by three letters A–Z.
- Rotor stepping occurs before the electrical path for each alphabetic character.
- Non-letter characters are passed through unchanged and do not advance the rotors.
- Text input is processed case-insensitively; encrypted alphabetic output is uppercase.
- Plugboard pairs are represented as two-letter strings, for example `["AV", "BS", "CG"]`. A letter can participate in at most one pair, with a maximum of 13 pairs.

## Project structure

```text
src/
  main/
    java/com/enigma/
      Main.java
      controller/
        EnigmaController.java
        InteriorController.java
      model/
        EnigmaMachine.java
        MachineConfig.java
        Plugboard.java
        ReflectorSpec.java
        Rotor.java
        RotorSpec.java
    resources/com/enigma/
      fxml/
        main-view.fxml
        interior-view.fxml
      styles/
        enigma.css
  test/
    java/com/enigma/
      controller/
        EnigmaControllerUiTest.java
      model/
        EnigmaMachineConfigurationTest.java
```

This is an educational historical cipher simulation, not modern secure encryption.
