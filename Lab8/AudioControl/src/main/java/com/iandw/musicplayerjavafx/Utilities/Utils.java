/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: Utils.java
 *      Notes: Various static functions for processing data or creating/copying directories.
 */

package com.iandw.musicplayerjavafx.Utilities;

import com.iandw.musicplayerjavafx.*;
import com.iandw.musicplayerjavafx.Libraries.ListViewLibrary;
import com.iandw.musicplayerjavafx.Libraries.TableViewLibrary;
import com.iandw.musicplayerjavafx.TrackMetadata;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

public class Utils {

    public static int maxTextAreaSize() {
        // Return in bytes max amount of text (1MB) from buffer to keep the App from locking up
        return 1000000;
    }

    public static String formatSeconds(int seconds) {
        if (seconds >= 3600) {
            return String.format("%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60);
        }

        return String.format("%02d:%02d", (seconds / 60) % 60, seconds % 60);
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          File UTILITIES
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public static void clearSerializedFiles() throws IOException {
        PrintWriter clearTrackList = new PrintWriter(ResourceURLs.getTrackListURL());
        clearTrackList.close();

        PrintWriter clearArtistList = new PrintWriter(ResourceURLs.getArtistListURL());
        clearArtistList.close();

        PrintWriter clearPlaylists = new PrintWriter(ResourceURLs.getPlaylistsURL());
        clearPlaylists.close();
    }

    /*
     * CopyFile & createDirectory
     * Used for importing artist/album/track data by MusicLibrary
     */
    public static void copyFile(String source, String destination) throws IOException {
        if (!Files.exists(Paths.get(destination))) {
            Files.copy(Paths.get(source), Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);

        } else {
            System.out.printf("Cannot copy, path %s already exists%n", destination);
        }
    }

    public static void createDirectory(String path, String directoryName) throws IOException {
        String newPath = path + File.separator + directoryName;

        File directory = new File(newPath);

        if (!directory.exists()) {
            Files.createDirectory(Paths.get(newPath));
        }
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          CONTEXT MENU UTILITIES
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


    public static void removeArtist(String removeArtistStr, ListViewLibrary listViewLibrary,
                                    TableViewLibrary tableViewLibrary, TrackIndex trackIndex,
                                    TableView<TrackMetadata> trackTableView, ListView<String> artistListView)
    {
        listViewLibrary.removeArtist(removeArtistStr);

        int tableSize = trackIndex.getTableSize();

        if (tableSize > 0) {
            // Remove Tracks from library
            for (int i = 0; i < tableSize; i++) {
                System.out.printf("Removing %s from %s%n", trackTableView.getItems().get(i).getTrackTitleStr(),
                        artistListView.getSelectionModel().getSelectedItem());
                tableViewLibrary.removeTrack(trackTableView.getItems().get(i));
                trackTableView.refresh();
            }

            tableViewLibrary.setOutputTrackListOnClose();
        }

        artistListView.setItems(listViewLibrary.getArtistObservableList());
    }

    public static void removePlaylist(String removePlaylistStr, ListViewLibrary listViewLibrary,
                                TableViewLibrary tableViewLibrary, TrackIndex trackIndex,
                                TableView<TrackMetadata> trackTableView, ListView<String> artistListView,
                                ListView<String> playlistListView)
    {
        listViewLibrary.removePlaylist(removePlaylistStr);

        int tableSize = trackIndex.getTableSize();

        if (tableSize > 0) {
            // Alter all playlist tracks playlist to "*"
            for (int i = 0; i < tableSize; i++) {
                System.out.printf("Removing %s from %s%n", trackTableView.getItems().get(i).getTrackTitleStr(),
                        artistListView.getSelectionModel().getSelectedItem());
                trackTableView.getItems().get(i).setPlaylistStr("*");
                trackTableView.refresh();
            }

            tableViewLibrary.setOutputTrackListOnClose();
        }

        playlistListView.setItems(listViewLibrary.getPlaylistObservableList());
    }

    public static void openExplorer(File file) {
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file.getParentFile());

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

}
