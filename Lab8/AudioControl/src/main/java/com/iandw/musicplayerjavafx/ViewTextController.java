/**
 *      Author: Ian Wallace copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: ViewTextController.java
 *      Notes: Handles popup window for About and Console Log Menu Items
 */

package com.iandw.musicplayerjavafx;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ViewTextController {
    @FXML private TextArea textArea;
    @FXML private Button copyToClipboard;
    private ByteArrayOutputStream consoleOutput;
    private String menuChoice;
    private Stage stage;

    public void initialize() {}

    public void initializeData(String menuChoice, ByteArrayOutputStream consoleOutput, Stage stage) throws IOException {
        this.menuChoice = menuChoice;
        this.consoleOutput = consoleOutput;
        this.stage = stage;

        fillTextArea();

        // Close key binding
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });
    }

    /**
     * showViewTextWindow() - entry point for GUI to hold plain text
     *
     * @param menuChoice => User selected Menu Item from Menu Bar
     * @param consoleOutput => Access object to output all System.out... to Text Area
     * @throws IOException
     */
    public void showViewTextWindow(String menuChoice, ByteArrayOutputStream consoleOutput) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("viewtext.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));

        ViewTextController controller = loader.getController();
        controller.initializeData(menuChoice, consoleOutput, stage);

        stage.setTitle(menuChoice);
        stage.setAlwaysOnTop(false);
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }

    private void fillTextArea() throws IOException {
        final String consoleLog = "Console Log";
        final String about = "About";

        switch (menuChoice) {
            case consoleLog -> viewConsoleLog();
            case about -> viewAbout();
        }
    }

    private void viewConsoleLog() {
        textArea.setText(consoleOutput.toString());
        textArea.setFocusTraversable(false);
    }

    private void viewAbout() throws IOException {
        try {
            Document doc = Jsoup.connect("https://raw.githubusercontent.com/wallace-i/MusicPlayerJavaFx/master/README.md").get();
            // Parse README.md from github readme raw file
            String htmlString = doc.toString();
            String cleanString = Jsoup.parse(htmlString).wholeText();

            textArea.setText(cleanString);
            textArea.setFocusTraversable(false);

        } catch (RuntimeException | UnknownHostException | ConnectException | SocketTimeoutException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Connection Issues");
            alert.setContentText("Could not connect to 'raw.githubusercontent.com',\n please check the GitHub to view README.md for information");
            alert.showAndWait();
            stage.close();
        }
    }

    @FXML
    private void copyClicked() {
        String textAreaString = textArea.getText();
        StringSelection stringSelection = new StringSelection(textAreaString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
