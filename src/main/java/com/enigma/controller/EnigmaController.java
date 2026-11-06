package com.enigma.controller;

import com.enigma.model.EnigmaMachine;
import com.enigma.model.MachineConfig;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Set;

public class EnigmaController {

    private final EnigmaMachine enigmaMachine = new EnigmaMachine(MachineConfig.defaultConfig());
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private char illuminatedLamp;

    @FXML
    private Label leftRotorSelector;
    @FXML
    private Label middleRotorSelector;
    @FXML
    private Label rightRotorSelector;
    @FXML
    private TextArea inputText;
    @FXML
    private TextArea outputText;
    @FXML
    private Label plugboardSummary;
    @FXML
    private Pane plugboardCanvas;

    private final Map<Character, Character> plugboardConnections = new HashMap<>();
    private final Map<Character, Point2D> plugSocketPositions = new HashMap<>();
    private final Map<Character, Circle> plugSockets = new HashMap<>();
    private final List<Line> plugboardWires = new ArrayList<>();
    private Character dragStartLetter;
    private Line previewWire;
    @FXML
    private Label lampA;
    @FXML
    private Label lampB;
    @FXML
    private Label lampC;
    @FXML
    private Label lampD;
    @FXML
    private Label lampE;
    @FXML
    private Label lampF;
    @FXML
    private Label lampG;
    @FXML
    private Label lampH;
    @FXML
    private Label lampI;
    @FXML
    private Label lampJ;
    @FXML
    private Label lampK;
    @FXML
    private Label lampL;
    @FXML
    private Label lampM;
    @FXML
    private Label lampN;
    @FXML
    private Label lampO;
    @FXML
    private Label lampP;
    @FXML
    private Label lampQ;
    @FXML
    private Label lampR;
    @FXML
    private Label lampS;
    @FXML
    private Label lampT;
    @FXML
    private Label lampU;
    @FXML
    private Label lampV;
    @FXML
    private Label lampW;
    @FXML
    private Label lampX;
    @FXML
    private Label lampY;
    @FXML
    private Label lampZ;


    @FXML
    public void initialize() {
        UpdateRotorPositions();
        BuildPlugboard();
    }

    @FXML
    public void IncrementLeftRotor() {
        ChangeRotorLetter(leftRotorSelector, 1);
    }

    @FXML
    public void DecrementLeftRotor() {
        ChangeRotorLetter(leftRotorSelector, -1);
    }

    @FXML
    public void IncrementMiddleRotor() {
        ChangeRotorLetter(middleRotorSelector, 1);
    }

    @FXML
    public void DecrementMiddleRotor() {
        ChangeRotorLetter(middleRotorSelector, -1);
    }

    @FXML
    public void IncrementRightRotor() {
        ChangeRotorLetter(rightRotorSelector, 1);
    }

    @FXML
    public void DecrementRightRotor() {
        ChangeRotorLetter(rightRotorSelector, -1);
    }

    @FXML
    private void HandleOnScreenKey(javafx.event.ActionEvent event) {
        if (!(event.getSource() instanceof Button pressedButton)) {
            return;
        }

        String buttonText = pressedButton.getText();

        if (buttonText == null || buttonText.length() != 1) {
            return;
        }

        HandleKeyPress(buttonText.charAt(0));
    }

    public void AttachKeyboardInput(Scene scene, Stage primaryStage) {
        scene.addEventFilter(
                KeyEvent.KEY_PRESSED,
                this::HandlePhysicalKey
        );

        scene.addEventFilter(
                KeyEvent.KEY_RELEASED,
                this::HandlePhysicalKeyRelease
        );

        primaryStage.focusedProperty().addListener(
                (observable, wasFocused, isFocused) -> {
                    if (!isFocused) {
                        pressedKeys.clear();
                    }
                }
        );
    }

    public void HandlePhysicalKey(KeyEvent event) {
        KeyCode keyCode = event.getCode();
        boolean isSpace = keyCode == KeyCode.SPACE;
        String keyName = keyCode.getName();
        boolean isLetter = keyName.length() == 1
                && keyName.charAt(0) >= 'A'
                && keyName.charAt(0) <= 'Z';
        if (!isLetter && !isSpace) {
            return;
        }
        event.consume();
        if (!pressedKeys.add(keyCode)) {
            return;
        }
        if (isSpace) {
            HandleKeyPress(' ');
        } else {
            HandleKeyPress(keyName.charAt(0));
        }
    }

    public void HandlePhysicalKeyRelease(KeyEvent event) {
        pressedKeys.remove(event.getCode());
        ExtinguishLamp();
        if (event.getCode() == KeyCode.SPACE || (event.getCode().getName().length() == 1
                && event.getCode().getName().charAt(0) >= 'A' && event.getCode().getName().charAt(0) <= 'Z')) {
            event.consume();
        }
        ExtinguishLamp();
        ClearLampboard();
    }

    public void HandleKeyPress(char inputCharacter) {
        inputCharacter = Character.toUpperCase(inputCharacter);
        if (inputCharacter == ' ') {
            AppendInputCharacter(' ');
            AppendOutputCharacter(' ');
            return;
        }
        if (inputCharacter < 'A' || inputCharacter > 'Z') {
            return;
        }
        AppendInputCharacter(inputCharacter);
        char outputCharacter = enigmaMachine.process(
                String.valueOf(inputCharacter)
        ).charAt(0);
        AppendOutputCharacter(outputCharacter);
        LightLamp(outputCharacter);
        //PlayKeyPressSound();
        UpdateRotorPositions();
    }
//    public void HandleKeyPress(char inputCharacter) {
//        inputCharacter = Character.toUpperCase(inputCharacter);
//        if (inputCharacter == ' ') {
//            AppendInputCharacter(' ');
//            AppendOutputCharacter(' ');
//            return;
//        }
//        if (inputCharacter < 'A' || inputCharacter > 'Z') {
//            return;
//        }
//        AppendInputCharacter(inputCharacter);
//        char outputCharacter = enigmaMachine.process(String.valueOf(inputCharacter)).charAt(0);
//        AppendOutputCharacter(outputCharacter);
//        UpdateRotorPositions();
//    }

    private void UpdateRotorPositions() {
        String positions = enigmaMachine.rotorPositions();
        leftRotorSelector.setText(String.valueOf(positions.charAt(0)));
        middleRotorSelector.setText(String.valueOf(positions.charAt(1)));
        rightRotorSelector.setText(String.valueOf(positions.charAt(2)));
    }

    private void AppendInputCharacter(char inputCharacter) {
        inputText.appendText(String.valueOf(inputCharacter));
        inputText.positionCaret(inputText.getLength());
    }

    private void AppendOutputCharacter(char outputCharacter) {
        outputText.appendText(String.valueOf(outputCharacter));
        outputText.positionCaret(outputText.getLength());
    }

    private void AppendEncryptedCharacter(char encryptedCharacter) {
        outputText.setText(
                outputText.getText() + encryptedCharacter);
    }

    private void ChangeRotorLetter(Label rotorSelector, int direction) {
        char currentLetter = rotorSelector.getText().charAt(0);
        int currentPosition = currentLetter - 'A';
        int newPosition = (currentPosition + direction + 26) % 26;
        rotorSelector.setText(String.valueOf((char) ('A' + newPosition)));

        String positions = leftRotorSelector.getText()
                + middleRotorSelector.getText()
                + rightRotorSelector.getText();
        enigmaMachine.setRotorPositions(positions);
        UpdateRotorPositions();
        ExtinguishLamp();
        ClearLampboard();
    }

//    private void IlluminateLamp(char encryptedCharacter) {
//        Label lamp = GetLamp(encryptedCharacter);
//        if (lamp != null) {
//            lamp.getStyleClass().add("lamp-lit");
//        }
//    }

    private void LightLamp(char character) {
        ExtinguishLamp();
        Label lamp = GetLamp(character);
        if (lamp != null) {
            lamp.getStyleClass().add("lamp-lit");
            illuminatedLamp = character;
        }
    }

    private void ExtinguishLamp() {
        if (illuminatedLamp == '\0') {
            return;
        }
        Label lamp = GetLamp(illuminatedLamp);
        if (lamp != null) {
            lamp.getStyleClass().remove("lamp-lit");
        }
        illuminatedLamp = '\0';
    }

    private void ClearLampboard() {
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            Label lamp = GetLamp(letter);

            if (lamp != null) {
                lamp.getStyleClass().remove("lamp-lit");
            }
        }
    }


    private void BuildPlugboard() {
        plugboardCanvas.getChildren().clear();
        plugSocketPositions.clear();
        plugSockets.clear();
        plugboardWires.clear();

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

            plugSockets.put(letter, socket);
            plugboardCanvas.getChildren().addAll(socket, label);
        }

        plugboardCanvas.setOnMouseDragged(this::HandlePlugboardDrag);
        plugboardCanvas.setOnMouseReleased(this::HandlePlugboardRelease);
        UpdatePlugboardSummary();
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
        Character nearestLetter = null;
        double nearestDistance = maximumDistance;
        for (Map.Entry<Character, Point2D> entry : plugSocketPositions.entrySet()) {
            double distance = entry.getValue().distance(point);
            if (distance <= nearestDistance) {
                nearestDistance = distance;
                nearestLetter = entry.getKey();
            }
        }
        return nearestLetter;
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
            char first = entry.getKey();
            char second = entry.getValue();
            if (first < second) pairs.add("" + first + second);
        }
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

    private Label GetLamp(char letter) {
        return switch (Character.toUpperCase(letter)) {
            case 'A' -> lampA;
            case 'B' -> lampB;
            case 'C' -> lampC;
            case 'D' -> lampD;
            case 'E' -> lampE;
            case 'F' -> lampF;
            case 'G' -> lampG;
            case 'H' -> lampH;
            case 'I' -> lampI;
            case 'J' -> lampJ;
            case 'K' -> lampK;
            case 'L' -> lampL;
            case 'M' -> lampM;
            case 'N' -> lampN;
            case 'O' -> lampO;
            case 'P' -> lampP;
            case 'Q' -> lampQ;
            case 'R' -> lampR;
            case 'S' -> lampS;
            case 'T' -> lampT;
            case 'U' -> lampU;
            case 'V' -> lampV;
            case 'W' -> lampW;
            case 'X' -> lampX;
            case 'Y' -> lampY;
            case 'Z' -> lampZ;
            default -> null;
        };
    }
}
