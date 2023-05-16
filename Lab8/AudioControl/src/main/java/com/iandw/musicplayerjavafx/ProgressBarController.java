/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: ProgressBarController.java
 *      Notes: Handles progress bar GUI window
 */

package com.iandw.musicplayerjavafx;

import com.iandw.musicplayerjavafx.Utilities.ProgressBarData;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressBarController implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label percentLabel;
    @FXML
    private Label systemTextLabel;
    @FXML
    private Button cancelButton;

    private Stage stage;
    private final ProgressBarData progressBarData;

    /**
     * ProgressBarController - constructor
     * @param progressBarData => References ProgressBarData object so that the GUI can be continuously
     *                        informed of changes to member variable data during Music Library Initialization
     */
    public ProgressBarController(ProgressBarData progressBarData) {
        this.progressBarData = progressBarData;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressBar.setProgress(0);
        percentLabel.setText("0%");
        systemTextLabel.setText("Analyzing Directory...");

        // Listener for updated double value from ProgressBarData
        // Where each file read increments the progress bar.
        progressBarData.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("progressDouble")) {
                double progressDouble = (double) evt.getNewValue();
                Platform.runLater(() -> {
                    progressBar.setProgress(progressDouble);
                    percentLabel.setText((int) Math.round(progressDouble * 100) + "%");
                });
            }
        });

        // Listener to populate systemTextLabel with processed file path
        progressBarData.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("trackPathStr")) {
                String trackPathStr = (String) evt.getNewValue();
                Platform.runLater(() -> {
                    systemTextLabel.setText(trackPathStr);
                });
            }
        });

    }

    /**
     * showProgressBarWindow() - entry point to Progress Bar GUI on Music Library Initialization
     * @throws IOException
     */
    public void showProgressBarWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("progressbar.fxml"));
        loader.setControllerFactory(progressBarController -> new ProgressBarController(progressBarData));

        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));

        // Set/Show Stage
        setStage(stage);
        stage.setAlwaysOnTop(true);
        stage.setTitle("Initialize Library");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        stage.setOnCloseRequest(event -> {
            event.consume();
            progressBarData.setContinueInitialization(false);
            stage.close();
        });
    }

    @FXML
    private void cancelButtonClicked() {
        progressBarData.setContinueInitialization(false);
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void setStage(Stage stage) { this.stage = stage; }

    public void close() { stage.close(); }

}
