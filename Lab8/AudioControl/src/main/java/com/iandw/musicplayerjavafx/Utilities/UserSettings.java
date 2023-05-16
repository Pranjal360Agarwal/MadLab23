/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: UserSettings.java
 *      Notes: Holds user setting data parsed from JSON file:
 *              - user's root directory path
 *              - currently selected App Theme
 *              - Music Library initialization choice (when resetting the library)
 */

package com.iandw.musicplayerjavafx.Utilities;

import com.iandw.musicplayerjavafx.FileIO.SettingsFileIO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class UserSettings implements Runnable {
    private String rootMusicDirectoryString;
    private String themeFileNameString;
    private String initializationString;
    private boolean writeOnClose;

    public UserSettings() {}

    @Override
    public void run() {
        JSONArray jsonArray = SettingsFileIO.jsonFileInput();
        jsonArray.forEach( settings -> parseSettingsObject( (JSONObject) settings));

        System.out.println("Root Directory: " + rootMusicDirectoryString);
        System.out.println("Theme File: " + themeFileNameString);
    }

    private void parseSettingsObject(JSONObject settings) {
        JSONObject settingObject = (JSONObject) settings.get("userSettings");

        // Get root directory path
        rootMusicDirectoryString = (String) settingObject.get("musicLibrary");

        // Get user css style selected
        themeFileNameString = (String) settingObject.get("themeFileName");

        // Get user initialization preference
        initializationString = (String) settingObject.get("initialization");
    }

    public void setRootMusicDirectoryString(String rootMusicDirectoryString) {
        this.rootMusicDirectoryString = rootMusicDirectoryString;
        writeOnClose = true;
    }
    public void setThemeFileNameString(String themeFileNameString) {
        this.themeFileNameString = themeFileNameString;
        writeOnClose = true;
    }

    public void setInitializationString(String initializationString) {
        this.initializationString = initializationString;
        writeOnClose = true;
    }


    public String getRootMusicDirectoryString() { return rootMusicDirectoryString; }
    public String getThemeFileNameString() { return themeFileNameString; }
    public String getInitalizationString() { return initializationString; }
    public boolean getWriteOnClose() { return writeOnClose; }
}
