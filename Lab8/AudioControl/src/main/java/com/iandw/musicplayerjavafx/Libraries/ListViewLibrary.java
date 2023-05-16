/**
 *      Author: Ian Wallace copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: ListViewLibrary.java
 *      Notes: This object holds all the necessary data for ArtistListView and PlaylistListView
 *              to function as lists of Strings, used to search the Table View Object when selected.
 */

package com.iandw.musicplayerjavafx.Libraries;

import com.iandw.musicplayerjavafx.FileIO.ArtistListFileIO;
import com.iandw.musicplayerjavafx.FileIO.PlaylistFileIO;
import com.iandw.musicplayerjavafx.ResourceURLs;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class ListViewLibrary implements Runnable {
    private ObservableList<String> artistObservableList;
    private ObservableList<String> playlistObservableList;
    private final String noArtists = "* artists *";
    private final String noPlaylists = "* playlists *";
    private boolean outputArtistOnClose;
    private boolean outputPlaylistOnClose;

    public ListViewLibrary() {}

    @Override
    public void run() {
        // Input artist data if file is not empty
        try {
            if (Files.size(Path.of(ResourceURLs.getArtistListURL())) > 0) {
                artistObservableList = FXCollections.observableArrayList(ArtistListFileIO.inputArtistNameObservableList());

            } else {
                artistObservableList = FXCollections.observableArrayList();
                artistObservableList.add(noArtists);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        // Input playlist data if file is not empty
        try {
            if (Files.size(Path.of(ResourceURLs.getPlaylistsURL())) > 0) {
                playlistObservableList = FXCollections.observableArrayList(PlaylistFileIO.inputPlaylistObservableList());

            } else {
                playlistObservableList = FXCollections.observableArrayList();
                playlistObservableList.add(noPlaylists);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          ADD / REMOVE
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void addArtist(String artistName) {
        outputArtistOnClose = true;

        // If empty remove empty message
        if (artistObservableList.contains(noArtists)) {
            artistObservableList.remove(0);
        }

        if (artistName != null) {
            // Update observable list, sort, and write to file
            artistObservableList.add(artistName);

            Collections.sort(artistObservableList);
        }
    }

    public void removeArtist(String artistName) {
        outputArtistOnClose = true;
        artistObservableList.remove(artistName);

        if (artistObservableList.isEmpty()) {
            artistObservableList.add(noArtists);
        }
    }

    public void addPlaylist(String playlist) {
        outputPlaylistOnClose = true;

        // Remove placeholder if currently empty
        if (playlistObservableList.contains(noPlaylists)) {
            playlistObservableList.remove(0);
        }

        // Update observable list, sort, and write to file
        playlistObservableList.add(playlist);
        Collections.sort(playlistObservableList);
    }
    public void removePlaylist(String playlist) {
        outputPlaylistOnClose = true;
        playlistObservableList.remove(playlist);

        if (playlistObservableList.isEmpty()) {
            playlistObservableList.add(noPlaylists);
        }

    }

    // Clears both List View objects of String data and applies
    // the "empty List" notification
    public void clearObservableLists() {
        outputArtistOnClose = true;
        outputPlaylistOnClose = true;

        artistObservableList.clear();
        playlistObservableList.clear();

        artistObservableList.add(noArtists);
        playlistObservableList.add(noPlaylists);
    }

    public void onClose() {
        if (outputArtistOnClose) {
            ArtistListFileIO.outputArtistNameObservableList(artistObservableList);
        }

        if (outputPlaylistOnClose) {
            PlaylistFileIO.outputPlaylistObservableList(playlistObservableList);
        }
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          GETTERS / SETTERS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public synchronized ObservableList<String> getArtistObservableList() { return artistObservableList; }
    public synchronized ObservableList<String> getPlaylistObservableList() { return playlistObservableList; }

    public synchronized void setArtistObservableList(ObservableList<String> artistObservableList) {
        this.artistObservableList = artistObservableList;
    }

    public void setOutputListsOnClose() {
        outputArtistOnClose = true;
        outputPlaylistOnClose = true;
    }

}
