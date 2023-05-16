/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: MusicPlayerController.java
 *      Notes: Main app controller - handles MusicPlayer, TableView, and ListView GUI objects
 *              Media playback, and Menu Items.
 */

package com.iandw.musicplayerjavafx;

import com.iandw.musicplayerjavafx.ContextMenus.ArtistListContextMenu;
import com.iandw.musicplayerjavafx.ContextMenus.PlaylistContextMenu;
import com.iandw.musicplayerjavafx.ContextMenus.TableViewContextMenu;
import com.iandw.musicplayerjavafx.FileIO.ConsoleLogFileIO;
import com.iandw.musicplayerjavafx.FileIO.SettingsFileIO;
import com.iandw.musicplayerjavafx.Libraries.ListViewLibrary;
import com.iandw.musicplayerjavafx.Libraries.MusicLibrary;
import com.iandw.musicplayerjavafx.Libraries.TableViewLibrary;
import com.iandw.musicplayerjavafx.Utilities.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

public class MusicPlayerController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ListView<String> artistListView;
    @FXML
    private ListView<String> playlistListView;
    @FXML
    private TableView<TrackMetadata> trackTableView;
    @FXML
    private TableColumn<TrackMetadata, String> colTrackTitle;
    @FXML
    public TableColumn<TrackMetadata, String> colAlbumTitle;
    @FXML
    public TableColumn<TrackMetadata, String> colTrackLength;
    @FXML
    public TableColumn<TrackMetadata, String> colTrackGenre;
    @FXML
    public TableColumn<TrackMetadata, String> colTrackFileNameInvisible;
    @FXML
    private TableColumn<TrackMetadata, String> colArtistNameInvisible;
    @FXML
    private TableColumn<TrackMetadata, String> colPlaylistInvisible;
    @FXML
    private TextField searchField;
    @FXML
    private Button playPauseButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button previousButton;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider seekSlider;
    @FXML
    private Label playingLabel;
    @FXML
    private Label albumLabel;
    @FXML
    private Label byLabel;
    @FXML
    private Label trackCurrentTimeLabel;
    @FXML
    private Label trackDurationLabel;
    @FXML
    private Label volumeLevelLabel;
    @FXML
    private Label volumeIconLabel;
    @FXML
    private RadioButton autoButton;
    @FXML
    private RadioButton shuffleButton;
    @FXML
    private RadioButton repeatButton;
    @FXML
    private CheckBox mute;
    @FXML
    private FontIcon playIcon;
    @FXML
    private FontIcon pauseIcon;
    @FXML
    private FontIcon volumeUp;
    @FXML
    private FontIcon volumeDown;
    @FXML
    private FontIcon volumeOff;
    @FXML
    private FontIcon volumeMute;
    @FXML
    private FontIcon albumIcon;
    @FXML
    private FontIcon artistIcon;
    @FXML
    private ImageView imageView;
    @FXML
    private MenuItem settingsMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private MenuItem importArtistMenuItem;
    @FXML
    private MenuItem importAlbumMenuItem;
    @FXML
    private MenuItem importTrackMenuItem;
    @FXML
    private MenuItem addArtistMenuItem;
    @FXML
    private MenuItem createPlaylistMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private MenuItem gitHubMenuItem;
    @FXML
    private MenuItem consoleLogMenuItem;
    @FXML
    private MenuItem reportBugMenuItem;

    private Image defaultAlbumImage;
    private ImageFileLogic imageFileLogic;
    private MediaPlayer mediaPlayer;
    private MusicLibrary musicLibrary;
    private final TableViewLibrary tableViewLibrary;
    private final ListViewLibrary listViewLibrary;
    private SearchTableView searchTableView;
    private AutoPlay autoPlay;
    private TrackIndex trackIndex;
    private final UserSettings userSettings;
    private final ExecutorService executorService;
    private final ByteArrayOutputStream consoleOutput;
    private final Stage stage;
    private String artistNameString;
    private String playlistTitleString;
    private String previousArtistNameString;
    private String currentTheme;
    private double volumeDouble;
    private boolean playing;
    private boolean stopped;
    private boolean artistsListSelected;
    private int albumImageWidth;

    /**
     * MusicPlayerController.java => constructor
     *
     * @param stage => Close the Application from the File Menu
     * @param executorService => Call AwaitTermination() to wait for file input threads to end
     * @param consoleOutput => Pass ByteArrayOS object to update buffer from System.out... statements
     * @param userSettings => Allows changes to UserSettings Object after initialization
     * @param listViewLibrary => Initialize App with Artist and Playlist data
     * @param tableViewLibrary => Initialize App with Track Metadata
     */
    public MusicPlayerController(Stage stage, ExecutorService executorService, ByteArrayOutputStream consoleOutput,
                                 UserSettings userSettings, ListViewLibrary listViewLibrary, TableViewLibrary tableViewLibrary)
    {
        this.stage = stage;
        this.executorService = executorService;
        this.consoleOutput = consoleOutput;
        this.userSettings = userSettings;
        this.listViewLibrary = listViewLibrary;
        this.tableViewLibrary = tableViewLibrary;
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          INITIALIZE GUI, LIST, & TABLE VIEWS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void initialize() throws IOException, InterruptedException {
        // Initialize variables
        playing = false;
        stopped = true;
        artistsListSelected = true;
        searchTableView = new SearchTableView();
        currentTheme = userSettings.getThemeFileNameString();
        imageFileLogic = new ImageFileLogic(currentTheme);
        playPauseButton.setGraphic(playIcon);
        volumeIconLabel.setGraphic(volumeUp);
        albumIcon.setOpacity(0);
        artistIcon.setOpacity(0);
        albumImageWidth = 55;
        volumeDouble = .25;
        autoPlay = AutoPlay.OFF;
        trackIndex = new TrackIndex();
        artistNameString = "";
        playlistTitleString = "";

        // Set TableView column widths
        colTrackTitle.setMaxWidth( 1f * Integer.MAX_VALUE * 40 ); // 40% width
        colAlbumTitle.setMaxWidth( 1f * Integer.MAX_VALUE * 40 );
        colTrackLength.setMaxWidth( 1f * Integer.MAX_VALUE * 6 );
        colTrackGenre.setMaxWidth( 1f * Integer.MAX_VALUE * 14 );

        // Autoplay Icon (all other icons are from bootstrapicons -> musiclibrary.fxml)
        ImageView autoPlayIcon = new ImageView(ResourceURLs.getAutoplayiconURL());

        // Change color to match theme
        autoPlayIcon.setEffect(imageFileLogic.getLighting());
        autoButton.setGraphic(autoPlayIcon);
        autoButton.getGraphic().setTranslateX(2.0);

        // Album Art default graphic
        defaultAlbumImage = new Image(imageFileLogic.getAlbumImage());
        imageView.setImage(defaultAlbumImage);
        imageView.setOpacity(.9);
        imageView.setCache(true);
        imageView.setVisible(true);

        // Initialize vol slider fill
        String initStyle = String.format(SliderFillColor.getStyle(currentTheme), (Math.sqrt(volumeDouble) * 100));
        volumeSlider.setStyle(initStyle);

        // Initialize main app objects for Music Library, ListView, and TableView
        musicLibrary = new MusicLibrary(userSettings);

        // Send user to Settings to initialize Music Library if tracklist.ser is empty
        if (Files.size(Paths.get(ResourceURLs.getTrackListURL())) == 0) {
            // Choose Root Directory for Music Library
            String directoryLabel = "Welcome, press 'Music Folder' to initialize.";

            SettingsController settingsController = new SettingsController();
            settingsController.showSettingsWindow(artistListView, playlistListView, trackTableView, listViewLibrary,
                    tableViewLibrary, musicLibrary, userSettings, directoryLabel);

            listViewLibrary.setOutputListsOnClose();
            tableViewLibrary.setOutputTrackListOnClose();

        // Else set List View objects with String data from .ser files
        } else {
            // Playlist and Artist List Data => artistPlaylistListView
            artistListView.setItems(listViewLibrary.getArtistObservableList());
            playlistListView.setItems(listViewLibrary.getPlaylistObservableList());

            // Track Metadata => trackTableView
            trackTableView.setItems(tableViewLibrary.getTrackObservableList());

            // Initialize table view after files are read in via executorService
            // Wait for maximum of 10 seconds (for reference, 200gb music files should take ~4 seconds)
            if (executorService.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
                artistListView.getSelectionModel().select(0);
                trackTableView.refresh();
                listViewSelected();
            }
        }

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         *                        KEY BINDINGS
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        // Menu Items
        importArtistMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));
        importAlbumMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
        importTrackMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        settingsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        exitMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        addArtistMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        createPlaylistMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
        aboutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        gitHubMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
        consoleLogMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        reportBugMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));

        // Buttons and Vol. Slider
        // Play/Pause
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE) {
                playPauseButton();
                keyEvent.consume();
            }
        });

        // Stop Track
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SLASH) {
                stopButton();
                keyEvent.consume();
            }
        });

        // Next Track
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.PERIOD && mediaPlayer != null) {
                nextButton();
                keyEvent.consume();
            }
        });

        // Previous Track
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.COMMA && mediaPlayer != null &&
                    trackTableView.getSelectionModel().getSelectedItem() != null) {
                previousButton();
                keyEvent.consume();
            }
        });

        // Auto Play
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DIGIT1) {
                autoButton.selectedProperty().set(!autoButton.selectedProperty().getValue());
                keyEvent.consume();
            }
        });

        // Shuffle
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DIGIT2) {
                shuffleButton.selectedProperty().set(!shuffleButton.selectedProperty().getValue());
                keyEvent.consume();
            }
        });

        // Repeat
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DIGIT3) {
                repeatButton.selectedProperty().set(!repeatButton.selectedProperty().getValue());
                keyEvent.consume();
            }
        });

        // Volume Up
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.EQUALS) {
                volumeSlider.setValue(volumeSlider.getValue() + 1);
                keyEvent.consume();
            }
        });

        // Volume Down
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.MINUS) {
                volumeSlider.setValue(volumeSlider.getValue() - 1);
                keyEvent.consume();
            }
        });

        // Mute
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DIGIT0) {
                mute.selectedProperty().set(!mute.selectedProperty().getValue());
                keyEvent.consume();
            }
        });

        // ArtistList Context Menu
        artistListView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick -> {
            if (mouseClick.getButton() == MouseButton.SECONDARY) {
                handleArtistListViewContextMenu();
            }
        });

        // Playlists Context Menu
        playlistListView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick -> {
            if (mouseClick.getButton() == MouseButton.SECONDARY) {
                handlePlaylistsListViewContextMenu();
            }
        });

        // TrackTableView Context Menu
        trackTableView.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClick -> {
            if (mouseClick.getButton() == MouseButton.SECONDARY) {
                handleTableViewContextMenu();
            }
        });

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *
         *                        GUI LISTENERS
         *
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        // listener for changes to volumeSlider's value
        volumeSlider.valueProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    int volumeInt = newValue.intValue();
                    volumeLevelLabel.setText(Integer.toString(volumeInt));

                    try {
                        volumeDouble = Math.pow(newValue.doubleValue(), 2) / 10000;
                        mediaPlayer.setVolume(volumeDouble);

                    } catch (NullPointerException e) {
                        System.out.println("mediaPlayer is null");
                    }

                    if (volumeSlider.getValue() >= 50) {
                        volumeIconLabel.setGraphic(volumeUp);

                    } else if (volumeSlider.getValue() > 0) {
                        volumeIconLabel.setGraphic(volumeDown);

                    } else {
                        volumeIconLabel.setGraphic(volumeOff);
                    }

                    // Slider css fill
                    double percentage = 100.0 * newValue.doubleValue() / volumeSlider.getMax();

                    if (Double.isNaN(percentage)) { percentage = 0.0; }

                    // Set slideSeeker css based on current style sheet
                    String style = String.format(SliderFillColor.getStyle(currentTheme), percentage);

                    //System.out.println(percentage);
                    volumeSlider.setStyle(style);
                }
        );

        // Mute checkbox
        mute.selectedProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.setMute(newValue);

                        if (mute.isSelected()) {
                            volumeIconLabel.setGraphic(volumeMute);

                        } else {
                            if (volumeSlider.getValue() >= 50) {
                                volumeIconLabel.setGraphic(volumeUp);

                            } else if (volumeSlider.getValue() > 0) {
                                volumeIconLabel.setGraphic(volumeDown);

                            } else {
                                volumeIconLabel.setGraphic(volumeOff);
                            }
                        }
                    }

                }
        );

        // Seek time during track duration, and updating current duration on seekSlider
        seekSlider.valueProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (seekSlider.isPressed() && !stopped) {
                        mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(seekSlider.getValue() / 100));
                    }

                    // Slider CSS fill
                    double percentage = 100.0 * newValue.doubleValue() / seekSlider.getMax();

                    if (Double.isNaN(percentage)) { percentage = 0.0; }

                    // Set slideSeeker css based on current style sheet
                    String style = String.format(SliderFillColor.getStyle(currentTheme), percentage);

                    //System.out.println(percentage);
                    seekSlider.setStyle(style);
                }
        );

        // Toggle Logic
        autoButton.selectedProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        autoPlay = AutoPlay.AUTO_PLAY;

                        if (shuffleButton.isSelected() || repeatButton.isSelected()) {
                            radioButtonActive();

                        } else {
                            radioButtonOff();
                        }

                    } else {
                        autoPlay = AutoPlay.OFF;
                        deselectRadioButton();
                        trackIndex.getShuffleArray().clear();
                    }
                }
        );

        shuffleButton.selectedProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        autoPlay = AutoPlay.SHUFFLE;

                        if (autoButton.isSelected() || repeatButton.isSelected()) {
                            radioButtonActive();

                        } else {
                            radioButtonOff();
                        }

                    } else {
                        autoPlay = AutoPlay.OFF;
                        deselectRadioButton();

                    }
                }
        );

        repeatButton.selectedProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        autoPlay = AutoPlay.REPEAT;

                        if (shuffleButton.isSelected() || autoButton.isSelected()) {
                            radioButtonActive();

                        } else {
                            radioButtonOff();
                        }

                    } else {
                        autoPlay = AutoPlay.OFF;
                        deselectRadioButton();
                    }
                }
        );

        // SearchField Listener
        searchField.textProperty().addListener(
                ((observableValue, oldValue, newValue) -> {
                    tableViewLibrary.getFilteredList().setPredicate(searchTableView.createSearchPredicate(newValue));
                    trackIndex.setTableSize(tableViewLibrary.getFilteredList().size());
                    trackIndex.clearShuffleArray();
                    trackIndex.clearPreviousIndexStack();
                })
        );


    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          TABLE VIEW
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML
    private void handleTableViewMouseClick(MouseEvent mouseClick) {
        if (mouseClick.getButton().equals(MouseButton.PRIMARY) && mouseClick.getClickCount() == 2) {
            if (playing) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                playing = false;
            }

            // Push any tracks selected by mouse to prev index stack.
            trackIndex.setPushCurrentTrackToStack(true);

            // Load currentPath and associated variables
            playMedia();
        }
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          LIST VIEW
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML
    private void handleArtistsListViewMouseClick(MouseEvent mouseClick) {
        if (mouseClick.getButton().equals(MouseButton.PRIMARY) && artistListView != null &&
                artistListView.getSelectionModel().getSelectedItem() != null)
        {
            playlistListView.getSelectionModel().clearSelection();
            artistListView.requestFocus();
            artistsListSelected = true;
            artistNameString = artistListView.getSelectionModel().getSelectedItem();
            listViewSelected();
        }
    }

    @FXML
    private void handlePlaylistsListViewClick(MouseEvent mouseClick) {
        if (mouseClick.getButton().equals(MouseButton.PRIMARY) && playlistListView != null &&
                playlistListView.getSelectionModel().getSelectedItem() != null)
        {
            artistListView.getSelectionModel().clearSelection();
            playlistListView.requestFocus();
            artistsListSelected = false;
            playlistTitleString = playlistListView.getSelectionModel().getSelectedItem();
            listViewSelected();
        }
    }

    private void listViewSelected() {
        // Get selected artist name (from directory name)
        previousArtistNameString = artistNameString;

        // Create a filtered list for trackTableView
        tableViewLibrary.createFilteredList();

        // Check artistsObservableList for artist name, call artist list predicate if true.
        // Else call the playlistListView predicate
        if (artistsListSelected && artistNameString != null) {
            tableViewLibrary.getFilteredList().setPredicate(searchTableView.createArtistListPredicate(artistNameString));

        } else if (playlistTitleString != null) {
            // Remove null pointer exceptions from predicate search
            tableViewLibrary.getFilteredList().setPredicate(searchTableView.createPlaylistListPredicate(playlistTitleString));
        }

        trackTableView.setItems(tableViewLibrary.getFilteredList());
        trackTableView.setVisible(true);
        trackIndex.setTableSize(tableViewLibrary.getFilteredList().size());

        // Sort Table
        trackTableView.getSortOrder().add(colTrackFileNameInvisible);

        // Populate trackTableView with track object data
        colArtistNameInvisible.setCellValueFactory(new PropertyValueFactory<>("artistNameStr"));
        colTrackFileNameInvisible.setCellValueFactory(new PropertyValueFactory<>("trackFileNameStr"));
        colTrackTitle.setCellValueFactory(new PropertyValueFactory<>("trackTitleStr"));
        colAlbumTitle.setCellValueFactory(new PropertyValueFactory<>("albumTitleStr"));
        colTrackLength.setCellValueFactory(new PropertyValueFactory<>("trackDurationStr"));
        colTrackGenre.setCellValueFactory(new PropertyValueFactory<>("trackGenreStr"));
        colPlaylistInvisible.setCellValueFactory(new PropertyValueFactory<>("playlistStr"));
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                          CONTEXT MENUS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void handleArtistListViewContextMenu() {

        artistNameString = artistListView.getSelectionModel().getSelectedItem();

        try {
            if (artistNameString != null) {
                // Right-clicking will update the tableview based on selection
                trackTableView.refresh();
                listViewSelected();

                ArtistListContextMenu.getContextMenu(artistListView, playlistListView, trackTableView,
                        listViewLibrary, tableViewLibrary, trackIndex, userSettings);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void handlePlaylistsListViewContextMenu() {

        playlistTitleString = playlistListView.getSelectionModel().getSelectedItem();

        // Right-clicking will update the tableview based on selection
        try {
            if (playlistTitleString != null) {
                trackTableView.refresh();
                listViewSelected();

                PlaylistContextMenu.getContextMenu(artistListView, playlistListView, trackTableView,
                        listViewLibrary, tableViewLibrary, trackIndex);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void handleTableViewContextMenu()  {
        TableViewContextMenu.getContextMenu(artistListView, playlistListView, trackTableView,
                listViewLibrary, tableViewLibrary, trackIndex);

        // If artist or playlist name is edited, keeps tableview from selecting nothing
        if (!Objects.equals(previousArtistNameString, artistNameString)) {
            // Refresh TableView
            listViewSelected();
        }

    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                         SEARCH BAR
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @FXML
    private void handleClearSearchText(MouseEvent mouseClick) {
        searchField.setText("");
        listViewSelected();
        mouseClick.consume();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                 PLAY/PAUSE/PREVIOUS/NEXT/STOP BUTTONS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    // Toggle media playback and the icon on the playPauseButton
    @FXML
    private void playPauseButtonPressed(MouseEvent mouseClick) {
        if (mouseClick.getButton().equals(MouseButton.PRIMARY)) {
            playPauseButton();
        }
    }

    private void playPauseButton() {
        trackIndex.setPushCurrentTrackToStack(true);

        try {
            // Pause currently playing track
            if (playing) {
                mediaPlayer.pause();
                playPauseButton.setGraphic(playIcon);
                playing = false;

            // Play from selected track if stopped or null
            } else if (mediaPlayer == null || stopped) {
                playMedia();

            // Play from currently paused track
            } else {
                mediaPlayer.play();
                playPauseButton.setGraphic(pauseIcon);
                playing = true;
                stopped = false;
                setNowPlayingText();
            }

        } catch (NullPointerException e) {
            System.out.println("No track selected.");
        }
    }

    @FXML
    private void seekSliderPressed(MouseEvent mouseClick) {
        // Allow clicking on the seekSlider to play the selected track
        if (mouseClick.getButton().equals(MouseButton.PRIMARY) && !stopped) {
            try {
                if (!playing) {
                    mediaPlayer.play();
                    playing = true;
                    stopped = false;
                    playPauseButton.setGraphic(pauseIcon);
                }

            } catch (NullPointerException e) {
                System.out.println("No track selected.");
            }
        }
    }

    @FXML
    private void stopButtonPressed(MouseEvent mouseClick) {
        if (mouseClick.getButton().equals(MouseButton.PRIMARY)) {
            stopButton();
        }
    }

    private void stopButton() {
        try {
            if (playing && (Objects.equals(artistNameString, artistListView.getSelectionModel().getSelectedItem()))) {
                stopMedia(false);

            } else {
                stopMedia(playing);
            }

        } catch (NullPointerException e) {
            System.out.println("No track selected.");
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void nextButtonPressed(MouseEvent mouseClick) {
        if (mouseClick.getButton().equals(MouseButton.PRIMARY) && mediaPlayer != null) {
            nextButton();
        }
    }

    private void nextButton() {
        trackIndex.setPushCurrentTrackToStack(true);
        if (shuffleButton.isSelected()) {
            shuffleSelected();

        } else if (trackTableView.getSelectionModel().getSelectedItem() != null) {
            trackTableView.getSelectionModel().select(trackIndex.getNextTrackIndex());
            stopMedia(true);
            playMedia();
        }
    }

    @FXML
    private void previousButtonPressed(MouseEvent mouseClick) {
        if (mouseClick.getButton().equals(MouseButton.PRIMARY) && mediaPlayer != null &&
            trackTableView.getSelectionModel().getSelectedItem() != null)
        {
            previousButton();
        }
    }

    private void previousButton() {
        trackIndex.setPushCurrentTrackToStack(false);
        trackTableView.scrollTo(trackIndex.peekPreviousIndexArray());

        if (!trackIndex.getPreviousIndexStack().empty()) {
            trackTableView.getSelectionModel().select(trackIndex.popPreviousIndexArray());
        }

        stopMedia(true);
        playMedia();
    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                         PLAY/PAUSE/STOP MEDIA PLAYER LOGIC
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void playMedia() {

        // Get the filepath of the currently selected track
        final String currentPath = trackTableView.getSelectionModel().getSelectedItem().getTrackPathStr();
        System.out.printf("currentPath: %s%n", currentPath);

        // Create Media Object for audio file playback
        Media audioFile = new Media(new File(currentPath).toURI().toString());
        mediaPlayer = new MediaPlayer(audioFile);

        // Track current track index for prev, next and autoplay functions
        trackIndexTracker();

        // Set Seeker slider
        mediaPlayer.currentTimeProperty().addListener(observable -> seekValueUpdate());

        mediaPlayer.setVolume(volumeDouble);

        // Play media
        mediaPlayer.setOnReady(() -> {
            mediaPlayer.play();

            if (mediaPlayer.getMedia().getMetadata().get("image") == null) {
                imageView.setImage(defaultAlbumImage);

            } else {
                imageView.setImage((Image) mediaPlayer.getMedia().getMetadata().get("image"));
            }

            imageView.setPreserveRatio(true);
            imageView.setFitWidth(albumImageWidth);
            playPauseButton.setGraphic(pauseIcon);
            playing = true;
            stopped = false;

            // Set text to currently playing text fields
            setNowPlayingText();
        });

        // Auto select or stop media player after current track is finished
        mediaPlayer.setOnEndOfMedia(() -> {
            if (trackTableView.getSelectionModel().getSelectedItem() != null) {
                System.out.println(autoPlay);

                if (autoButton.isSelected()) {
                    autoPlaySelected();

                } else if (repeatButton.isSelected()) {
                    repeatSelected();

                } else if (shuffleButton.isSelected()) {
                    shuffleSelected();

                } else {
                    stopMedia(false);
                }

            } else {
                stopMedia(true);
            }
        });
    }

    private void stopMedia(boolean dispose) {
        mediaPlayer.stop();

        if (dispose) {
            mediaPlayer.dispose();
        }

        playPauseButton.setGraphic(playIcon);
        playing = false;
        stopped = true;
        setNowPlayingText();
    }

    // Updates the time values of the seekSlider
    private void seekValueUpdate() {
        if (mediaPlayer == null) {
            trackDurationLabel.setText("");
            trackCurrentTimeLabel.setText("");
        }

        trackDurationLabel.setText(Utils.formatSeconds(
                (int) mediaPlayer.getTotalDuration().toSeconds() - (int) mediaPlayer.getCurrentTime().toSeconds()));
        trackCurrentTimeLabel.setText(Utils.formatSeconds((int) mediaPlayer.getCurrentTime().toSeconds()));
        seekSlider.valueProperty().setValue(mediaPlayer.getCurrentTime().toMillis() /
                mediaPlayer.getTotalDuration().toMillis() * 100);
    }

    // Handles icons and label text for top left corner of the Application
    private void setNowPlayingText() {
        if (stopped) {
            playingLabel.setText("-");
            albumLabel.setText("");
            byLabel.setText("");
            albumIcon.setOpacity(0);
            artistIcon.setOpacity(0);

        } else if (trackTableView.getSelectionModel().getSelectedIndex() != trackIndex.getCurrentTrackIndex()) {
            albumIcon.setOpacity(100);
            artistIcon.setOpacity(100);
            playingLabel.setText(" " + trackTableView.getItems().get(trackIndex.getCurrentTrackIndex()).getTrackTitleStr());
            albumLabel.setText(" " + trackTableView.getItems().get(trackIndex.getCurrentTrackIndex()).getAlbumTitleStr());
            byLabel.setText(" " + trackTableView.getItems().get(trackIndex.getCurrentTrackIndex()).getArtistNameStr());

        } else {
            albumIcon.setOpacity(100);
            artistIcon.setOpacity(100);
            playingLabel.setText(" " + trackTableView.getSelectionModel().getSelectedItem().getTrackTitleStr());
            albumLabel.setText(" " + trackTableView.getSelectionModel().getSelectedItem().getAlbumTitleStr());
            byLabel.setText(" " + trackTableView.getSelectionModel().getSelectedItem().getArtistNameStr());
        }
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                         TRACK AUTOPLAY/SHUFFLE/REPEAT LOGIC
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private void radioButtonActive() {
        switch (autoPlay) {
            case AUTO_PLAY -> {
                shuffleButton.setSelected(false);
                repeatButton.setSelected(false);
                autoButton.setSelected(true);
            }
            case SHUFFLE -> {
                shuffleButton.setSelected(true);
                repeatButton.setSelected(false);
                autoButton.setSelected(false);
            }
            case REPEAT -> {
                shuffleButton.setSelected(false);
                repeatButton.setSelected(true);
                autoButton.setSelected(false);
            }
            case OFF -> {
                shuffleButton.setSelected(false);
                repeatButton.setSelected(false);
                autoButton.setSelected(false);
            }
        }
    }

    // Allow user to de-select Radio Buttons
    private void radioButtonOff() {
        switch (autoPlay) {
            case AUTO_PLAY  -> autoButton.setSelected(true);
            case SHUFFLE    -> shuffleButton.setSelected(true);
            case REPEAT     -> repeatButton.setSelected(true);
        }
    }

    private void deselectRadioButton() {
        switch (autoPlay) {
            case AUTO_PLAY  -> autoButton.setSelected(false);
            case SHUFFLE    -> shuffleButton.setSelected(false);
            case REPEAT     -> repeatButton.setSelected(false);
        }
    }

    // Handles row index navigation of trackTableView
    private void trackIndexTracker() {
        // If prev button is continually pressed, do not add current track to stack.
        // Will add current track to stack when true, while next, auto, user selection, or shuffle are selected.
        if (trackIndex.getPushCurrentTrackToStack()) {
            trackIndex.pushToPreviousIndexArray(trackIndex.getCurrentTrackIndex());
        }

        trackIndex.setCurrentTrackIndex(trackTableView.getItems().indexOf(
                trackTableView.getSelectionModel().getSelectedItem()));

        // If last track set next track to beginning (0).
        if (trackIndex.getCurrentTrackIndex() == trackIndex.getTableSize() - 1) {
            trackIndex.setNextTrackIndex(0);

        } else {
            trackIndex.setNextTrackIndex(trackIndex.getCurrentTrackIndex() + 1);
        }
    }

    // When selected automatically goes to next track in trackTableView
    private void autoPlaySelected() {
        trackIndex.setPushCurrentTrackToStack(true);
        trackTableView.getSelectionModel().select(trackIndex.getNextTrackIndex());
        trackTableView.scrollTo(trackIndex.getNextTrackIndex());
        stopMedia(true);
        playMedia();
    }

    // When selected chooses a random track currently visible in trackTableView
    private void shuffleSelected() {
        int tableSize = trackIndex.getTableSize();
        trackIndex.setPushCurrentTrackToStack(true);

        if (trackIndex.getShuffleArray() == null || trackIndex.getShuffleArray().isEmpty()) {
            assert false;
            trackIndex.addToShuffleArray(trackIndex.getCurrentTrackIndex());
        }

        if (!Objects.equals(previousArtistNameString, artistNameString)) {
            trackIndex.clearShuffleArray();
            previousArtistNameString = artistNameString;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(0, tableSize);

        if (!trackIndex.getShuffleArray().contains(randomIndex)) { // If index is not present play next
            trackIndex.addToShuffleArray(randomIndex);

        } else if (trackIndex.getShuffleArray().size() >= tableSize) { // reset table if array is larger than table size
            trackIndex.clearShuffleArray();
            trackIndex.addToShuffleArray(randomIndex);

        } else {
            while (trackIndex.getShuffleArray().contains(randomIndex)) { // if index present, find new index
                randomIndex = random.nextInt(0, tableSize);

                if (trackIndex.getShuffleArray().size() >= tableSize) { // while loop fail-safe
                    trackIndex.clearShuffleArray();
                    break;
                }
            }
            trackIndex.getShuffleArray().add(randomIndex);
        }

        trackTableView.getSelectionModel().select(randomIndex);
        trackTableView.scrollTo(randomIndex);
        stopMedia(true);
        playMedia();
    }

    // When selected repeats current track
    private void repeatSelected() {
        trackIndex.setPushCurrentTrackToStack(false);
        trackTableView.getSelectionModel().select(trackIndex.getCurrentTrackIndex());
        stopMedia(true);
        playMedia();
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                         FILE MENUS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML
    private void importArtistClicked() {
        // Select Artist
        DirectoryChooser artistChooser = new DirectoryChooser();
        artistChooser.setTitle("Select Artist Folder");
        artistChooser.setInitialDirectory((new File(".")));

        // Set Stage, show artistChooser Dialog
        Stage stage = new Stage();
        File file = artistChooser.showDialog(stage);

        // Execute import on new Thread for task interruptions/succeed/fail feedback
        // and to get import off of Application thread
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    musicLibrary.importArtist(file);

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedIOException();
                    }

                    Platform.runLater(() -> {
                        tableViewLibrary.setTrackObservableList(musicLibrary.getTrackObservableList());
                        addArtistFromImport();
                    });

                    System.out.printf("Finished importing %s.%n", file.toString());

                } catch (InterruptedIOException consumed) {
                    System.out.println("Import failed.");
                }

                return null;
            }
        };

        task.setOnSucceeded(evt -> {
            System.out.println("Import succeeded.");
            clickArtistNameAfterImport();
        });

        task.setOnFailed(evt -> {
            System.out.println("Import Failed.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Initialization Failed");
            alert.setContentText("Invalid file type or folder hierarchy.\nCheck console log for details.");
            alert.showAndWait();
        });

        // Start initializeMusicLibrary() thread
        Thread thread = new Thread(task);
        thread.start();

        // Write to File on close
        Platform.runLater(tableViewLibrary::setOutputTrackListOnClose);
    }

    @FXML
    private void importAlbumClicked() {
        // Select album folder
        DirectoryChooser albumChooser = new DirectoryChooser();
        albumChooser.setTitle("Select Album Folder");
        albumChooser.setInitialDirectory((new File(".")));

        // Set Stage, show albumChooser Dialog
        Stage stage = new Stage();
        File file = albumChooser.showDialog(stage);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    musicLibrary.importAlbum(file);

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedIOException();
                    }

                    Platform.runLater(() -> {
                        tableViewLibrary.setTrackObservableList(musicLibrary.getTrackObservableList());
                        addArtistFromImport();
                    });

                    System.out.printf("Finished importing %s.%n", file.toString());

                } catch (InterruptedIOException consumed) {
                    System.out.println("Import failed.");
                }

                return null;
            }
        };

        task.setOnSucceeded(evt -> {
            System.out.println("Import succeeded.");
            clickArtistNameAfterImport();
        });

        task.setOnFailed(evt -> {
            System.out.println("Import Failed.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Initialization Failed");
            alert.setContentText("Invalid file type or folder hierarchy.\nCheck console log for details.");
            alert.showAndWait();
        });

        // Start initializeMusicLibrary() thread
        Thread thread = new Thread(task);
        thread.start();

        // Write to File on close
        Platform.runLater(tableViewLibrary::setOutputTrackListOnClose);
    }

    @FXML
    private void importTrackClicked() {
        // Select track file
        FileChooser trackChooser = new FileChooser();
        trackChooser.setTitle("Select Track File");
        trackChooser.setInitialDirectory((new File(".")));

        // Set Stage, show trackChooser Dialog
        Stage stage = new Stage();
        File file = trackChooser.showOpenDialog(stage);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    musicLibrary.importTrack(file);

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedIOException();
                    }

                    Platform.runLater(() -> {
                        if (!musicLibrary.getTrackObservableList().isEmpty()) {
                            tableViewLibrary.addTrack(musicLibrary.getImportedTrack());
                            addArtistFromImport();
                        }
                    });

                    System.out.printf("Finished importing %s.%n", file.toString());

                } catch (InterruptedIOException consumed) {
                    System.out.println("Import failed.");
                }

                return null;
            }
        };

        task.setOnSucceeded(evt -> {
            System.out.println("Import succeeded.");
            clickArtistNameAfterImport();
        });

        task.setOnFailed(evt -> {
            System.out.println("Import Failed.");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Initialization Failed");
            alert.setContentText("Invalid file type or folder hierarchy.\nCheck console log for details.");
            alert.showAndWait();
        });

        // Start initializeMusicLibrary() thread
        Thread thread = new Thread(task);
        thread.start();

        // Write to File on close
        Platform.runLater(tableViewLibrary::setOutputTrackListOnClose);

    }

    // Add artist name to list view and save to file if not available
    private void addArtistFromImport() {
        if (!listViewLibrary.getArtistObservableList().contains(musicLibrary.getArtistNameStr()) &&
            musicLibrary.getArtistNameStr() != null)
        {
            listViewLibrary.addArtist(musicLibrary.getArtistNameStr());
            artistListView.setItems(listViewLibrary.getArtistObservableList());
        }
    }

    // Simulate mouse click to update tableview with imported metadata
    private void clickArtistNameAfterImport() {
        artistListView.getSelectionModel().select(musicLibrary.getArtistNameStr());

        if (musicLibrary.getArtistNameStr() != null) {
            MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                    false, false, false, false, true, false,
                    false, true, false, false, null);

            artistListView.fireEvent(mouseEvent);
        }
    }

    @FXML
    private void settingsClicked() throws IOException {
        String directoryLabel = userSettings.getRootMusicDirectoryString();
        SettingsController settingsController = new SettingsController();
        settingsController.showSettingsWindow(artistListView, playlistListView, trackTableView, listViewLibrary,
                tableViewLibrary, musicLibrary, userSettings, directoryLabel);
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                         EDIT MENUS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @FXML
    private void addArtistClicked() {
        ArtistListContextMenu.addArtist(artistListView, playlistListView, trackTableView,
                listViewLibrary, tableViewLibrary, trackIndex);
    }

    @FXML
    private void createPlaylistClicked() {
        PlaylistContextMenu.createPlaylist(artistListView, playlistListView, trackTableView,
                listViewLibrary, tableViewLibrary, trackIndex);
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                         HELP MENUS
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML
    private void aboutClicked() throws IOException {
        final String about = "About";

        ViewTextController viewTextController = new ViewTextController();
        viewTextController.showViewTextWindow(about, consoleOutput);

    }

    @FXML
    private void gitHubClicked() {
        Dotenv dotenv = Dotenv.configure().load();
        final String gitHubUrl = dotenv.get("GITHUB");

        HostServices hostServices = (HostServices) stage.getProperties().get("hostServices");
        hostServices.showDocument(gitHubUrl);
    }

    @FXML
    private void consoleLogClicked() throws IOException {
        final String consoleLog = "Console Log";

        ViewTextController viewTextController = new ViewTextController();

        // Check the size to keep TextArea object from locking up Application
        if (consoleOutput.size() < Utils.maxTextAreaSize()) {
            viewTextController.showViewTextWindow(consoleLog, consoleOutput);

        } else {
            System.out.println("File size too large");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Console Log");
            alert.setHeaderText("Console Log size > 1MB.");
            alert.setContentText("Check the consolelog.txt file after closing application\n to check the console log report.");
            alert.showAndWait();
        }
    }

    @FXML
    private void reportBugClicked() throws IOException {
        BugReportController bugReportController = new BugReportController();
        bugReportController.showBugReportWindow(consoleOutput);
    }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *                         ON CLOSE
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @FXML
    private void exitClicked() throws FileNotFoundException {
        // Output to file on close if files data has been altered
        if (userSettings.getWriteOnClose()) {
            SettingsFileIO.jsonFileOutput(userSettings);
        }

        listViewLibrary.onClose();
        tableViewLibrary.onClose();

        // Write console log to file
        ConsoleLogFileIO.outputConsoleLog(consoleOutput.toString());

        stage.close();
    }

}

