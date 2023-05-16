/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: SearchTableView.java
 *      Notes: Contains search logic for List View and Text Field objects.
 */

package com.iandw.musicplayerjavafx.Utilities;

import com.iandw.musicplayerjavafx.TrackMetadata;

import java.util.function.Predicate;

public class SearchTableView {
    private String artistNameString;
    private String playlistTitleString;

    public SearchTableView() {}

    /**
     * createArtistsListPredicate() - handles trackTableView search for ArtistListView
     * @param artistNameString => Search parameter
     * @return => Boolean, if search parameter is in the Table View Observable List
     */
    public Predicate<TrackMetadata> createArtistListPredicate(String artistNameString) {
        this.artistNameString = artistNameString;

        return this::artistsListTrackSearch;
    }

    /**
     * createPlaylistsListPredicate() - handles trackTableView search for PlaylistListView
     * @param playlistTitleString => Search parameter
     * @return => Boolean, if search parameter is in the Table View Observable List
     */
    public Predicate<TrackMetadata> createPlaylistListPredicate(String playlistTitleString) {
        this.playlistTitleString = playlistTitleString;

        return this::playlistsListTrackSearch;
    }

    public Predicate<TrackMetadata> createSearchPredicate(String searchText) {
        return track -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            return searchMetadata(track, searchText);
        };
    }

    private boolean artistsListTrackSearch(TrackMetadata trackMetadata) {
        if (trackMetadata.getArtistNameStr().contains(artistNameString)) {
            return trackMetadata.getArtistNameStr().contains(artistNameString);
        }

        return false;
    }

    private boolean playlistsListTrackSearch(TrackMetadata trackMetadata) {
        if (trackMetadata.getPlaylistStr().contains(playlistTitleString)) {
            return trackMetadata.getPlaylistStr().contains(playlistTitleString);
        }

        return false;
    }

    // Check trackMetadata objects for searchText matches
    private boolean searchMetadata(TrackMetadata trackMetadata, String searchText) {
        return (trackMetadata.getTrackTitleStr().toLowerCase().contains(searchText.toLowerCase()) ||
                trackMetadata.getAlbumTitleStr().toLowerCase().contains(searchText.toLowerCase()) ||
                trackMetadata.getArtistNameStr().toLowerCase().contains(searchText.toLowerCase()) ||
                trackMetadata.getTrackGenreStr().toLowerCase().contains(searchText.toLowerCase()) ||
                trackMetadata.getPlaylistStr().toLowerCase().contains(searchText.toLowerCase())
        );
    }
}
