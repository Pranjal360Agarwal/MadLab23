/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: ListViewController.java
 *      Notes: Handles popup windows relating to List View Context Menu items
 */

package com.iandw.musicplayerjavafx;

import com.iandw.musicplayerjavafx.Libraries.ListViewLibrary;
import com.iandw.musicplayerjavafx.Libraries.TableViewLibrary;
import com.iandw.musicplayerjavafx.Utilities.TrackIndex;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ListViewController {
    @FXML private AnchorPane anchorPane;
    @FXML private TextField listViewTextInput;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    private TableView<TrackMetadata> trackTableView;
    private ListView<String> artistListView;
    private ListView<String> playlistListView;
    private ListViewLibrary listViewLibrary;
    private TableViewLibrary tableViewLibrary;
    private TrackIndex trackIndex;
    private String windowTitle;
    private String userInput;
    private String menuSelection;
    final private String addArtist = "Add Artist";
    final private String editArtist = "Edit Artist";
    final private String createPlaylist = "Create Playlist";
    final private String editPlaylist = "Edit Playlist";

    private void initializeData(ListView<String> artistListView, ListView<String> playlistListView,
                                TableView<TrackMetadata> trackTableView, ListViewLibrary listViewLibrary,
                                TableViewLibrary tableViewLibrary, TrackIndex trackIndex,
                                String windowTitle, String menuSelection, Stage stage)
    {
        this.artistListView = artistListView;
        this.playlistListView = playlistListView;
        this.trackTableView = trackTableView;
        this.tableViewLibrary = tableViewLibrary;
        this.listViewLibrary = listViewLibrary;
        this.trackIndex = trackIndex;
        this.windowTitle = windowTitle;
        this.menuSelection = menuSelection;

        switch (windowTitle) {
            case addArtist -> listViewTextInput.setPromptText("add Artist");
            case createPlaylist -> listViewTextInput.setPromptText("create Playlist");
            case editArtist, editPlaylist -> listViewTextInput.setText(menuSelection);
        }

        //listViewTextInput.setFocusTraversable(false);
        anchorPane.requestFocus();

        // Key Bindings
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                okButton(stage);
            }
        });

        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.close();
            }
        });

    }

    /**
     * showListViewInputWindow() - entry point to edit items in List View objects
     *
     * @param artistListView => Updates List View when artist name is added or edited
     * @param playlistListView => Updates List View when playlist names are added or edited
     * @param trackTableView => Access Table View to edit all visible tracks related to List View selection
     * @param listViewLibrary => Access artist list and playlist data
     * @param tableViewLibrary => Access track metadata
     * @param trackIndex => Get Table View table size for editing methods
     * @param windowTitle => Switch based on Context Menu Item selection
     * @param menuSelection => String from List View selection model (Artist name or Playlist)
     * @throws IOException
     */
    public void showListViewInputWindow(ListView<String> artistListView, ListView<String> playlistListView,
                                        TableView<TrackMetadata> trackTableView, ListViewLibrary listViewLibrary,
                                        TableViewLibrary tableViewLibrary, TrackIndex trackIndex,
                                        String windowTitle, String menuSelection) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("listview.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        ListViewController controller = loader.getController();

        controller.initializeData(artistListView, playlistListView, trackTableView, listViewLibrary, tableViewLibrary,
                trackIndex, windowTitle, menuSelection, stage);

        stage.setTitle(windowTitle);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.isAlwaysOnTop();
        stage.show();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          OK / CANCEL BUTTONS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML
    private void okButtonClicked(MouseEvent mouseClick) {
        Stage stage = (Stage) okButton.getScene().getWindow();
        okButton(stage);
    }

    private void okButton(Stage stage) {
        userInput = listViewTextInput.getText();
        boolean closeFlag = true;

        switch (windowTitle) {

            // Add Artist
            case addArtist -> {
                System.out.println("adding artist");
                try {
                    // Throw exception if playlist is the same name as an Artist
                    // otherwise may cause bugs when choosing to remove an artist or playlist
                    if (listViewLibrary.getArtistObservableList().contains(userInput) ||
                            listViewLibrary.getPlaylistObservableList().contains(userInput)) {
                        throw new Exception();
                    }

                    listViewLibrary.addArtist(userInput);
                    artistListView.setItems(listViewLibrary.getArtistObservableList());

                    artistListView.getSelectionModel().select(userInput);

                    // Simulate mouse click to update tableview
                    if (artistListView.getSelectionModel().getSelectedItem() != null) {
                        MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                                false, false, false, false, true, false,
                                false, true, false, false, null);

                        artistListView.fireEvent(mouseEvent);
                    }

                    artistListView.refresh();

                } catch (Exception e) {
                    closeFlag = false;
                    listViewTextInput.clear();
                    listViewTextInput.setPromptText("Artist must be unique");
                    listViewTextInput.setFocusTraversable(false);
                    anchorPane.requestFocus();
                }
            }

            // Create playlist
            case createPlaylist -> {
                try {
                    // Throw exception if playlist is the same name as an Artist
                    // otherwise may cause bugs when choosing to remove an artist or playlist
                    if (listViewLibrary.getArtistObservableList().contains(userInput) ||
                            listViewLibrary.getPlaylistObservableList().contains(userInput)) {
                        throw new Exception();
                    }

                    listViewLibrary.addPlaylist(userInput);
                    playlistListView.setItems(listViewLibrary.getPlaylistObservableList());

                    playlistListView.getSelectionModel().select(userInput);

                    // Simulate mouse click to update tableview
                    if (playlistListView.getSelectionModel().getSelectedItem() != null) {
                        MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                                false, false, false, false, true, false,
                                false, true, false, false, null);

                        playlistListView.fireEvent(mouseEvent);
                    }

                } catch (Exception e) {
                    closeFlag = false;
                    listViewTextInput.clear();
                    listViewTextInput.setPromptText("Playlist must be unique");
                    listViewTextInput.setFocusTraversable(false);
                    anchorPane.requestFocus();
                }
            }

            case editArtist -> {
                try {
                    // Throw exception if playlist is the same name as an Artist
                    // otherwise may cause bugs when choosing to remove an artist or playlist
                    if (listViewLibrary.getPlaylistObservableList().contains(userInput)) {
                        throw new Exception();
                    }

                    // Edit Artist
                    if (listViewLibrary.getArtistObservableList().contains(menuSelection)) {
                        System.out.printf("Editing artist %s to %s.", menuSelection, userInput);
                        listViewLibrary.removeArtist(menuSelection);

                        if (!listViewLibrary.getArtistObservableList().contains(userInput)) {
                            listViewLibrary.addArtist(userInput);
                        }

                        artistListView.setItems(listViewLibrary.getArtistObservableList());

                        editArtist();

                        artistListView.getSelectionModel().select(userInput);
                    }

                } catch (Exception e) {
                    closeFlag = false;
                    listViewTextInput.clear();
                    listViewTextInput.setPromptText("Name must be unique");
                    listViewTextInput.setFocusTraversable(false);
                    anchorPane.requestFocus();
                }
            }

            case editPlaylist -> {
                try {
                    // Throw exception if playlist is the same name as an Artist
                    // otherwise may cause bugs when choosing to remove an artist or playlist
                    if (listViewLibrary.getArtistObservableList().contains(userInput)) {
                        throw new Exception();
                    }

                    // Edit Playlist
                    if (listViewLibrary.getPlaylistObservableList().contains(menuSelection)) {
                        System.out.printf("Editing playlist %s to %s.", menuSelection, userInput);
                        listViewLibrary.removePlaylist(menuSelection);

                        if (!listViewLibrary.getPlaylistObservableList().contains(userInput)) {
                            listViewLibrary.addPlaylist(userInput);
                        }

                        playlistListView.setItems(listViewLibrary.getPlaylistObservableList());

                        editPlaylist();

                        playlistListView.getSelectionModel().select(userInput);
                    }

                } catch (Exception e) {
                    closeFlag = false;
                    listViewTextInput.clear();
                    listViewTextInput.setPromptText("Name must be unique");
                    listViewTextInput.setFocusTraversable(false);
                    anchorPane.requestFocus();
                }
            }
        }

        if (closeFlag) {
            stage.close();
        }
    }

    @FXML
    private void cancelButtonClicked(MouseEvent mouseClick) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          EDIT PLAYLIST / ARTIST
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private void editPlaylist() {
        int tableSize = trackIndex.getTableSize();

        if (tableSize > 0) {
            // Edit every track metadata object visible in trackTableView
            for (int trackIndex = 0; trackIndex < tableSize; trackIndex++) {
                System.out.printf("Editing %s playlist to %s%n",
                        trackTableView.getItems().get(trackIndex).getTrackTitleStr(), userInput);
                trackTableView.getItems().get(trackIndex).setPlaylistStr(userInput);
                trackTableView.refresh();
            }

            // Write on close flag
            tableViewLibrary.setOutputTrackListOnClose();
        }
    }

    private void editArtist() {
        int tableSize = trackIndex.getTableSize();

        if (tableSize > 0) {
            // Edit every track metadata object visible in trackTableView
            for (int trackIndex = 0; trackIndex < tableSize; trackIndex++) {
                System.out.printf("Editing %s artist to %s%n",
                        trackTableView.getItems().get(trackIndex).getTrackTitleStr(), userInput);
                trackTableView.getItems().get(trackIndex).setArtistNameStr(userInput);
                trackTableView.refresh();
            }

            // Write on close flag
            tableViewLibrary.setOutputTrackListOnClose();
        }
    }
}
