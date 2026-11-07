package com.enigma.controller;

import com.enigma.model.EnigmaMachine;
import com.enigma.model.RotorSpec;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

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

    private EnigmaMachine enigmaMachine;

    @FXML
    public void initialize() {
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

    @FXML
    public void CloseInteriorView() {
        Stage stage = (Stage) configurationStatus.getScene().getWindow();
        stage.close();
    }
}
