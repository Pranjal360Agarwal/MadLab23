/**
 *      Author: Ian Wallace copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: ResourceURLs.java
 *      Notes: Holds static String objects for quick file location anywhere in the Application
 */

package com.iandw.musicplayerjavafx;

import java.util.Objects;

public class ResourceURLs {
    /**
     *
     *  In IDE compile
     *
     */
    private static final String settingsURL = Objects.requireNonNull(App.class.getResource(
                    "settings.json")).toString().substring(6);

    private static final String artistlistURL = Objects.requireNonNull(App.class.getResource(
                    "artistlist.ser")).toString().substring(6);

    private static final String tracklistURL = Objects.requireNonNull(App.class.getResource(
                    "tracklist.ser")).toString().substring(6);

    private static final String playlistsURL = Objects.requireNonNull(App.class.getResource(
                    "playlists.ser")).toString().substring(6);

    private static final String consolelogURL = Objects.requireNonNull(App.class.getResource(
                    "consolelog.txt")).toString().substring(6);

    private static final String autoplayiconURL = Objects.requireNonNull(App.class.getResource(
                    "autoplay2.png")).toString().substring(6);


    /*
     *                      DEFAULT ALBUM ART FILES
     */

    private static final String musicnoteslightURL = Objects.requireNonNull(App.class.getResource(
                    "musicnotes-light.png")).toString().substring(6);

    private static final String musicnotesdarkURL = Objects.requireNonNull(App.class.getResource(
                    "musicnotes-dark.png")).toString().substring(6);

    private static final String musicnotesgreenURL = Objects.requireNonNull(App.class.getResource(
                    "musicnotes-green.png")).toString().substring(6);

    private static final String musicnotesblueURL = Objects.requireNonNull(App.class.getResource(
                    "musicnotes-blue.png")).toString().substring(6);

    private static final String musicnotesredURL = Objects.requireNonNull(App.class.getResource(
                    "musicnotes-red.png")).toString().substring(6);

    private static final String musicnotespinkURL = Objects.requireNonNull(App.class.getResource(
                    "musicnotes-pink.png")).toString().substring(6);

    private static final String musicnotesconsoleURL = Objects.requireNonNull(App.class.getResource(
                    "musicnotes-console.png")).toString().substring(6);


    /**
     *
     *                      GETTERS
     *
     */

    public synchronized static String getSettingsURL() { return settingsURL; }
    public static String getArtistListURL() { return artistlistURL; }
    public static String getTrackListURL() { return tracklistURL; }
    public static String getPlaylistsURL() { return playlistsURL; }
    public static String getConsolelogURL() { return consolelogURL; }
    // Image urls
    public static String getAutoplayiconURL() { return autoplayiconURL; }
    public static String getMusicnotesLightURL() { return musicnoteslightURL; }
    public static String getMusicnotesDarkURL() { return musicnotesdarkURL; }
    public static String getMusicnotesGreenURL() { return musicnotesgreenURL; }
    public static String getMusicnotesBlueURL() { return musicnotesblueURL; }
    public static String getMusicnotesRedURL() { return musicnotesredURL; }
    public static String getMusicnotesPinkURL() { return musicnotespinkURL; }
    public static String getMusicnotesConsoleURL() { return musicnotesconsoleURL; }

}
