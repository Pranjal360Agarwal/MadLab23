/**
 *      Author: Ian Wallace copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: MusicLibrary.java
 *      Notes: This object processes all user audio files to be used in Application.
 *
 *              __Initialization__
 *
 *             Two kinds of audio file initialization (the extraction of track metadata to be
 *             stored by the Application for Media Player playback and to propagate the TableView):
 *                  1. Standard - Strict file hierarchy, uses Directory names for Artist and
 *                      Album names.
 *                  2. Recursive - Recursively processes each file within root directory.
 *
 *              __Importing__
 *
 *              Also contains audio file import via the Import Menu Item in Menu Bar.
 *              Three kinds of file import:
 *                  1. Artist - imports the artist folder and all albums and tracks within.
 *                  2. Album - imports the album folder and all tracks within.
 *                  3. Track - imports a single track.
 *
 *              Import notes:
 *                  - All import methods use a strict file hierarchy, no recursive function.
 *                  - Imported files are all copied over into root directory (not moved).
 *                  - Imported files will create a new directory if Artist and/or Album does not
 *                    currently exist.
 *
 */

package com.iandw.musicplayerjavafx.Libraries;

import com.iandw.musicplayerjavafx.Utilities.ProgressBarData;
import com.iandw.musicplayerjavafx.TrackMetadata;
import com.iandw.musicplayerjavafx.Utilities.UserSettings;
import com.iandw.musicplayerjavafx.Utilities.ID3v1Genres;
import com.iandw.musicplayerjavafx.Utilities.ImportCategory;
import com.iandw.musicplayerjavafx.Utilities.Utils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

public class MusicLibrary {
    private final ObservableList<TrackMetadata> trackMetadataObservableList;
    private final ObservableList<String> artistNameObservableList;
    private final List<String> supportedFileTypes;
    private String artistNameStr;
    private String albumDirectoryStr;
    private String trackPathStr;
    private String trackFileName;
    private String trackContainerType;
    private int index;
    private ImportCategory importCategory;
    private String rootMusicDirectoryString;
    private Boolean continueInitializing;

    /**
     * MusicLibrary() - initialize arrays for track metadata objects
     * @param userSettings => initialize rootMusicDirectoryString from user settings JSON file
     */
    public MusicLibrary(UserSettings userSettings) {
        rootMusicDirectoryString = userSettings.getRootMusicDirectoryString();
        trackMetadataObservableList = FXCollections.observableArrayList();
        artistNameObservableList = FXCollections.observableArrayList();
        supportedFileTypes = Arrays.asList(".aif", ".aiff", ".mp3", "mp4", ".m4a", ".wav");
        continueInitializing = true;
    }

    public void clearMusicLibrary() {
        trackMetadataObservableList.clear();
        artistNameObservableList.clear();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          STANDARD INITIALIZATION
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * initializeMusicLibrary() - entry point to acquiring track metadata for Application to store and use.
     *
     *      If a file is not one of the supported file times, it will be passed over.
     *      Follows strict directory Hierarchy:
     *      Either Root -> Artist -> Album -> TrackFile.mp3 or Root -> Artist -> TrackFile.mp3
     *      All track metadata written to tracklist.ser
     *
     * @param progressBarData => Passes progress data to ProgressBarController
     * @throws IOException
     */
    public void standardInitialization(ProgressBarData progressBarData) throws IOException {
        System.out.println("Initializing observable list");
        ArrayList<String> tempArtistArray = new ArrayList<>();
        Utils.clearSerializedFiles();

        Path rootPath = Paths.get(rootMusicDirectoryString);

        if (Files.exists(rootPath)) {
            if (Files.isDirectory(rootPath)) {
                DirectoryStream<Path> musicDir = Files.newDirectoryStream(rootPath);

                // MUSIC DIRECTORY => LOOP THROUGH ARTIST FOLDERS
                for (Path artistFolder : musicDir) {

                    // Break on Cancel Button Clicked
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Cancelling gracefully...");
                        break;
                    }

                    Path artistDirectoryPath = artistFolder.toAbsolutePath();

                    artistNameStr = artistDirectoryPath.toString().substring(artistDirectoryPath.toString().lastIndexOf(File.separator) + 1);

                    if (Files.isDirectory(artistDirectoryPath)) {
                        tempArtistArray.add(artistNameStr);

                    } else {
                        System.out.printf("%s is not a directory%n", artistDirectoryPath);
                    }

                    if (Files.isDirectory(artistFolder)) {
                        DirectoryStream<Path> artistDir = Files.newDirectoryStream(artistDirectoryPath);

                        // ARTIST DIRECTORY => LOOP THROUGH ALBUM FOLDERS
                        for (Path albumFolder : artistDir) {

                            // Break on Cancel Button Clicked
                            if (Thread.currentThread().isInterrupted()) {
                                System.out.println("Cancelling gracefully...");
                                break;
                            }

                            Path albumDirectoryPath = albumFolder.toAbsolutePath();
                            albumDirectoryStr = albumDirectoryPath.toString().substring(albumDirectoryPath.toString().lastIndexOf(File.separator) + 1);

                            if (Files.isDirectory(albumFolder)) {
                                DirectoryStream<Path> albumDirPath = Files.newDirectoryStream(albumDirectoryPath);

                                // ALBUM DIRECTORY => LOOP THROUGH TRACK FILES
                                for (Path trackPath : albumDirPath) {
                                    if (Files.isRegularFile(trackPath) && Files.exists(trackPath) ) {
                                        trackPathStr = trackPath.toAbsolutePath().toString();
                                        trackFileName = trackPath.getFileName().toString();
                                        trackContainerType = trackPathStr.substring(trackPathStr.lastIndexOf('.'));

                                        // Check for playable file container
                                        if (supportedFileTypes.contains(trackContainerType.toLowerCase())) {
                                            standardParse();
                                            progressBarData.increaseProgress(trackPathStr);

                                        } else {
                                            System.out.printf("%s is not a compatible file type.%n", trackFileName);

                                        }

                                    } else {
                                        System.out.printf("%s is not a file%n", trackPath);
                                    }
                                }

                            } else {
                                // ARTIST DIRECTORY => LOOP THROUGH TRACK FILES
                                // Used when no album folder exists
                                // albumDirectoryPath is equal to trackPath when there is no album directory for audio Files
                                if (Files.exists(albumDirectoryPath)) {
                                    trackPathStr = albumDirectoryPath.toAbsolutePath().toString();
                                    trackFileName = albumDirectoryPath.getFileName().toString();
                                    trackContainerType = trackPathStr.substring(trackPathStr.lastIndexOf('.'));

                                    // Check for playable file container
                                    if (supportedFileTypes.contains(trackContainerType.toLowerCase())) {
                                        standardParse();
                                        progressBarData.increaseProgress(trackPathStr);

                                    } else {
                                        System.out.printf("%s is not a compatible file type.", trackFileName);
                                    }

                                } else {
                                    System.out.printf("%s does not exist%n", albumDirectoryPath);
                                }
                            }
                        }

                    } else {
                        System.out.printf("%s is not an artist directory%n", artistDirectoryPath);
                    }
                }

            } else {
                System.out.printf("%s is not a directory%n", rootPath);
            }

        } else {
            System.out.printf("%s does not exist%n", rootPath);
        }

        // Add artist names to observable array after parsing is finished and thread safe
        Platform.runLater(() -> artistNameObservableList.addAll(tempArtistArray));
    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          RECURSIVE INITIALIZATION
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * recursiveInitialization() - entry point to acquiring track metadata for Application to store and use.
     *
     *      If a file is not one of the supported file times, it will be passed over.
     *      All track metadata written to tracklist.ser
     *      Will access each file withint rootdirectory regardless of file hierarchy.
     *
     * @param progressBarData => Passes progress data to ProgressBarController
     * @throws IOException
     */
    public void recursiveInitialization(ProgressBarData progressBarData) throws IOException {
        System.out.println("Initializing observable list");
        ArrayList<String> tempArtistArray = new ArrayList<>();

        Utils.clearSerializedFiles();

        Path rootPath = Paths.get(rootMusicDirectoryString);

        if (Files.exists(rootPath)) {
            if (Files.isDirectory(rootPath)) {
                File rootDirectory = new File(rootMusicDirectoryString);

                trackMetadataObservableList.addAll(listFileTree(rootDirectory, progressBarData, tempArtistArray));

            } else {
                System.out.printf("%s is not a directory%n", rootPath);
            }

        } else {
            System.out.printf("%s does not exist%n", rootPath);
        }

        // Add artist names to observable array after parsing is finished and thread safe
        Platform.runLater(() -> artistNameObservableList.addAll(tempArtistArray));
    }



    private Collection<TrackMetadata> listFileTree(File dir, ProgressBarData progressBarData, ArrayList<String> tempArtistArray) {
        Set<TrackMetadata> fileTree = new HashSet<>();

        if (dir == null || dir.listFiles() == null) {
            return fileTree;
        }

        // Return on Cancel Button Clicked
        if (Thread.currentThread().isInterrupted()) {
            System.out.println("Cancelling gracefully...");
            return fileTree;
        }

        for (File entry : Objects.requireNonNull(dir.listFiles())) {
            if (entry.isFile()) {
                trackPathStr = entry.getAbsolutePath();
                trackFileName = entry.getName();
                trackContainerType = trackPathStr.substring(trackPathStr.lastIndexOf('.'));

                // Break on Cancel Button Clicked
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Cancelling gracefully...");
                    break;
                }

                if (supportedFileTypes.contains(trackContainerType.toLowerCase())) {
                    recursiveParse(tempArtistArray);
                    progressBarData.increaseProgress(trackPathStr);

                } else {
                    System.out.println(trackFileName + " is not a compatible file type.");
                }

            } else {
                fileTree.addAll(listFileTree(entry, progressBarData, tempArtistArray));
            }
        }

        return fileTree;
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          IMPORT METHODS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * importArtist() - Imports track data via Artist folder. File hierarchy includes:
     *                  Artist -> Album -> Track.mp3 or
     *                  Artist -> Track.mp3
     * @throws IOException
     */
    public void importArtist(File file) throws IOException {
        importCategory = ImportCategory.ARTIST;

        // Clear list to write Artist's tracks
        trackMetadataObservableList.clear();

        // Import Artist metadata into Music Library
        if (file != null) {
            Path artistPath = file.toPath();
            artistNameStr = artistPath.toString().substring(artistPath.toString().lastIndexOf(File.separator) + 1);

            if (Files.isDirectory(artistPath)) {
                DirectoryStream<Path> artistDirectory = Files.newDirectoryStream(artistPath);

                index = 0;

                // Album Folder
                for (Path albumPath : artistDirectory) {
                    if (Files.isDirectory(albumPath)) {
                        DirectoryStream<Path> albumDirectory = Files.newDirectoryStream(albumPath);
                        albumDirectoryStr = albumPath.toString().substring(albumPath.toString().lastIndexOf(File.separator) + 1);

                        // Tracks in Album
                        for (Path trackPath : albumDirectory) {
                            if (Files.isRegularFile(trackPath)) {
                                if (Files.exists(trackPath)) {
                                    importTrackLogic(trackPath, rootMusicDirectoryString);
                                }

                            } else {
                                System.out.printf("%s is not a file%n", trackPath);
                            }
                        }

                    } else {
                        // No album folder, Tracks in Artist
                        if (Files.exists(albumPath)) {
                            trackPathStr = albumPath.toAbsolutePath().toString();
                            trackFileName = albumPath.getFileName().toString();
                            trackContainerType = trackPathStr.substring(trackPathStr.lastIndexOf('.'));

                            // Check for playable file container
                            if (supportedFileTypes.contains(trackContainerType.toLowerCase())) {

                                // Standard Parse ok here
                                standardParse();

                            } else {
                                System.out.printf("%s is not a compatible file type.", trackFileName);
                            }
                        }
                    }
                }

                artistNameStr = trackMetadataObservableList.get(0).getArtistNameStr();
            }

        } else {
            System.out.println("Track File empty or does not exist");
        }
    }

    /**
     * importAlbum() - Imports Album metadata, simple file Hierarchy:
     *                  Album Folder -> Track.mp3
     * @throws IOException
     */
    public void importAlbum(File file) throws IOException {
        importCategory = ImportCategory.ALBUM;

        // Clear list to write album
        trackMetadataObservableList.clear();

        // Import Album metadata into Music Library
        if (file != null) {
            Path albumPath = file.toPath();
            albumDirectoryStr = albumPath.toString().substring(albumPath.toString().lastIndexOf(File.separator) + 1);

            if (Files.isDirectory(albumPath)) {
                DirectoryStream<Path> albumDirectory = Files.newDirectoryStream(albumPath);

                index = 0;

                for (Path trackPath : albumDirectory) {
                    if (Files.isRegularFile(trackPath)) {
                        if (Files.exists(trackPath)) {
                            importTrackLogic(trackPath, rootMusicDirectoryString);
                        }

                    } else {
                        System.out.printf("%s is not a file%n", trackPath);
                    }
                }

                artistNameStr = trackMetadataObservableList.get(0).getArtistNameStr();

            } else {
                System.out.printf("%s is not a directory%n", albumPath);
            }

        } else {
            System.out.println("Track File empty or does not exist");
        }

    }

    /**
     * importTrack() - imports a single audio file into the music library
     * @throws IOException
     */
    public void importTrack(File file) throws IOException {
        importCategory = ImportCategory.TRACK;

        // Clear list to write track
        trackMetadataObservableList.clear();

        // Import Track metadata into Music Library
        if (file != null) {
            Path trackPath = file.toPath();

            if (Files.isRegularFile(trackPath)) {
                if (Files.exists(trackPath)) {

                    index = 0;

                    importTrackLogic(trackPath, rootMusicDirectoryString);

                    artistNameStr = trackMetadataObservableList.get(0).getArtistNameStr();
                    System.out.println("ArtistName: " + artistNameStr);
                }

            } else {
                System.out.printf("%s is not a file%n", trackPath);
            }

        } else {
            System.out.println("Track File empty or does not exist");
        }

    }

    private void importTrackLogic(Path trackPath, String rootDirectory) throws IOException {
        trackPathStr = trackPath.toAbsolutePath().toString();
        trackFileName = trackPath.getFileName().toString();
        trackContainerType = trackPathStr.substring(trackPathStr.lastIndexOf('.'));

        // Check for playable file container
        if (supportedFileTypes.contains(trackContainerType.toLowerCase())) {

            importParse();

            try {
                // Check for slashes which could interfere with file creation
                final String trackArtist = removeSlashes(trackMetadataObservableList.get(index).getArtistNameStr());
                final String trackAlbum = removeSlashes(trackMetadataObservableList.get(index).getAlbumTitleStr());
                final String trackFileName = removeSlashes(trackMetadataObservableList.get(index).getTrackFileNameStr());
                final String source = trackPath.toString();
                final String destination = rootDirectory + File.separator + trackArtist + File.separator +
                        trackAlbum + File.separator + trackFileName;

                // Create new Artist/Album Directories, filepath and move track
                // If Artist Directory Exists move to Artist Directory,
                if (Files.exists(Path.of(rootDirectory + File.separator + trackArtist))) {

                    // If Album Directory Exists move to Album Directory,
                    if (Files.exists(Path.of(rootDirectory + File.separator + trackArtist + File.separator + trackAlbum))) {
                        Utils.copyFile(source, destination);

                    } else {
                        // Else create Album Directory hen move file
                        Utils.createDirectory(rootDirectory + File.separator + trackArtist, trackAlbum);
                        Utils.copyFile(source, destination);
                    }

                } else {
                    System.out.println(trackArtist);
                    // Else Create new Artist and Album Directory
                    Utils.createDirectory(rootDirectory, trackArtist);
                    Utils.createDirectory(rootDirectory + File.separator + trackArtist, trackAlbum);
                    Utils.copyFile(source, destination);

                }

                trackMetadataObservableList.get(index).setTrackPathStr(destination);
                index++;

            } catch (IndexOutOfBoundsException e) {
                System.out.println("Import failed");
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

        } else {
            System.out.printf("%s is not a compatible file type.", trackFileName);
        }
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          PARSE METADATA METHODS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // For Standard Initialization
    private void standardParse() {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(trackPathStr));
            Tag tag = audioFile.getTag();
            String trackTitle = trackFileName;
            String trackAlbum;
            String trackGenre;
            final String duration = Utils.formatSeconds(audioFile.getAudioHeader().getTrackLength());
            final String playlist = "*";

            // Check title metadata for null value, if true replace with file name substring
            if (tag.getFirst(FieldKey.TITLE) == null || Objects.equals(tag.getFirst(FieldKey.TITLE), "")) {
                trackTitle = trackFileName.substring(0, trackTitle.indexOf('.'));

                if (Character.isDigit(trackTitle.charAt(0))) {
                    trackTitle = filterDigitsFromTitle(trackTitle);
                }

            } else {
                trackTitle = tag.getFirst(FieldKey.TITLE);
            }

            // If still null replace with trackFileName (Redundancy)
            if (trackTitle == null) {
                trackTitle = trackFileName;
            }

            // Check album metadata for null value, if true replace with directory name
            if (tag.getFirst(FieldKey.ALBUM) == null || Objects.equals(tag.getFirst(FieldKey.ALBUM), "")) {
                trackAlbum = albumDirectoryStr;

            } else {
                trackAlbum = tag.getFirst(FieldKey.ALBUM);
            }

            // Check genre metadata for null value, if true leave blank
            if (tag.getFirst(FieldKey.GENRE) == null) {
                trackGenre = null;

            } else {
                trackGenre = tag.getFirst(FieldKey.GENRE);
            }

            assert trackGenre != null;
            if (trackGenre.startsWith("(")) {
                String trackGenreID = trackGenre.substring(trackGenre.indexOf('(') + 1, trackGenre.indexOf(')'));
                trackGenre = ID3v1Genres.getGenre(Integer.parseInt(trackGenreID));
            }

            // Populate Track object
            TrackMetadata trackMetadata = new TrackMetadata(
                    artistNameStr,
                    trackFileName,
                    trackContainerType,
                    trackTitle,
                    trackAlbum,
                    trackGenre,
                    duration,
                    trackPathStr,
                    playlist
            );

            trackMetadataObservableList.add(trackMetadata);

            System.out.println("Importing: " + trackFileName);
            System.out.println(tag);


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // For Recursive Initialization
    private void recursiveParse(ArrayList<String> tempArtistArray) {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(trackPathStr));
            Tag tag = audioFile.getTag();
            String trackArtist;
            String trackAlbum;
            String trackTitle = trackFileName;
            String trackGenre;
            final String duration = Utils.formatSeconds(audioFile.getAudioHeader().getTrackLength());
            final String playlist = "*";

            // Get Track Artist Name
            if (tag.getFirst(FieldKey.ALBUM_ARTIST) == null || Objects.equals(tag.getFirst(FieldKey.ALBUM_ARTIST), "")){
                if (tag.getFirst(FieldKey.ARTIST) == null || Objects.equals(tag.getFirst(FieldKey.ARTIST), "")) {
                    trackArtist = "Unknown";
                } else {
                    trackArtist = tag.getFirst(FieldKey.ARTIST);
                }
            } else {
                trackArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
            }

            // Check album metadata for null value, if true replace with directory name
            if (tag.getFirst(FieldKey.ALBUM) == null || Objects.equals(tag.getFirst(FieldKey.ALBUM), "")) {
                trackAlbum = "Unknown";
            } else {
                trackAlbum = tag.getFirst(FieldKey.ALBUM);
            }

            // Get Track Title
            // Check title metadata for null value, if true replace with file name substring
            if (tag.getFirst(FieldKey.TITLE) == null || Objects.equals(tag.getFirst(FieldKey.TITLE), "")) {
                trackTitle = trackFileName.substring(0, trackTitle.indexOf('.'));

                if (Character.isDigit(trackTitle.charAt(0))) {
                    trackTitle = filterDigitsFromTitle(trackTitle);
                }
            } else {
                trackTitle = tag.getFirst(FieldKey.TITLE);
            }

            // If still null replace with trackFileName (Redundancy)
            if (trackTitle == null) {
                trackTitle = trackFileName;
            }

            // Check genre metadata for null value, if true leave blank
            if (tag.getFirst(FieldKey.GENRE) == null) {
                trackGenre = null;

            } else {
                trackGenre = tag.getFirst(FieldKey.GENRE);
            }

            assert trackGenre != null;
            if (trackGenre.startsWith("(")) {
                String trackGenreID = trackGenre.substring(trackGenre.indexOf('(') + 1, trackGenre.indexOf(')'));
                trackGenre = ID3v1Genres.getGenre(Integer.parseInt(trackGenreID));
            }


            // Populate Track object
            TrackMetadata trackMetadata = new TrackMetadata(
                    trackArtist,
                    trackFileName,
                    trackContainerType,
                    trackTitle,
                    trackAlbum,
                    trackGenre,
                    duration,
                    trackPathStr,
                    playlist
            );

            trackMetadataObservableList.add(trackMetadata);

            if (!tempArtistArray.contains(trackArtist)) {
                tempArtistArray.add(trackArtist);
            }

            System.out.println("Importing: " + trackFileName);
            System.out.println(tag);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    // For Track/Album/Artist Importing
    private void importParse() {
        try {
            AudioFile audioFile = AudioFileIO.read(new File(trackPathStr));
            Tag tag = audioFile.getTag();
            String trackTitle = trackFileName;
            String trackGenre;
            final String duration = Utils.formatSeconds(audioFile.getAudioHeader().getTrackLength());
            final String playlist = "*";
            final String unknown = "Unknown";
            String trackArtist = unknown;
            String trackAlbum = unknown;

            // Check title metadata for null value, if true replace with file name substring
            if (tag.getFirst(FieldKey.TITLE) == null || Objects.equals(tag.getFirst(FieldKey.TITLE), "")) {
                trackTitle = trackFileName.substring(0, trackTitle.indexOf('.'));

                if (Character.isDigit(trackTitle.charAt(0))) {
                    trackTitle = filterDigitsFromTitle(trackTitle);
                }

            } else {
                trackTitle = tag.getFirst(FieldKey.TITLE);
            }

            // If still null replace with trackFileName (Redundancy)
            if (trackTitle == null) {
                trackTitle = trackFileName;
            }

            switch (importCategory) {
                case ARTIST -> {
                    trackArtist = artistNameStr;
                    trackAlbum = albumDirectoryStr;
                }

                case ALBUM -> {
                    if (tag.getFirst(FieldKey.ALBUM_ARTIST) == null || Objects.equals(tag.getFirst(FieldKey.ALBUM_ARTIST), "")){
                        if (tag.getFirst(FieldKey.ARTIST) == null || Objects.equals(tag.getFirst(FieldKey.ARTIST), "")) {
                            trackArtist = unknown;
                        } else {
                            trackArtist = tag.getFirst(FieldKey.ARTIST);
                        }
                    } else {
                        trackArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
                    }

                    trackAlbum = albumDirectoryStr;

                }

                case TRACK -> {
                    if (tag.getFirst(FieldKey.ARTIST) == null || Objects.equals(tag.getFirst(FieldKey.ARTIST), "")){
                        if (tag.getFirst(FieldKey.ALBUM_ARTIST) == null || Objects.equals(tag.getFirst(FieldKey.ALBUM_ARTIST), "")) {
                            trackArtist = unknown;
                        } else {
                            trackArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
                        }
                    } else {
                        trackArtist = tag.getFirst(FieldKey.ARTIST);
                    }

                    // Check Album metadata for null value, if true replace with Unknown
                    if (tag.getFirst(FieldKey.ALBUM) != null || !Objects.equals(tag.getFirst(FieldKey.ALBUM), "")) {
                        trackAlbum = tag.getFirst(FieldKey.ALBUM);
                    }
                }
            }

            // Check genre metadata for null value, if true leave blank
            if (tag.getFirst(FieldKey.GENRE) == null) {
                trackGenre = null;

            } else {
                trackGenre = tag.getFirst(FieldKey.GENRE);
            }

            assert trackGenre != null;
            if (trackGenre.startsWith("(")) {
                String trackGenreID = trackGenre.substring(trackGenre.indexOf('(') + 1, trackGenre.indexOf(')'));
                trackGenre = ID3v1Genres.getGenre(Integer.parseInt(trackGenreID));
            }


            // Populate Track object
            TrackMetadata trackMetadata = new TrackMetadata(
                    trackArtist,
                    trackFileName,
                    trackContainerType,
                    trackTitle,
                    trackAlbum,
                    trackGenre,
                    duration,
                    trackPathStr,
                    playlist
            );

            trackMetadataObservableList.add(trackMetadata);

            System.out.println("Importing: " + trackFileName);
            System.out.println(tag);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          STRING PROCESSING
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private String filterDigitsFromTitle(String trackTitle) {
        if (trackTitle.contains(".")) {
            if (trackTitle.contains(" - ")) {
                trackTitle = trackTitle.substring(trackTitle.indexOf('-') + 2,  trackTitle.lastIndexOf('.'));

            } else {
                trackTitle = trackTitle.substring(trackTitle.indexOf(' ') + 1, trackTitle.lastIndexOf('.'));
            }

        } else {
            if (trackTitle.contains(" - ")) {
                trackTitle = trackTitle.substring(trackTitle.indexOf('-') + 2);
            } else {
                trackTitle = trackTitle.substring(trackTitle.indexOf(' ') + 1);
            }

        }

        return trackTitle;
    }

    private String removeSlashes(String string) {
        String updatedString = string;

        if (string.contains("/") ) {
            updatedString = string.replace('/', '_');
        }

        if (string.contentEquals("\\")) {
            updatedString = string.replace('\\', '_');
        }

        return updatedString;
    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          SETTERS / GETTERS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void setRootMusicDirectoryString(String rootMusicDirectoryString) {
        this.rootMusicDirectoryString = rootMusicDirectoryString;
    }

    public ObservableList<TrackMetadata> getTrackObservableList() {
        return trackMetadataObservableList;
    }
    public ObservableList<String> getArtistNameObservableList() {
        return artistNameObservableList;
    }

    public TrackMetadata getImportedTrack() {
        // Get Track, clear list
        TrackMetadata trackMetadata = trackMetadataObservableList.get(0);
        trackMetadataObservableList.clear();

        return trackMetadata;
    }

    public String getArtistNameStr() { return artistNameStr; }

}