/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: PlaylistContextMenu.java
 *      Notes: Handles all Context Menu Requests for playlistListView object
 */

package com.iandw.musicplayerjavafx.ContextMenus;

import com.iandw.musicplayerjavafx.*;
import com.iandw.musicplayerjavafx.Libraries.ListViewLibrary;
import com.iandw.musicplayerjavafx.Libraries.TableViewLibrary;
import com.iandw.musicplayerjavafx.TrackMetadata;
import com.iandw.musicplayerjavafx.Utilities.TrackIndex;
import com.iandw.musicplayerjavafx.Utilities.Utils;

import java.io.IOException;
import java.util.Objects;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PlaylistContextMenu {

    private static final String emptyPlaylist = "* playlists *";

    /**
     * getContextMenu() - context menu entry point
     *
     * @param artistListView => updates List View based on any changes made to Artist Name
     * @param playlistListView => updates List View based on any changes made to Playlist title
     * @param trackTableView => updates trackTableView Observable List when playlist titles are edited or removed
     * @param listViewLibrary => encapsulates Artist and Playlist ObservableList arrays for changes to object data
     * @param tableViewLibrary => encapsulates Track Metadata ObservableList for changes to object data
     * @param trackIndex => used to access currently viewed Table View cells
     */
    public static void getContextMenu(ListView<String> artistListView, ListView<String> playlistListView,
                                      TableView<TrackMetadata> trackTableView, ListViewLibrary listViewLibrary,
                                      TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        String selectedItem = playlistListView.getSelectionModel().getSelectedItem();
        System.out.println("Playlist selected: " + selectedItem);

        ContextMenu contextMenu = new ContextMenu();

        // Add to list
        MenuItem createPlaylist = new MenuItem("Create Playlist");
        SeparatorMenuItem divider1 = new SeparatorMenuItem();

        // Edit List
        MenuItem editPlaylist = new MenuItem("Edit Title");

        // Remove from list
        MenuItem removePlaylist = new MenuItem("Remove");
        SeparatorMenuItem divider2 = new SeparatorMenuItem();

        // View folder in explorer
        MenuItem openInExplorer = new MenuItem("Open in Explorer");

        // Create Playlist
        createPlaylist.setOnAction(event -> {
            createPlaylist(artistListView, playlistListView, trackTableView, listViewLibrary, tableViewLibrary, trackIndex);
        });

        // Edit Playlist title
        editPlaylist.setOnAction(event -> {
            editPlaylist(artistListView, playlistListView, trackTableView, listViewLibrary, tableViewLibrary, trackIndex);
        });

        // Remove selected playlist
        removePlaylist.setOnAction(event -> {
            removePlaylist(artistListView, playlistListView, trackTableView, listViewLibrary, tableViewLibrary, trackIndex);
        });

        contextMenu.getItems().addAll(createPlaylist, divider1, editPlaylist, removePlaylist, divider2, openInExplorer);

        playlistListView.setContextMenu(contextMenu);

    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          CREATE PLAYLIST
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static void createPlaylist(ListView<String> artistListView, ListView<String> playlistListView,
                               TableView<TrackMetadata> trackTableView, ListViewLibrary listViewLibrary,
                               TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        try {
            String windowTitle = "Create Playlist";
            String menuSelection = playlistListView.getSelectionModel().getSelectedItem();

            ListViewController listViewController = new ListViewController();
            listViewController.showListViewInputWindow(artistListView, playlistListView, trackTableView,
                    listViewLibrary, tableViewLibrary, trackIndex, windowTitle, menuSelection);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          EDIT PLAYLIST
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void editPlaylist(ListView<String> artistListView, ListView<String> playlistListView,
                                     TableView<TrackMetadata> trackTableView, ListViewLibrary listViewLibrary,
                                     TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        try {
            String windowTitle = "Edit Playlist";
            String menuSelection = playlistListView.getSelectionModel().getSelectedItem();

            if (menuSelection != null && !Objects.equals(menuSelection, emptyPlaylist)) {
                ListViewController listViewController = new ListViewController();
                listViewController.showListViewInputWindow(artistListView, playlistListView, trackTableView,
                        listViewLibrary, tableViewLibrary, trackIndex, windowTitle, menuSelection);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          REMOVE PLAYLIST
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void removePlaylist(ListView<String> artistListView, ListView<String> playlistListView,
                                       TableView<TrackMetadata> trackTableView, ListViewLibrary listViewLibrary,
                                       TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        String menuSelection = playlistListView.getSelectionModel().getSelectedItem();

        if (menuSelection != null && !Objects.equals(menuSelection, emptyPlaylist)) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Playlist");
            alert.setHeaderText("Removing playlist does not affect files or folders.");
            alert.setContentText("Would you like to continue?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                Utils.removePlaylist(menuSelection, listViewLibrary, tableViewLibrary, trackIndex,
                        trackTableView, artistListView, playlistListView);
            }
        }

        playlistListView.getSelectionModel().select(0);

        // Simulate mouse click to update tableview
        if (playlistListView.getSelectionModel().getSelectedItem() != null) {
            MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                    false, false, false, false, true, false,
                    false, true, false, false, null);

            playlistListView.fireEvent(mouseEvent);
        }

        trackTableView.refresh();
    }

}
