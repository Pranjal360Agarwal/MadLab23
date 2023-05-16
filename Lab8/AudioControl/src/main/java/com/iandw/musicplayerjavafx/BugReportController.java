/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: BugReportController.java
 *      Notes: Handles Window for sending Dev a bug report (via jakarta.mail)
 */

package com.iandw.musicplayerjavafx;

import com.iandw.musicplayerjavafx.Utilities.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import io.github.cdimascio.dotenv.Dotenv;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BugReportController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField userEmailField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea textArea;
    @FXML
    private Button sendButton;
    @FXML
    private Button consoleLogButton;
    @FXML
    private Label statusLabel;
    private ByteArrayOutputStream consoleOutput;

    public void initializeData(ByteArrayOutputStream consoleOutput, Stage stage) {
        this.consoleOutput = consoleOutput;
        setTextFieldFocus();

        // Close key binding
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });
    }


    /**
     * showBugReportWindow() - entry point to bug report object
     *
     * @param consoleOutput => Allow user to 'attach' console log in bug report
     * @throws IOException
     */
    public void showBugReportWindow(ByteArrayOutputStream consoleOutput) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("bugreport.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        BugReportController controller = loader.getController();

        controller.initializeData(consoleOutput, stage);

        stage.setTitle("Bug Report");
        stage.setAlwaysOnTop(false);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

    }


    private void setTextFieldFocus() {
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);

        // Keeps prompt text readable in text fields
        userNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && firstTime.get()) {
                anchorPane.requestFocus();
                firstTime.setValue(false);
            }
        });
    }

    @FXML
    private void insertConsoleLogClicked() {
        // Add console log to bottom of text area
        if (consoleOutput.size() < Utils.maxTextAreaSize()) {
            textArea.setText(textArea.getText() + '\n' + consoleOutput.toString());
        } else {
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.setAlwaysOnTop(false);
            Dotenv dotenv = Dotenv.configure().load();
            final String devEmail = dotenv.get("BUG_REPORT_EMAIL");

            System.out.println("File size too large");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Console Log");
            alert.setHeaderText("Console Log size > 1MB.");
            alert.setContentText("Please e-mail consolelog.txt as an attachment to " + devEmail +
                    " after closing the application.");
            alert.showAndWait();
            alert.onCloseRequestProperty().addListener(observable -> stage.setAlwaysOnTop(true));
        }
    }

    @FXML
    private void sendClicked() {

        // Update UI on background thread
        Task<Void> task = new Task<>() {

            @Override
            protected Void call() {
                Platform.runLater(() -> statusLabel.setText("Sending message..."));
                Dotenv dotenv = Dotenv.configure().load();
                final String devEmail = dotenv.get("BUG_REPORT_EMAIL");
                final String token = dotenv.get("TOKEN");

                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.starttls.enable", "true"); //TLS
                props.put("mail.smtp.auth", "true");

                // Authenticates via dev bugreport gmail account
                Authenticator authenticator = new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(devEmail, token);
                    }
                };

                Session session = Session.getInstance(props, authenticator);

                try {
                    // Create message
                    MimeMessage message = new MimeMessage(session);
                    message.setRecipients(Message.RecipientType.TO, devEmail);
                    message.setSubject(subjectField.getText());
                    message.setSentDate(new Date());

                    // Add user email to top of bug report
                    message.setText(
                            "From: " + userNameField.getText() + '\n' +
                                    "Email: " + userEmailField.getText() + '\n' +
                                    "App: MusicPlayer" + '\n' +
                                    textArea.getText()
                    );

                    // Send it
                    Transport.send(message);

                    // Update UI on a background thread
                    Platform.runLater(() -> {
                        statusLabel.setText("Message sent!");
                        sendButton.disableProperty().set(true);
                        setTextFieldFocus();
                    });

                } catch (MessagingException mex) {
                    // Update UI on a background thread
                    Platform.runLater(() -> {
                        statusLabel.setText("Send failed.");
                        System.out.println("send failed, exception: " + mex);
                        sendButton.disableProperty().set(false);
                    });
                }

                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
}
