/**
 *      Author: Ian Wallace copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: SettingsFileIO.java
 *      Notes: Handles all File I/O for user settings. Accesses the JSON file settings.json
 *              located in resources.
 */

package com.iandw.musicplayerjavafx.FileIO;

import com.iandw.musicplayerjavafx.ResourceURLs;
import com.iandw.musicplayerjavafx.Utilities.UserSettings;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SettingsFileIO {

    /**
     * jsonFileInput() - read JSON objects from file
     * @return => JSONArray object containing all saved user settings
     *              UserSettings include:
     *              1. Root File Directory for user's music library
     *              2. Application Theme selection
     *              3. Music Library initialization type (Standard or Recursive)
     */
    public static JSONArray jsonFileInput() {

        System.out.println("Reading user settings from JSON file.");
        Path jsonURL = Paths.get(ResourceURLs.getSettingsURL());
        JSONParser jsonParser = new JSONParser();
        Object obj = new Object();

        try (FileReader reader = new FileReader(String.valueOf(jsonURL))) {
            obj = jsonParser.parse(reader);

        } catch (ParseException | IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return (JSONArray) obj;
    }

    /**
     * jsonFileOutput() - writes userSettings to file if the user makes any changes
     * @param userSettings => UserSettings object encapsulating all user settings
     */
    public static void jsonFileOutput(UserSettings userSettings) {
        final String rootMusicDirectoryString = userSettings.getRootMusicDirectoryString();
        final String themeFileNameString = userSettings.getThemeFileNameString();
        final String initializationString = userSettings.getInitalizationString();

        JSONObject userSettingsDetails = new JSONObject();
        userSettingsDetails.put("musicLibrary", rootMusicDirectoryString);
        userSettingsDetails.put("themeFileName", themeFileNameString);
        userSettingsDetails.put("initialization", initializationString);

        JSONObject userSettingsObject = new JSONObject();
        userSettingsObject.put("userSettings", userSettingsDetails);

        JSONArray userSettingsList = new JSONArray();
        userSettingsList.add(userSettingsObject);

        try (FileWriter file = new FileWriter(ResourceURLs.getSettingsURL())) {
            System.out.println("Writing user settings to settings.json");
            file.write(userSettingsList.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
