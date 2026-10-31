package com.enigma.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;

public class EnigmaController {
    @FXML
    private void HandleOnScreenKey(ActionEvent event) {
        Button button = (Button) event.getSource();
        HandleKeyPress(button.getText().charAt(0));
    }
    private void HandlePhysicalKey(KeyEvent event) {
        if (event.isAltDown()
                || event.isControlDown()
                || event.isMetaDown()) {
            return;
        }

        String text = event.getText();

        if (text != null && text.length() == 1) {
            char key = Character.toUpperCase(text.charAt(0));

            if (key >= 'A' && key <= 'Z') {
                HandleKeyPress(key);
                event.consume();
            }
        }
    }


    public void HandleKeyPress(char key) {
        char input = Character.toUpperCase(key);

        if (input < 'A' || input > 'Z') {
            return;
        }

        // The machine steps its rotors and encrypts this character.
        char encrypted = EnigmaMachine.pressKey(input);

        // Reflect the new machine state in the UI.
        updateRotorPositions();
        illuminateLamp(encrypted);

        // Keep the input and encrypted text in sync.
        appendInputCharacter(input);
        appendEncryptedCharacter(encrypted);
    }

}
