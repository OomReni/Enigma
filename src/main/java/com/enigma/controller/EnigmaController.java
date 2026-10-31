package com.enigma.controller;

import com.enigma.model.EnigmaMachine;
import com.enigma.model.MachineConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyEvent;

public class EnigmaController {

    private final EnigmaMachine enigmaMachine = new EnigmaMachine(MachineConfig.defaultConfig());
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    @FXML
    private Label leftRotorPosition;
    @FXML
    private Label middleRotorPosition;
    @FXML
    private Label rightRotorPosition;
    @FXML
    private Label inputText;
    @FXML
    private Label outputText;
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

    /**
     * Handles a click on one of the on-screen keyboard buttons.
     */
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

    /**
     * Handles physical keyboard input.
     */
//    @FXML
//    private void HandlePhysicalKey(KeyEvent event) {
//        String keyName = event.getCode().getName();
//
//        if (keyName == null || !keyName.matches("[A-Z]")) {
//            return;
//        }
//
//        HandleKeyPress(keyName.charAt(0));
//        event.consume();
//    }

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

        // Prevent JavaFX buttons from handling these key events.
        event.consume();

        // Ignore repeated KEY_PRESSED events while the key is held.
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

        if (event.getCode() == KeyCode.SPACE
                || (event.getCode().getName().length() == 1
                && event.getCode().getName().charAt(0) >= 'A'
                && event.getCode().getName().charAt(0) <= 'Z')) {
            event.consume();
        }
    }

    /**
     * Processes one character through the Enigma machine.
     * Both keyboard input methods call this method.
     */
//    private void HandleKeyPress(char inputCharacter) {
//        char upperCaseCharacter =
//                Character.toUpperCase(inputCharacter);
//
//        if (upperCaseCharacter < 'A' || upperCaseCharacter > 'Z') {
//            return;
//        }
//
//        String encryptedText =
//                enigmaMachine.process(String.valueOf(upperCaseCharacter));
//
//        if (encryptedText.isEmpty()) {
//            return;
//        }
//
//        char encryptedCharacter = encryptedText.charAt(0);
//
//        ClearLampboard();
//        UpdateRotorPositions();
//        AppendInputCharacter(upperCaseCharacter);
//        AppendEncryptedCharacter(encryptedCharacter);
//        IlluminateLamp(encryptedCharacter);
//    }

    public void HandleKeyPress(char inputCharacter) {
        inputCharacter = Character.toUpperCase(inputCharacter);

        AppendInputCharacter(inputCharacter);

        // Spaces are display-only and must not interact with the machine.
        if (inputCharacter == ' ') {
            AppendOutputCharacter(' ');
            return;
        }

        // Reject anything outside the Enigma alphabet.
        if (inputCharacter < 'A' || inputCharacter > 'Z') {
            return;
        }

        // Encrypt exactly one character.
        char encryptedCharacter = enigmaMachine.process(String.valueOf(inputCharacter)).charAt(0);

        AppendOutputCharacter(encryptedCharacter);

        UpdateRotorPositions();
        IlluminateLamp(encryptedCharacter);
    }

    /**
     * Updates the three rotor-window labels.
     */
    private void UpdateRotorPositions() {
        String positions = enigmaMachine.rotorPositions();

        leftRotorPosition.setText(
                String.valueOf(positions.charAt(0)));

        middleRotorPosition.setText(
                String.valueOf(positions.charAt(1)));

        rightRotorPosition.setText(
                String.valueOf(positions.charAt(2)));
    }

    /**
     * Appends the original character to the input display.
     */
    private void AppendInputCharacter(char inputCharacter) {
        inputText.setText(
                inputText.getText() + inputCharacter);
    }

    private void AppendOutputCharacter(char inputCharacter) {
        outputText.setText(
                outputText.getText() + inputCharacter);
    }

    /**
     * Appends the encrypted character to the output display.
     */
    private void AppendEncryptedCharacter(char encryptedCharacter) {
        outputText.setText(
                outputText.getText() + encryptedCharacter);
    }

    /**
     * Illuminates the lamp corresponding to the encrypted character.
     */
    private void IlluminateLamp(char encryptedCharacter) {
        Label lamp = GetLamp(encryptedCharacter);

        if (lamp != null) {
            lamp.getStyleClass().add("lamp-lit");
        }
    }

    /**
     * Turns off all currently illuminated lamps.
     */
    private void ClearLampboard() {
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            Label lamp = GetLamp(letter);

            if (lamp != null) {
                lamp.getStyleClass().remove("lamp-lit");
            }
        }
    }

    /**
     * Returns the lamp label associated with a letter.
     */
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
