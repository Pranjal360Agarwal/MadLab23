/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: ProgressBarData.java
 *      Notes: Holds variable data for ProgressBarController. Provides the user with a
 *          percentage of audio files initialized and the file paths being processed.
 *          Also bridges the ProgressBarController with the MusicLibrary via a
 *          PropertyChangeListener.
 */

package com.iandw.musicplayerjavafx.Utilities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProgressBarData implements java.io.Serializable {
    private double progressDouble;
    private String trackPathStr;
    private boolean continueInitialization;
    private final PropertyChangeSupport propertySupport;
    private final double fileAmount;
    private double fileIndex;

    public ProgressBarData(String rootDirectory) throws IOException {
        propertySupport = new PropertyChangeSupport(this);

        // Get size of directory
        fileAmount = (double) Files.find(Paths.get(rootDirectory),
                3, (path, attributes) -> attributes.isRegularFile()).count() - 1;

        System.out.println("Total Files: " + fileAmount);
        fileIndex = 0.0;
        progressDouble = 0.0;
        continueInitialization = true;
    }

    // Increments every time a file is processed in the innermost folder
    public void increaseProgress(String trackPathStr) {
        fileIndex++;
        double percent = fileIndex / fileAmount;

        if (percent < 0.0) { percent = 0.0; }
        if (percent > 1.0) { percent = 1.0; }

        setProgressDouble(percent);
        setTrackPathStr(trackPathStr);
    }

    public void setTrackPathStr(String newValue) {
        String oldValue = trackPathStr;
        trackPathStr = newValue;
        propertySupport.firePropertyChange("trackPathStr", oldValue, newValue);
    }

    public void setProgressDouble(double newValue) {
        double oldValue = progressDouble;
        progressDouble = newValue;
        propertySupport.firePropertyChange("progressDouble", oldValue, newValue);
    }

    public void setContinueInitialization(boolean newValue) {
        boolean oldValue = continueInitialization;
        continueInitialization = newValue;
        propertySupport.firePropertyChange("continueInitialization", oldValue, newValue);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

}
