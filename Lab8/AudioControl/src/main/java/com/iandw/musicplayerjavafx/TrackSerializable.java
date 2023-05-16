/**
 *      Author: Ian Wallace copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: TrackSerializable.java
 *      Notes: Holds individual audio file Metadata for serialization when outputting
 *              Observable List Arrays to tracklist.ser
 */

package com.iandw.musicplayerjavafx;

import java.io.Serializable;

public class TrackSerializable implements Serializable {
    private final String artistNameStr;
    private final String trackFileNameStr;
    private final String trackContainerTypeStr;
    private final String trackTitleStr;
    private final String albumTitleStr;
    private final String trackDurationStr;
    private final String trackGenreStr;
    private final String trackPathStr;
    private final String playlistStr;

    /**
     * TrackSerializable - constructor, a new object is made when TracklistFileIO is called to write
     *      user TrackMetadata to file (For each TrackMetadata object).
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
    public TrackSerializable(String artistNameStr,      String trackFileNameStr,    String trackContainerTypeStr,
                             String trackTitleStr,      String albumTitleStr,       String trackGenreStr,
                             String trackDurationStr,   String trackPathStr,        String playlistStr)
    {
        this.artistNameStr = artistNameStr;
        this.trackFileNameStr = trackFileNameStr;
        this.trackContainerTypeStr = trackContainerTypeStr;
        this.trackTitleStr = trackTitleStr;
        this.albumTitleStr = albumTitleStr;
        this.trackDurationStr = trackDurationStr;
        this.trackGenreStr = trackGenreStr;
        this.trackPathStr = trackPathStr;
        this.playlistStr = playlistStr;

    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          GETTERS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public String getArtistNameStr() { return artistNameStr; }
    public String getTrackFileNameStr() { return trackFileNameStr; }
    public String getTrackContainerTypeStr() { return trackContainerTypeStr; }
    public String getTrackTitleStr() { return trackTitleStr; }
    public String getAlbumTitleStr() { return albumTitleStr; }
    public String getTrackDurationStr() { return trackDurationStr; }
    public String getTrackGenreStr() { return trackGenreStr; }
    public String getTrackPathStr() { return trackPathStr; }
    public String getPlaylistStr() { return playlistStr; }

}
