package com.enigma;

import com.enigma.model.EnigmaMachine;
import com.enigma.model.MachineConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/** Small console smoke-test until the JavaFX presentation layer is added. */
public final class Main {
    private Main() {}

    public static void main(String[] args) {
        MachineConfig config = MachineConfig.defaultConfig();
        EnigmaMachine machine = new EnigmaMachine(config);
        System.out.println("Enigma I core demonstration");
        System.out.println("Rotors: I-II-III | Reflector: B | Rings: AAA | Start: AAA | Plugboard: empty");
        System.out.println("AAAAA -> " + machine.process("AAAAA"));
        System.out.println("Final rotor positions: " + machine.rotorPositions());
    }
}
