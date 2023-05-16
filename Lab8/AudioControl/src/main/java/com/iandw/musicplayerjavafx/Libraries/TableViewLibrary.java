/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: TableViewLibrary.java
 *      Notes: This object holds all necessary Audio File Metadata for the Applications
 *              Table View JavaFX object.
 *
 *              Contains two data structures for track metadata:
 *                  1. trackMetadataObservableList - main array to hold all user audio file data.
 *                  2. filteredList - secondary data structure to hold tracks which meet search
 *                      parameters.
 */

package com.iandw.musicplayerjavafx.Libraries;

import com.iandw.musicplayerjavafx.FileIO.TrackListFileIO;
import com.iandw.musicplayerjavafx.ResourceURLs;
import com.iandw.musicplayerjavafx.TrackMetadata;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class TableViewLibrary implements Runnable {
    // Main list to hold all Track objects for TableView
    private ObservableList<TrackMetadata> trackMetadataObservableList;

    // Filtered from trackObservableList from Search Bar or either List View
    private FilteredList<TrackMetadata> filteredList;
    private boolean outputTrackListOnClose;

    public TableViewLibrary() {}

    @Override
    public void run() {
        // Load trackTableView
        try {
            if (Files.size(Path.of(ResourceURLs.getTrackListURL())) > 0) {
                trackMetadataObservableList = FXCollections.observableArrayList(TrackListFileIO.inputTrackObservableList());

            } else {
                trackMetadataObservableList = FXCollections.observableArrayList();

            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void createFilteredList() {
        //TODO => see if these filtered lists object lifetime is limited or not
        filteredList = new FilteredList<>(FXCollections.observableArrayList(trackMetadataObservableList));
    }

    public synchronized void addTrack(TrackMetadata trackMetadata) {
        outputTrackListOnClose = true;
        trackMetadataObservableList.add(trackMetadata);
    }

    public synchronized void removeTrack(TrackMetadata trackMetadata) {
        outputTrackListOnClose = true;
        trackMetadataObservableList.remove(trackMetadata);
    }

    public synchronized void clearObservableList() { trackMetadataObservableList.clear(); }

    public void onClose() throws FileNotFoundException {
        if (outputTrackListOnClose) {
            TrackListFileIO.outputTrackObservableList(trackMetadataObservableList);
        }
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          SETTERS / GETTERS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public synchronized void setTrackObservableList(ObservableList<TrackMetadata> trackMetadataObservableList) {
        ObservableList<TrackMetadata> tempList = FXCollections.observableArrayList(this.trackMetadataObservableList);
        tempList.addAll(trackMetadataObservableList);
        this.trackMetadataObservableList = tempList;
    }
    public synchronized ObservableList<TrackMetadata> getTrackObservableList() { return trackMetadataObservableList; }
    public FilteredList<TrackMetadata> getFilteredList() { return filteredList; }
    public void setOutputTrackListOnClose() { outputTrackListOnClose = true; }

}
