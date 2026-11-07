package com.enigma.controller;

import com.enigma.model.EnigmaMachine;
import com.enigma.model.RotorSpec;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;

public class InteriorController {
    @FXML private ComboBox<RotorSpec> leftRotorType;
    @FXML private ComboBox<RotorSpec> middleRotorType;
    @FXML private ComboBox<RotorSpec> rightRotorType;
    @FXML private ComboBox<String> leftRingSetting;
    @FXML private ComboBox<String> middleRingSetting;
    @FXML private ComboBox<String> rightRingSetting;
    @FXML private Label configurationStatus;
    @FXML private Label leftRotorMark;
    @FXML private Label middleRotorMark;
    @FXML private Label rightRotorMark;
    @FXML private Pane plugboardCanvas;
    @FXML private Label plugboardSummary;

    private final Map<Character, Character> plugboardConnections = new HashMap<>();
    private final Map<Character, Point2D> plugSocketPositions = new HashMap<>();
    private final List<Line> plugboardWires = new ArrayList<>();
    private Character dragStartLetter;
    private Line previewWire;

    private EnigmaMachine enigmaMachine;

    @FXML
    public void initialize() {
        BuildPlugboard();
        var rotorOptions = FXCollections.observableArrayList(RotorSpec.values());
        var ringOptions = FXCollections.observableArrayList(
                Arrays.stream("ABCDEFGHIJKLMNOPQRSTUVWXYZ".split(""))
                        .toList());

        leftRotorType.setItems(FXCollections.observableArrayList(rotorOptions));
        middleRotorType.setItems(FXCollections.observableArrayList(rotorOptions));
        rightRotorType.setItems(FXCollections.observableArrayList(rotorOptions));
        leftRingSetting.setItems(FXCollections.observableArrayList(ringOptions));
        middleRingSetting.setItems(FXCollections.observableArrayList(ringOptions));
        rightRingSetting.setItems(FXCollections.observableArrayList(ringOptions));

        leftRotorType.valueProperty().addListener((observable, oldValue, newValue) ->
                leftRotorMark.setText(newValue == null ? "—" : newValue.name()));
        middleRotorType.valueProperty().addListener((observable, oldValue, newValue) ->
                middleRotorMark.setText(newValue == null ? "—" : newValue.name()));
        rightRotorType.valueProperty().addListener((observable, oldValue, newValue) ->
                rightRotorMark.setText(newValue == null ? "—" : newValue.name()));
    }

    public void SetEnigmaMachine(EnigmaMachine machine) {
        enigmaMachine = machine;
        plugboardConnections.clear();
        for (String pair : enigmaMachine.PlugboardPairs()) {
            char first = pair.charAt(0);
            char second = pair.charAt(1);
            plugboardConnections.put(first, second);
            plugboardConnections.put(second, first);
        }
        RenderPlugboardWires();
        UpdatePlugboardSummary();
        List<RotorSpec> installedRotors = enigmaMachine.RotorOrder();
        String rings = enigmaMachine.RingSettings();

        leftRotorType.setValue(installedRotors.get(0));
        middleRotorType.setValue(installedRotors.get(1));
        rightRotorType.setValue(installedRotors.get(2));
        leftRingSetting.setValue(String.valueOf(rings.charAt(0)));
        middleRingSetting.setValue(String.valueOf(rings.charAt(1)));
        rightRingSetting.setValue(String.valueOf(rings.charAt(2)));
    }

    @FXML
    public void ApplyRotorConfiguration() {
        if (enigmaMachine == null) return;

        List<RotorSpec> selectedRotors = Arrays.asList(
                leftRotorType.getValue(),
                middleRotorType.getValue(),
                rightRotorType.getValue());

        if (selectedRotors.stream().anyMatch(java.util.Objects::isNull)) {
            configurationStatus.setText("Select a rotor for each position.");
            return;
        }
        if (selectedRotors.stream().distinct().count() != 3) {
            configurationStatus.setText("A rotor can only be installed in one position at a time.");
            return;
        }

        List<String> selectedRings = Arrays.asList(
                leftRingSetting.getValue(),
                middleRingSetting.getValue(),
                rightRingSetting.getValue());
        if (selectedRings.stream().anyMatch(java.util.Objects::isNull)) {
            configurationStatus.setText("Select a ring setting for each rotor.");
            return;
        }

        String rings = String.join("", selectedRings);
        enigmaMachine.SetRotorConfiguration(selectedRotors, rings);
        configurationStatus.setText("Configuration installed. Current window positions and plugboard wiring are unchanged.");
    }


    private void BuildPlugboard() {
        plugboardCanvas.getChildren().clear();
        plugSocketPositions.clear();

        double startX = 42;
        double spacing = 54;
        double firstRowY = 58;
        double secondRowY = 158;

        for (int index = 0; index < 26; index++) {
            char letter = (char) ('A' + index);
            int column = index % 13;
            int row = index / 13;
            double centerX = startX + column * spacing;
            double centerY = row == 0 ? firstRowY : secondRowY;
            plugSocketPositions.put(letter, new Point2D(centerX, centerY));

            Circle socket = new Circle(centerX, centerY, 17);
            socket.getStyleClass().add("plug-socket");
            socket.setUserData(letter);
            socket.setOnMousePressed(event -> HandlePlugSocketPressed(letter, event));

            Text label = new Text(String.valueOf(letter));
            label.getStyleClass().add("plug-socket-label");
            label.setMouseTransparent(true);
            label.setX(centerX - 4.5);
            label.setY(centerY + 5);

            plugboardCanvas.getChildren().addAll(socket, label);
        }

        plugboardCanvas.setOnMouseDragged(this::HandlePlugboardDrag);
        plugboardCanvas.setOnMouseReleased(this::HandlePlugboardRelease);
    }

    private void HandlePlugSocketPressed(char letter, MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            RemovePlugConnection(letter);
            event.consume();
            return;
        }
        if (event.getButton() != MouseButton.PRIMARY) return;

        dragStartLetter = letter;
        Point2D start = plugSocketPositions.get(letter);
        previewWire = new Line(start.getX(), start.getY(), start.getX(), start.getY());
        previewWire.getStyleClass().add("plug-wire-preview");
        previewWire.setMouseTransparent(true);
        plugboardCanvas.getChildren().add(0, previewWire);
        event.consume();
    }

    private void HandlePlugboardDrag(MouseEvent event) {
        if (dragStartLetter == null || previewWire == null) return;
        Point2D cursor = plugboardCanvas.sceneToLocal(event.getSceneX(), event.getSceneY());
        previewWire.setEndX(cursor.getX());
        previewWire.setEndY(cursor.getY());
        event.consume();
    }

    private void HandlePlugboardRelease(MouseEvent event) {
        if (dragStartLetter == null) return;
        Point2D releasePoint = plugboardCanvas.sceneToLocal(event.getSceneX(), event.getSceneY());
        Character targetLetter = FindNearestPlugSocket(releasePoint, 25);
        char sourceLetter = dragStartLetter;

        dragStartLetter = null;
        if (previewWire != null) {
            plugboardCanvas.getChildren().remove(previewWire);
            previewWire = null;
        }

        if (targetLetter != null) ConnectPlugPair(sourceLetter, targetLetter);
        event.consume();
    }

    private Character FindNearestPlugSocket(Point2D point, double maximumDistance) {
        return plugSocketPositions.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().distance(point)))
                .filter(entry -> entry.getValue() <= maximumDistance)
                .min(Comparator.comparingDouble(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private void ConnectPlugPair(char firstLetter, char secondLetter) {
        if (firstLetter == secondLetter
                || plugboardConnections.containsKey(firstLetter)
                || plugboardConnections.containsKey(secondLetter)
                || plugboardConnections.size() >= 26) {
            UpdatePlugboardSummary();
            return;
        }

        plugboardConnections.put(firstLetter, secondLetter);
        plugboardConnections.put(secondLetter, firstLetter);
        ApplyPlugboardConnections();
        RenderPlugboardWires();
        UpdatePlugboardSummary();
    }

    private void RemovePlugConnection(char letter) {
        Character partner = plugboardConnections.remove(letter);
        if (partner == null) return;
        plugboardConnections.remove(partner);
        ApplyPlugboardConnections();
        RenderPlugboardWires();
        UpdatePlugboardSummary();
    }

    @FXML
    public void ClearPlugboard() {
        plugboardConnections.clear();
        ApplyPlugboardConnections();
        RenderPlugboardWires();
        UpdatePlugboardSummary();
    }

    private void ApplyPlugboardConnections() {
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<Character, Character> entry : plugboardConnections.entrySet()) {
            if (entry.getKey() < entry.getValue()) {
                pairs.add("" + entry.getKey() + entry.getValue());
            }
        }
        pairs.sort(String::compareTo);
        enigmaMachine.setPlugboardPairs(pairs);
    }

    private void RenderPlugboardWires() {
        plugboardCanvas.getChildren().removeAll(plugboardWires);
        plugboardWires.clear();

        for (Map.Entry<Character, Character> entry : plugboardConnections.entrySet()) {
            char first = entry.getKey();
            char second = entry.getValue();
            if (first >= second) continue;

            Point2D start = plugSocketPositions.get(first);
            Point2D end = plugSocketPositions.get(second);
            Line wire = new Line(start.getX(), start.getY(), end.getX(), end.getY());
            wire.getStyleClass().add("plug-wire");
            wire.setMouseTransparent(true);
            plugboardWires.add(wire);
            plugboardCanvas.getChildren().add(0, wire);
        }
    }

    private void UpdatePlugboardSummary() {
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<Character, Character> entry : plugboardConnections.entrySet()) {
            if (entry.getKey() < entry.getValue()) {
                pairs.add("" + entry.getKey() + entry.getValue());
            }
        }
        pairs.sort(String::compareTo);
        plugboardSummary.setText(pairs.isEmpty()
                ? "No plugboard connections"
                : String.join("   ", pairs) + "   (" + pairs.size() + "/13 pairs)");
    }

    @FXML
    public void CloseInteriorView() {
        Stage stage = (Stage) configurationStatus.getScene().getWindow();
        stage.close();
    }
}
