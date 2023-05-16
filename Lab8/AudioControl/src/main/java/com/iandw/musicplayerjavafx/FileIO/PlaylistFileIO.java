/**
 *      Author: Ian Wallace copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: PlaylistFileIO.java
 *      Notes: Handles all file input/output from playlists.ser located in resources.
 *             Observable Lists do not serialize so all String objects are transferred to and
 *             from an ArrayList for serialization.
 */

package com.iandw.musicplayerjavafx.FileIO;

import com.iandw.musicplayerjavafx.ResourceURLs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class PlaylistFileIO implements Serializable {
    /**
     * inputPlaylistObservableList() - read Artist names from artistlist.ser
     * @return => ObservableList for playlistObservableList (ListViewLibrary) to handle.
     */
    public static ObservableList<String> inputPlaylistObservableList() {

        ObservableList<String> playlistObservableList = FXCollections.observableArrayList();

        try {
            // Read from file
            System.out.println("Reading from playlists.ser");
            InputStream in = Files.newInputStream(Path.of(ResourceURLs.getPlaylistsURL()));
            ObjectInputStream ois = new ObjectInputStream(in);
            ArrayList<String> playlistArray = (ArrayList<String>) ois.readObject();
            ois.close();

            playlistObservableList.addAll(playlistArray);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return playlistObservableList;

    }

    /**
     * outputPlaylistObservableList() - write Playlist String objects to playlists.ser
     * @param playlistsObservableList => Observable List to be output to file
     */
    public static void outputPlaylistObservableList(ObservableList<String> playlistsObservableList) {

        ArrayList<String> playlistsArrayList = new ArrayList<>(playlistsObservableList);

        try {
            // Write track objects to file
            System.out.println("Writing to playlists.ser");
            OutputStream out = Files.newOutputStream(Path.of(ResourceURLs.getPlaylistsURL()));
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(playlistsArrayList);
            oos.close();

        } catch (IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

}
