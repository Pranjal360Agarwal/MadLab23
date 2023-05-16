/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: TableViewContextMenu.java
 *      Notes: Handles all Context Menu Requests for trackTableView Table View object
 */

package com.iandw.musicplayerjavafx.ContextMenus;

import com.iandw.musicplayerjavafx.*;
import com.iandw.musicplayerjavafx.Libraries.ListViewLibrary;
import com.iandw.musicplayerjavafx.Libraries.TableViewLibrary;
import com.iandw.musicplayerjavafx.TrackMetadata;
import com.iandw.musicplayerjavafx.Utilities.TrackIndex;
import com.iandw.musicplayerjavafx.Utilities.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TableViewContextMenu {

    /**
     * getContextMenu() - context menu entry point
     *
     * @param artistListView => updates List View based on any changes made to Artist Name
     * @param trackTableView => updates List View based on any changes made to Playlist title
     * @param listViewLibrary => encapsulates Artist and Playlist ObservableList arrays for changes to object data
     * @param tableViewLibrary =>  encapsulates Track Metadata ObservableList for changes to TrackMetadata object data
     * @param trackIndex => used to access currently viewed Table View cells
     */
    public static void getContextMenu(ListView<String> artistListView, ListView<String> playlistListView, TableView<TrackMetadata> trackTableView,
                                      ListViewLibrary listViewLibrary, TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        ContextMenu contextMenu = new ContextMenu();

        // Playlist Options
        ArrayList<MenuItem> playlistMenuList = new ArrayList<>();
        Menu addTrackToPlaylist = new Menu("Add to Playlist");
        MenuItem removeTrackFromPlaylist = new MenuItem("Remove from Playlist");
        final String emptyPlaylist = "* playlists *";
        SeparatorMenuItem divider1 = new SeparatorMenuItem();

        // Add playlists to menu
        for (String playlist : listViewLibrary.getPlaylistObservableList()) {
            if (!Objects.equals(playlist, emptyPlaylist)) {
                playlistMenuList.add(new MenuItem(playlist));
            }
        }

        addTrackToPlaylist.getItems().addAll(playlistMenuList);

        // Edit track data
        Menu editTrack = new Menu("Edit Track");
        MenuItem editArtistName = new MenuItem("Artist Name");
        MenuItem editTrackTitle = new MenuItem("Track Title");
        MenuItem editAlbumTitle = new MenuItem("Album Title");
        MenuItem editTrackGenre = new MenuItem("Genre");

        MenuItem deleteTrack = new MenuItem("Delete Track");
        SeparatorMenuItem divider2 = new SeparatorMenuItem();

        editTrack.getItems().addAll(editArtistName, editAlbumTitle, editTrackTitle, editTrackGenre);

        // Explorer/Properties items
        MenuItem openInExplorer = new MenuItem("Open in Explorer");

        // Add track to Playlist
        addTrackToPlaylist.setOnAction(event ->  {
            addTrackToPlaylist(event, trackTableView, tableViewLibrary, trackIndex);
        });

        // Remove track from Playlist
        removeTrackFromPlaylist.setOnAction(event -> {
            removeTrackFromPlaylist(playlistListView, trackTableView, tableViewLibrary, trackIndex);
        });

        // Edit Artist Name
        editArtistName.setOnAction(event -> {
            editArtistName(artistListView, trackTableView, listViewLibrary, tableViewLibrary);
        });

        // Edit Album Title
        editAlbumTitle.setOnAction(event -> {
            editAlbumTitle(artistListView, trackTableView, listViewLibrary, tableViewLibrary);
        });

        // Edit Track Title
        editTrackTitle.setOnAction(event -> {
            editTrackTitle(artistListView, trackTableView, listViewLibrary, tableViewLibrary);
        });

        // Edit Genre
        editTrackGenre.setOnAction(event -> {
            editTrackGenre(artistListView, trackTableView, listViewLibrary, tableViewLibrary);
        });

        // Delete Track
        deleteTrack.setOnAction(event -> {
            deleteTrack(artistListView, trackTableView, tableViewLibrary);
        });

        // Open in File Explorer
        openInExplorer.setOnAction(event -> {
            File file = new File(trackTableView.getSelectionModel().getSelectedItem().getTrackPathStr());
            if (file.exists()) {
                Utils.openExplorer(file);
            }
        });

        contextMenu.getItems().addAll(addTrackToPlaylist, removeTrackFromPlaylist, divider1, editTrack, deleteTrack, divider2, openInExplorer);

        trackTableView.setContextMenu(contextMenu);

        trackTableView.refresh();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          ADD TO PLAYLIST
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void addTrackToPlaylist(ActionEvent event, TableView<TrackMetadata> trackTableView,
                                           TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        int tableSize = trackIndex.getTableSize();

        if (tableSize > 0) {
            System.out.printf("Add %s to %s%n", trackTableView.getSelectionModel().getSelectedItem().getTrackTitleStr(), ((MenuItem) event.getTarget()).getText());
            trackTableView.getSelectionModel().getSelectedItem().setPlaylistStr(((MenuItem) event.getTarget()).getText());

            System.out.printf("track playlist set to: %s%n", trackTableView.getSelectionModel().getSelectedItem().getPlaylistStr());
            tableViewLibrary.setOutputTrackListOnClose();
        }
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          REMOVE FROM PLAYLIST
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void removeTrackFromPlaylist(ListView<String> playlistListView, TableView<TrackMetadata> trackTableView,
                                                TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        int tableSize = trackIndex.getTableSize();

        if (tableSize > 0 && !Objects.equals(trackTableView.getSelectionModel().getSelectedItem().getPlaylistStr(), "*")) {
            System.out.printf("Removing %s from %s%n", trackTableView.getSelectionModel().getSelectedItem().getTrackTitleStr(),
                    trackTableView.getSelectionModel().getSelectedItem().getPlaylistStr());

            trackTableView.getSelectionModel().getSelectedItem().setPlaylistStr("*");
            tableViewLibrary.setOutputTrackListOnClose();
        }

        // Simulate mouse click to update tableview
        if (playlistListView.getSelectionModel().getSelectedItem() != null) {
            MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                    false, false, false, false, true, false,
                    false, true, false, false, null);

            playlistListView.fireEvent(mouseEvent);
        }

        trackTableView.refresh();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          EDIT ARTIST NAME
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void editArtistName(ListView<String> artistListView, TableView<TrackMetadata> trackTableView,
                                       ListViewLibrary listViewLibrary, TableViewLibrary tableViewLibrary)
    {
        try {
            EditTrackController editTrackController = new EditTrackController();
            String columnName = "Artist Name";
            String currentTrackTitle = trackTableView.getSelectionModel().getSelectedItem().getArtistNameStr();
            String selectedArtist = trackTableView.getSelectionModel().getSelectedItem().getArtistNameStr();
            artistListView.getSelectionModel().select(selectedArtist);
            System.out.println(currentTrackTitle);

            try {
                editTrackController.showEditWindow(columnName, currentTrackTitle, trackTableView, artistListView,
                        listViewLibrary, tableViewLibrary);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        trackTableView.refresh();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          EDIT ALBUM TITLE
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void editAlbumTitle(ListView<String> artistListView, TableView<TrackMetadata> trackTableView,
                                       ListViewLibrary listViewLibrary, TableViewLibrary tableViewLibrary)
    {
        EditTrackController editTrackController = new EditTrackController();
        String columnName = "Album Title";
        String currentTrackAlbum = trackTableView.getSelectionModel().getSelectedItem().getAlbumTitleStr();
        System.out.println(currentTrackAlbum);

        try {
            editTrackController.showEditWindow(columnName, currentTrackAlbum, trackTableView, artistListView,
                    listViewLibrary, tableViewLibrary);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        trackTableView.refresh();

    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          EDIT TRACK TITLE
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void editTrackTitle(ListView<String> artistListView, TableView<TrackMetadata> trackTableView,
                                       ListViewLibrary listViewLibrary, TableViewLibrary tableViewLibrary)
    {
        EditTrackController editTrackController = new EditTrackController();
        String columnName = "Track Title";
        String currentTrackTitle = trackTableView.getSelectionModel().getSelectedItem().getTrackTitleStr();
        System.out.println(currentTrackTitle);

        try {
            editTrackController.showEditWindow(columnName, currentTrackTitle, trackTableView, artistListView,
                    listViewLibrary, tableViewLibrary);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        trackTableView.refresh();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          EDIT TRACK GENRE
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void editTrackGenre(ListView<String> artistListView, TableView<TrackMetadata> trackTableView,
                                       ListViewLibrary listViewLibrary, TableViewLibrary tableViewLibrary)
    {
        EditTrackController editTrackController = new EditTrackController();
        String columnName = "Genre";
        String currentGenre = trackTableView.getSelectionModel().getSelectedItem().getTrackGenreStr();
        System.out.println(currentGenre);

        try {
            editTrackController.showEditWindow(columnName, currentGenre, trackTableView, artistListView,
                    listViewLibrary, tableViewLibrary);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        trackTableView.refresh();

    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          DELETE TRACK
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void deleteTrack(ListView<String> artistListView, TableView<TrackMetadata> trackTableView,
                                    TableViewLibrary tableViewLibrary)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Track");
        alert.setHeaderText("You are about to delete " + trackTableView.getSelectionModel().getSelectedItem().getTrackTitleStr());
        alert.setContentText("Would you like to continue?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            // Get current artist selected
            String selectedArtist = trackTableView.getSelectionModel().getSelectedItem().getArtistNameStr();

            // Delete Track
            System.out.printf("Removing %s from %s%n", trackTableView.getSelectionModel().getSelectedItem().getTrackTitleStr(),
                    artistListView.getSelectionModel().getSelectedItem());

            tableViewLibrary.removeTrack(trackTableView.getSelectionModel().getSelectedItem());

            // Refocus on current artist for tableview to refresh
            artistListView.getSelectionModel().select(selectedArtist);

            // Simulate mouse click to update tableview
            if (artistListView.getSelectionModel().getSelectedItem() != null) {
                MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                        false, false, false, false, true, false,
                        false, true, false, false, null);

                artistListView.fireEvent(mouseEvent);
            }

            trackTableView.refresh();
        }
    }

}
