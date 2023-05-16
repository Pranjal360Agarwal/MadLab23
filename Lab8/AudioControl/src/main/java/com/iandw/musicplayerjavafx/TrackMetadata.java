/**
 *      Author: Ian Wallace copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: TrackMetadata.java
 *      Notes: Holds individual audio file Metadata for Application to access.
 *          Used in Observable List Arrays.
 */

package com.iandw.musicplayerjavafx;

import javafx.beans.property.SimpleStringProperty;

public class TrackMetadata {
    private SimpleStringProperty artistNameStr;
    private final SimpleStringProperty trackFileNameStr;
    private final SimpleStringProperty trackContainerTypeStr;
    private SimpleStringProperty trackTitleStr;
    private SimpleStringProperty albumTitleStr;
    private final SimpleStringProperty trackDurationStr;
    private SimpleStringProperty trackGenreStr;
    private SimpleStringProperty trackPathStr;
    private SimpleStringProperty playlistStr;

    /**
     * TrackMetadata - constructor, a new object is made after every audio file is processed in
     *      Music Library Standard or Recursive initialization or when audio files are imported via
     *      File Menu items.
     *
     * @param artistNameStr => Artist name
     * @param trackFileNameStr => Track file name (includes extension)
     * @param trackContainerTypeStr => String only containing file extension i.e. ".mp3" or ".aiff", includes dot
     * @param trackTitleStr => Track title
     * @param albumTitleStr => Album Title
     * @param trackGenreStr => a IDv3 tag (i.e. "(32)") or explicit genre (i.e. "Rock") or null
     * @param trackDurationStr => Formatted to String representation of Duration object in "HH:MM:SS" format
     *
     * @param trackPathStr => File path of audio file (From root to file i.e.
     *                     "C:/User/Music/AristName/AlbumTitle/TrackTitle.mp3")
     *
     * @param playlistStr => Name of playlist track is "saved" to. Track will be found based on search predicate
     *                    methods in SearchTableView.java
     */
    public TrackMetadata(String artistNameStr,    String trackFileNameStr, String trackContainerTypeStr,
                         String trackTitleStr,    String albumTitleStr,    String trackGenreStr,
                         String trackDurationStr, String trackPathStr,     String playlistStr)
    {
        this.artistNameStr = new SimpleStringProperty(artistNameStr);
        this.trackFileNameStr = new SimpleStringProperty(trackFileNameStr);
        this.trackContainerTypeStr = new SimpleStringProperty(trackContainerTypeStr);
        this.trackTitleStr = new SimpleStringProperty(trackTitleStr);
        this.albumTitleStr = new SimpleStringProperty(albumTitleStr);
        this.trackGenreStr = new SimpleStringProperty(trackGenreStr);
        this.trackDurationStr = new SimpleStringProperty(trackDurationStr);
        this.trackPathStr = new SimpleStringProperty(trackPathStr);
        this.playlistStr = new SimpleStringProperty(playlistStr);
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          SETTERS/GETTERS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public String getArtistNameStr() { return artistNameStr.get(); }
    public String getTrackFileNameStr() { return trackFileNameStr.get(); }
    public String getTrackContainerTypeStr() { return trackContainerTypeStr.get(); }
    public String getTrackTitleStr() { return trackTitleStr.get(); }
    public String getAlbumTitleStr() { return albumTitleStr.get(); }
    public String getTrackDurationStr() {return trackDurationStr.get(); }
    public String getTrackGenreStr() { return trackGenreStr.get(); }
    public String getTrackPathStr() { return trackPathStr.get(); }
    public String getPlaylistStr() { return playlistStr.get(); }

    public void setArtistNameStr(String artistNameStr) { this.artistNameStr = new SimpleStringProperty(artistNameStr); }
    public void setTrackTitleStr(String trackTitleStr) { this.trackTitleStr = new SimpleStringProperty(trackTitleStr); }
    public void setAlbumTitleStr(String albumTitleStr) { this.albumTitleStr = new SimpleStringProperty(albumTitleStr); }
    public void setTrackGenreStr(String trackGenreStr) { this.trackGenreStr = new SimpleStringProperty(trackGenreStr); }
    public void setPlaylistStr(String playlistStr) { this.playlistStr = new SimpleStringProperty(playlistStr); }
    public void setTrackPathStr(String trackPathStr) { this.trackPathStr = new SimpleStringProperty(trackPathStr); }
}
