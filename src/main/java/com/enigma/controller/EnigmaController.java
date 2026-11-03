package com.enigma.controller;

import com.enigma.model.EnigmaMachine;
import com.enigma.model.MachineConfig;
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
    private Button applyRotorSettingsButton;
    @FXML
    private TextArea inputText;
    @FXML
    private TextArea outputText;
    @FXML
    private Label plugboardSummary;
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
        leftRotorSelector.setText("A");
        middleRotorSelector.setText("A");
        rightRotorSelector.setText("A");
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

    @FXML
    public void ApplyRotorSettings() {
        String leftPosition = leftRotorSelector.getText();
        String middlePosition = middleRotorSelector.getText();
        String rightPosition = rightRotorSelector.getText();
        String positions = leftPosition + middlePosition + rightPosition;
        enigmaMachine.setRotorPositions(positions);
        UpdateRotorPositions();
        ClearLampboard();
        AppendInputCharacter('\n');
        AppendInputCharacter('\n');
        AppendOutputCharacter('\n');
        AppendOutputCharacter('\n');
    }

    private void ChangeRotorLetter(Label rotorSelector, int direction) {
        char currentLetter = rotorSelector.getText().charAt(0);
        int currentPosition = currentLetter - 'A';
        int newPosition = (currentPosition + direction + 26) % 26;
        rotorSelector.setText(String.valueOf((char) ('A' + newPosition)));
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
