package com.enigma.controller;

import com.enigma.model.EnigmaMachine;
import com.enigma.model.MachineConfig;
import com.enigma.model.RotorSpec;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnigmaControllerUiTest {

    @Test
    void OpeningInteriorRepeatedlyReusesWindowAndClosingAllowsReopening() throws Exception {
        RunOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/enigma/fxml/main-view.fxml"));
            Parent mainRoot = loader.load();
            EnigmaController controller = loader.getController();

            Stage mainStage = new Stage();
            mainStage.setScene(new Scene(mainRoot));
            mainStage.show();

            try {
                controller.OpenInteriorView();
                controller.OpenInteriorView();

                assertEquals(1, CountInteriorWindows());

                Stage interiorStage = FindInteriorStage();
                assertNotNull(interiorStage);
                interiorStage.close();

                controller.OpenInteriorView();
                assertEquals(1, CountInteriorWindows());
            } finally {
                for (Window window : Window.getWindows().stream().toList()) {
                    if (window instanceof Stage stage
                            && "Enigma Machine I — Interior".equals(stage.getTitle())) {
                        stage.close();
                    }
                }
                mainStage.close();
            }
            return null;
        });
    }

    @Test
    void InteriorPlugboardUiUpdatesTheSharedMachineAndDrawsItsWire() throws Exception {
        RunOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/enigma/fxml/interior-view.fxml"));
            loader.load();
            InteriorController controller = loader.getController();
            EnigmaMachine machine = new EnigmaMachine(MachineConfig.defaultConfig());
            controller.SetEnigmaMachine(machine);

            Method connectPair = InteriorController.class
                    .getDeclaredMethod("ConnectPlugPair", char.class, char.class);
            connectPair.setAccessible(true);
            connectPair.invoke(controller, 'A', 'B');

            assertEquals(java.util.List.of("AB"), machine.PlugboardPairs());

            Field canvasField = InteriorController.class.getDeclaredField("plugboardCanvas");
            canvasField.setAccessible(true);
            Pane canvas = (Pane) canvasField.get(controller);
            assertEquals(26, canvas.getChildren().stream()
                    .filter(Circle.class::isInstance)
                    .count());
            assertEquals(1, canvas.getChildren().stream()
                    .filter(Line.class::isInstance)
                    .count());

            Field summaryField = InteriorController.class.getDeclaredField("plugboardSummary");
            summaryField.setAccessible(true);
            javafx.scene.control.Label summary =
                    (javafx.scene.control.Label) summaryField.get(controller);
            assertTrue(summary.getText().contains("AB"));
            return null;
        });
    }

    @Test
    void RotorAndRingSettingsSurviveClosingAndReopeningTheInterior() throws Exception {
        RunOnFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/enigma/fxml/main-view.fxml"));
            Parent mainRoot = loader.load();
            EnigmaController mainController = loader.getController();
            Stage mainStage = new Stage();
            mainStage.setScene(new Scene(mainRoot));
            mainStage.show();

            try {
                mainController.OpenInteriorView();
                InteriorController interiorController = GetInteriorController(mainController);

                GetComboBox(interiorController, "leftRotorType").setValue(RotorSpec.IV);
                GetComboBox(interiorController, "middleRotorType").setValue(RotorSpec.V);
                GetComboBox(interiorController, "rightRotorType").setValue(RotorSpec.VI);
                GetComboBox(interiorController, "leftRingSetting").setValue("B");
                GetComboBox(interiorController, "middleRingSetting").setValue("C");
                GetComboBox(interiorController, "rightRingSetting").setValue("D");

                assertTrue(interiorController.InstallRotorConfiguration());
                FindInteriorStage().close();

                mainController.OpenInteriorView();
                InteriorController reopenedController = GetInteriorController(mainController);

                assertEquals(List.of(RotorSpec.IV, RotorSpec.V, RotorSpec.VI),
                        List.of(
                                GetComboBox(reopenedController, "leftRotorType").getValue(),
                                GetComboBox(reopenedController, "middleRotorType").getValue(),
                                GetComboBox(reopenedController, "rightRotorType").getValue()));
                assertEquals(List.of("B", "C", "D"),
                        List.of(
                                GetComboBox(reopenedController, "leftRingSetting").getValue(),
                                GetComboBox(reopenedController, "middleRingSetting").getValue(),
                                GetComboBox(reopenedController, "rightRingSetting").getValue()));
            } finally {
                for (Window window : Window.getWindows().stream().toList()) {
                    if (window instanceof Stage stage
                            && "Enigma Machine I — Interior".equals(stage.getTitle())) {
                        stage.close();
                    }
                }
                mainStage.close();
            }
            return null;
        });
    }

    private static InteriorController GetInteriorController(
            EnigmaController controller) throws Exception {
        Field field = EnigmaController.class.getDeclaredField("interiorController");
        field.setAccessible(true);
        return (InteriorController) field.get(controller);
    }

    @SuppressWarnings("unchecked")
    private static <T> ComboBox<T> GetComboBox(
            InteriorController controller, String fieldName) throws Exception {
        Field field = InteriorController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (ComboBox<T>) field.get(controller);
    }

    private static long CountInteriorWindows() {
        return Window.getWindows().stream()
                .filter(Stage.class::isInstance)
                .map(Stage.class::cast)
                .filter(stage -> "Enigma Machine I — Interior".equals(stage.getTitle()))
                .count();
    }

    private static Stage FindInteriorStage() {
        return Window.getWindows().stream()
                .filter(Stage.class::isInstance)
                .map(Stage.class::cast)
                .filter(stage -> "Enigma Machine I — Interior".equals(stage.getTitle()))
                .findFirst()
                .orElse(null);
    }

    private static <T> T RunOnFxThread(Callable<T> action) throws Exception {
        StartJavaFx();
        CountDownLatch finished = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                result.set(action.call());
            } catch (Throwable throwable) {
                failure.set(throwable);
            } finally {
                finished.countDown();
            }
        });

        assertTrue(finished.await(15, TimeUnit.SECONDS), "JavaFX task timed out");
        if (failure.get() != null) {
            if (failure.get() instanceof Exception exception) throw exception;
            if (failure.get() instanceof Error error) throw error;
            throw new RuntimeException(failure.get());
        }
        return result.get();
    }

    private static synchronized void StartJavaFx() throws InterruptedException {
        try {
            CountDownLatch started = new CountDownLatch(1);
            Platform.startup(started::countDown);
            assertTrue(started.await(15, TimeUnit.SECONDS), "JavaFX startup timed out");
        } catch (IllegalStateException alreadyStarted) {
            // Another UI test has already initialized the JavaFX toolkit.
        }
        Platform.setImplicitExit(false);
    }

    @AfterAll
    static void StopJavaFx() {
        Platform.exit();
    }
}
