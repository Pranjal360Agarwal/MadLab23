/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: ArtistListContextMenu.java
 *      Notes: Handles all Context Menu Requests for artistListView ListView object
 */

package com.iandw.musicplayerjavafx.ContextMenus;

import com.iandw.musicplayerjavafx.*;
import com.iandw.musicplayerjavafx.Libraries.ListViewLibrary;
import com.iandw.musicplayerjavafx.Libraries.TableViewLibrary;
import com.iandw.musicplayerjavafx.TrackMetadata;
import com.iandw.musicplayerjavafx.Utilities.TrackIndex;
import com.iandw.musicplayerjavafx.Utilities.UserSettings;
import com.iandw.musicplayerjavafx.Utilities.Utils;

import java.io.File;
import java.io.IOException;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ArtistListContextMenu {

    /**
     * getContextMenu() - context menu entry point
     *
     * @param artistListView => updates List View based on any changes made to Artist Name
     * @param playlistListView => updates List View based on any changes made to Playlist title
     * @param trackTableView => updates trackTableView Observable List when artist names are edited or removed
     * @param listViewLibrary => encapsulates Artist and Playlist ObservableList arrays for changes to object data
     * @param tableViewLibrary => encapsulates Track Metadata ObservableList for changes to object data
     * @param trackIndex => used to access currently viewed Table View cells
     * @param userSettings => used to get rootDirectoryString for File Explorer
     */
    public static void getContextMenu(ListView<String> artistListView, ListView<String> playlistListView,
                                      TableView<TrackMetadata> trackTableView, ListViewLibrary listViewLibrary,
                                      TableViewLibrary tableViewLibrary, TrackIndex trackIndex, UserSettings userSettings) {

        String selectedItem = artistListView.getSelectionModel().getSelectedItem();
        System.out.println("Artists selected: " + selectedItem);

        ContextMenu contextMenu = new ContextMenu();

        // Add to list
        MenuItem addArtist = new MenuItem("Add Artist");
        SeparatorMenuItem divider1 = new SeparatorMenuItem();

        // Edit list
        MenuItem editArtist = new MenuItem("Edit Name");


        // Remove from list
        MenuItem removeArtist = new MenuItem("Remove");
        SeparatorMenuItem divider2 = new SeparatorMenuItem();

        // View folder in explorer
        MenuItem openInExplorer = new MenuItem("Open in Explorer");

        // Add Artist
        addArtist.setOnAction(event -> {
            addArtist(artistListView, playlistListView, trackTableView, listViewLibrary, tableViewLibrary, trackIndex);
        });

        // Edit Artist or Playlist
        editArtist.setOnAction(event -> {
            editArtist(artistListView, playlistListView, trackTableView, listViewLibrary, tableViewLibrary, trackIndex);
        });

        // Remove Artist
        removeArtist.setOnAction(event -> {
            removeArtist(artistListView, trackTableView, listViewLibrary, tableViewLibrary, trackIndex);
        });

        // Open in File Explorer
        openInExplorer.setOnAction(event -> {
            String menuSelection = artistListView.getSelectionModel().getSelectedItem();
            File file = new File(userSettings.getRootMusicDirectoryString() + File.separator + menuSelection);

            if (menuSelection != null) {
                Utils.openExplorer(file);
            }
        });

        contextMenu.getItems().addAll(addArtist, divider1, editArtist, removeArtist, divider2, openInExplorer);

        artistListView.setContextMenu(contextMenu);
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          ADD ARTIST
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    public static void addArtist(ListView<String> artistListView, ListView<String> playlistListView,
                                 TableView<TrackMetadata> trackTableView, ListViewLibrary listViewLibrary,
                                 TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        try {
            String windowTitle = "Add Artist";
            String menuSelection = artistListView.getSelectionModel().getSelectedItem();

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
     *                          EDIT ARTIST NAME
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void editArtist(ListView<String> artistListView, ListView<String> playlistListView,
                                   TableView<TrackMetadata> trackTableView, ListViewLibrary listViewLibrary,
                                   TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        try {
            String windowTitle = "Edit Artist";
            String menuSelection = artistListView.getSelectionModel().getSelectedItem();

            if (menuSelection != null) {
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
     *                          REMOVE ARTIST
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    private static void removeArtist(ListView<String> artistListView, TableView<TrackMetadata> trackTableView,
                                     ListViewLibrary listViewLibrary, TableViewLibrary tableViewLibrary, TrackIndex trackIndex)
    {
        String menuSelection = artistListView.getSelectionModel().getSelectedItem();

        if (menuSelection != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Artist");
            alert.setHeaderText("Removing Artist deletes their tracks from Library,\n but does not affect files or folders.");
            alert.setContentText("Would you like to continue?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                Utils.removeArtist(menuSelection, listViewLibrary, tableViewLibrary, trackIndex, trackTableView, artistListView);
            }
        }

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
