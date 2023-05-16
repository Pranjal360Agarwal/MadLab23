/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: TrackListFileIO.java
 *      Notes: Handles all file input/output from tracklist.ser located in resources.
 *             Observable Lists do not serialize so all TrackMetadata objects are transferred to
 *             and from an ArrayList for serialization.
 */

package com.iandw.musicplayerjavafx.FileIO;

import com.iandw.musicplayerjavafx.ResourceURLs;
import com.iandw.musicplayerjavafx.TrackMetadata;
import com.iandw.musicplayerjavafx.TrackSerializable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TrackListFileIO {

    /**
     * inputTrackObservableList() - Reads an ArrayList of TrackSerializable objects, which are then
     *                              copied into trackMetadataObserableList as TrackMetadata objects,
     *                              which can be handled by the Table View javaFX object.
     *
     *                              SimpleStringProperties used by TrackMetadata member variables
     *                              are not serializable, hence the need to copy the String data from
     *                              TrackSerializable to TrackMetadata objects.
     *
     * @return => ObservableList for trackMetadataObservableList in TableViewLibrary
     */
    public static ObservableList<TrackMetadata> inputTrackObservableList() {
        System.out.println("Reading from tracklist.ser");

        ObservableList<TrackMetadata> trackMetadataObservableList = FXCollections.observableArrayList();

        try {
            // Read from file
            InputStream in = Files.newInputStream(Path.of(ResourceURLs.getTrackListURL()));
            ObjectInputStream ois = new ObjectInputStream(in);
            ArrayList<TrackSerializable> trackArrayList = (ArrayList<TrackSerializable>) ois.readObject();
            ois.close();

            // Deep copy ArrayList<TrackSerializable> to ObservableList<TrackMetadata>
            // String -> SimpleStringProperty
            for (TrackSerializable trackSerializable : trackArrayList) {
                trackMetadataObservableList.add(new TrackMetadata(
                        trackSerializable.getArtistNameStr(),
                        trackSerializable.getTrackFileNameStr(),
                        trackSerializable.getTrackContainerTypeStr(),
                        trackSerializable.getTrackTitleStr(),
                        trackSerializable.getAlbumTitleStr(),
                        trackSerializable.getTrackGenreStr(),
                        trackSerializable.getTrackDurationStr(),
                        trackSerializable.getTrackPathStr(),
                        trackSerializable.getPlaylistStr()
                ));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return trackMetadataObservableList;
    }

    /**
     * outputTrackObservableList() - same as input but reversed.
     * @param trackMetadataObservableList => ObservableList to be output
     * @throws FileNotFoundException
     */
    public static void outputTrackObservableList(ObservableList<TrackMetadata> trackMetadataObservableList) throws FileNotFoundException {
        System.out.println("Writing to tracklist.ser");

        ArrayList<TrackSerializable> trackArrayList = new ArrayList<>();

        // Deep copy ObservableList<TrackMetadata> to ArrayList<TrackSerializable>
        // SimpleStringProperty -> String
        for (TrackMetadata trackMetadata : trackMetadataObservableList) {
            trackArrayList.add(new TrackSerializable(
                    trackMetadata.getArtistNameStr(),
                    trackMetadata.getTrackFileNameStr(),
                    trackMetadata.getTrackContainerTypeStr(),
                    trackMetadata.getTrackTitleStr(),
                    trackMetadata.getAlbumTitleStr(),
                    trackMetadata.getTrackGenreStr(),
                    trackMetadata.getTrackDurationStr(),
                    trackMetadata.getTrackPathStr(),
                    trackMetadata.getPlaylistStr()
            ));
        }

        try {
            // Write track objects to file
            OutputStream out = Files.newOutputStream(Path.of(ResourceURLs.getTrackListURL()));
            ObjectOutputStream oos = new ObjectOutputStream(out);

            oos.writeObject(trackArrayList);
            oos.close();

        } catch (IOException e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
