/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: InitializeSelectionController.java
 *      Notes: Allows User to choose between Standard and Recursive Music Library Initialization
 *          methods based on user's file structure or preference.
 */

package com.iandw.musicplayerjavafx;

import com.iandw.musicplayerjavafx.Libraries.ListViewLibrary;
import com.iandw.musicplayerjavafx.Libraries.MusicLibrary;
import com.iandw.musicplayerjavafx.Libraries.TableViewLibrary;
import com.iandw.musicplayerjavafx.Utilities.ProgressBarData;
import com.iandw.musicplayerjavafx.Utilities.UserSettings;
import com.iandw.musicplayerjavafx.Utilities.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InitializeSelectionController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button standardButton;
    @FXML
    private Button recursiveButton;
    @FXML
    private TextArea textAreaStandard;
    @FXML
    private TextArea textAreaRecursive;
    @FXML
    private Label rootDirectoryLabel;

    private MusicLibrary musicLibrary;
    private TableViewLibrary tableViewLibrary;
    private ListViewLibrary listViewLibrary;
    private UserSettings userSettings;
    private ListView<String> artistListView;
    private ListView<String> playlistListView;
    private TableView<TrackMetadata> trackTableView;
    private Stage settingsStage;



    public void initializeData(MusicLibrary musicLibrary, TableViewLibrary tableViewLibrary,
                               ListViewLibrary listViewLibrary, UserSettings userSettings,
                               ListView<String> artistListView, ListView<String> playlistListView,
                               TableView<TrackMetadata> trackTableView,  Label rootDirectoryLabel,
                               Stage settingsStage)
    {
        this.musicLibrary = musicLibrary;
        this.tableViewLibrary = tableViewLibrary;
        this.listViewLibrary = listViewLibrary;
        this.userSettings = userSettings;
        this.artistListView = artistListView;
        this.playlistListView = playlistListView;
        this.trackTableView = trackTableView;
        this.rootDirectoryLabel = rootDirectoryLabel;
        this.settingsStage = settingsStage;

        setLabelText();
    }

    /**
     * showInitializationWindow() - entry point to Music Library initialization methods
     *
     * @param musicLibrary => Accesses and processes user audio file data
     * @param tableViewLibrary => Access Observable List
     * @param listViewLibrary => Access Observable Lists
     * @param userSettings => Access rootDirectoryString and initialization type variables
     * @param artistListView => Set Artist name data on initialization
     * @param playlistListView => Set Playlist data on initialization
     * @param trackTableView => Set track metadata on initialization
     * @param rootDirectoryLabel => Update label text on initialization
     * @throws IOException
     */
    public void showInitializationWindow(MusicLibrary musicLibrary, TableViewLibrary tableViewLibrary,
                                         ListViewLibrary listViewLibrary, UserSettings userSettings,
                                         ListView<String> artistListView, ListView<String> playlistListView,
                                         TableView<TrackMetadata> trackTableView, Label rootDirectoryLabel,
                                         Stage settingsStage) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("initializeselection.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        InitializeSelectionController controller = loader.getController();

        controller.initializeData(musicLibrary, tableViewLibrary, listViewLibrary, userSettings,
                artistListView, playlistListView, trackTableView, rootDirectoryLabel, settingsStage);

        stage.setTitle("Initialize Library");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.requestFocus();
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.show();
    }

    private void setLabelText() {
        // Set selection information text
        textAreaStandard.setText("""
                Best for organized music libraries that follow the file structure:
                Music Folder -> Artist Folder -> Album Folder -> Track.mp3
                _or_
                Music Folder -> Artist Folder -> Track.mp3
                Names and titles are obtained from folder and file names.""");

        textAreaRecursive.setText("""
                Best for unorganized music libraries that can follow any file structure:
                Music Folder -> Folder A -> Folder B -> Folder n -> Track.mp3
                All track data is obtained from the file's metadata.
                Note: file metadata may be inaccurate, incomplete, or missing.""");
    }

    @FXML
    private void standardClicked() throws IOException {
        // Create DirectoryChooser for root Music Directory
        DirectoryChooser rootMusicDirectoryChooser = new DirectoryChooser();
        rootMusicDirectoryChooser.setTitle("Select Music Folder");
        rootMusicDirectoryChooser.setInitialDirectory((new File(".")));

        Stage stage = (Stage) anchorPane.getScene().getWindow();
        File file = rootMusicDirectoryChooser.showDialog(stage);

        if (file != null) {
            Path rootDirectoryPath = file.toPath();

            if (Files.exists(rootDirectoryPath)) {
                rootDirectoryLabel.setText(rootDirectoryPath.toString());

                userSettings.setRootMusicDirectoryString(rootDirectoryPath.toString());
                userSettings.setInitializationString("standard");
                System.out.println("Initializing metadata");

                // Clear current list file and observable list
                Utils.clearSerializedFiles();
                tableViewLibrary.clearObservableList();
                listViewLibrary.clearObservableLists();

                stage.close();

                // Holds data for progressbar to update to
                ProgressBarData progressBarData = new ProgressBarData(userSettings.getRootMusicDirectoryString());

                // Run initializeMusicLibrary on separate thread to free up Application Thread
                // for ProgressBarController
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            musicLibrary.standardInitialization(progressBarData);

                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedIOException();
                            }

                            Platform.runLater(() -> loadLibraries());
                            System.out.println("Finished initializing.");
                            System.out.printf("updated root directory: %s%n", rootDirectoryPath);

                        } catch (InterruptedIOException consumed) {
                            System.out.println("Cancelled Library Initialization.");
                        }

                        return null;
                    }
                };

                // Open progress bar window
                ProgressBarController progressBarController = new ProgressBarController(progressBarData);
                progressBarController.showProgressBarWindow();

                // Re-initialize with new metadata from new root directory
                musicLibrary.clearMusicLibrary();
                musicLibrary.setRootMusicDirectoryString(rootDirectoryPath.toString());

                // Cancel task thread on Cancel Button clicked
                progressBarData.addPropertyChangeListener(evt -> {
                    if (evt.getPropertyName().equals("continueInitialization")) {
                        boolean continueInitialization = (boolean) evt.getNewValue();
                        Platform.runLater(() -> {
                            if (!continueInitialization) {
                                task.cancel();
                                task.setOnCancelled(null);
                            }
                        });
                    }
                });

                task.setOnSucceeded(evt -> progressBarController.close());
                task.setOnFailed(evt -> {
                    System.out.println("Initialization Failed.");
                    progressBarController.close();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initOwner(settingsStage);
                    alert.setTitle("Error");
                    alert.setHeaderText("Initialization Failed");
                    alert.setContentText("Invalid file type or folder hierarchy.\nCheck console log for details.");
                    alert.showAndWait();

                });

                // Start initializeMusicLibrary() thread
                Thread thread = new Thread(task);
                thread.start();
            }

        } else {
            rootDirectoryLabel.setText("Select file or directory");
        }

        stage.close();
    }

    @FXML
    private void recursiveClicked() throws IOException {
        // Create DirectoryChooser for root Music Directory
        DirectoryChooser rootMusicDirectoryChooser = new DirectoryChooser();
        rootMusicDirectoryChooser.setTitle("Select Music Folder");
        rootMusicDirectoryChooser.setInitialDirectory((new File(".")));

        Stage stage = (Stage) anchorPane.getScene().getWindow();
        File file = rootMusicDirectoryChooser.showDialog(stage);

        if (file != null) {
            Path rootDirectoryPath = file.toPath();

            if (Files.exists(rootDirectoryPath)) {
                rootDirectoryLabel.setText(rootDirectoryPath.toString());

                userSettings.setRootMusicDirectoryString(rootDirectoryPath.toString());
                userSettings.setInitializationString("recursive");

                System.out.println("Initializing metadata");

                // Clear current list file and observable list
                Utils.clearSerializedFiles();
                tableViewLibrary.clearObservableList();
                listViewLibrary.clearObservableLists();

                stage.close();

                // Holds data for progressbar to update to
                ProgressBarData progressBarData = new ProgressBarData(userSettings.getRootMusicDirectoryString());

                // Run initializeMusicLibrary on separate thread to free up Application Thread
                // for ProgressBarController
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            musicLibrary.recursiveInitialization(progressBarData);

                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedIOException();
                            }

                            Platform.runLater(() -> loadLibraries());
                            Platform.runLater(() -> Collections.sort(listViewLibrary.getArtistObservableList()));
                            System.out.println("Finished initializing.");
                            System.out.printf("updated root directory: %s%n", rootDirectoryPath);

                        } catch (InterruptedIOException consumed) {
                            System.out.println("Cancelled Library Initialization.");
                        }

                        return null;
                    }
                };

                // Open progress bar window
                ProgressBarController progressBarController = new ProgressBarController(progressBarData);
                progressBarController.showProgressBarWindow();

                // Re-initialize with new metadata from new root directory
                musicLibrary.clearMusicLibrary();
                musicLibrary.setRootMusicDirectoryString(rootDirectoryPath.toString());

                // Cancel task thread on Cancel Button clicked
                progressBarData.addPropertyChangeListener(evt -> {
                    if (evt.getPropertyName().equals("continueInitialization")) {
                        boolean continueInitialization = (boolean) evt.getNewValue();
                        Platform.runLater(() -> {
                            if (!continueInitialization) {
                                task.cancel();
                                task.setOnCancelled(null);
                            }
                        });
                    }
                });

                task.setOnSucceeded(evt -> progressBarController.close());
                task.setOnFailed(evt -> {
                    System.out.println("Initialization Failed.");
                    progressBarController.close();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.initOwner(settingsStage);
                    alert.setTitle("Error");
                    alert.setHeaderText("Initialization Failed");
                    alert.setContentText("Invalid file type or folder hierarchy.\nCheck console log for details.");
                    alert.showAndWait();
                });

                // Start initializeMusicLibrary() thread
                Thread thread = new Thread(task);
                thread.start();
            }

        } else {
            rootDirectoryLabel.setText("Select file or directory");
        }

        stage.close();
    }

    private void loadLibraries() {
        listViewLibrary.setArtistObservableList(musicLibrary.getArtistNameObservableList());
        tableViewLibrary.setTrackObservableList(musicLibrary.getTrackObservableList());

        // Set Listview and Tableview
        artistListView.setItems(listViewLibrary.getArtistObservableList());
        playlistListView.setItems(listViewLibrary.getPlaylistObservableList());
        trackTableView.setItems(musicLibrary.getTrackObservableList());

        trackTableView.refresh();
        artistListView.refresh();
        playlistListView.refresh();
    }
}
