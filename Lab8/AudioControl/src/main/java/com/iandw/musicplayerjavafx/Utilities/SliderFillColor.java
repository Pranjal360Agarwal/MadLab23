/**
 *      Author: Ian Wallace, copyright 2022 all rights reserved.
 *      Application: MusicPlayer
 *      Class: SliderFillColor.java
 *      Notes: Fills slider track objects (volume and seekSlider) with a color up to
 *              the slider's thumb.
 */

package com.iandw.musicplayerjavafx.Utilities;

public class SliderFillColor {

    private static final String lightStyle =
            "-track-color: linear-gradient(to right, " +
                    "-fx-accent 0%%, " +
                    "-fx-accent %1$.1f%%, " +
                    "white %1$.1f%%, " +
                    "white 100%%);";

    private static final String darkStyle =
            "-track-color: linear-gradient(to right, " +
                    "#1f3d7a 0%%, " +
                    "#1f3d7a %1$.1f%%, " +
                    "#363840 %1$.1f%%, " +
                    "#363840 100%%);";

    private static final String greenStyle =
            "-track-color: linear-gradient(to right, " +
                    "#3cb478 0%%, " +
                    "#3cb478 %1$.1f%%, " +
                    "#e6ffe6 %1$.1f%%, " +
                    "#e6ffe6 100%%);";

    private static final String blueStyle =
            "-track-color: linear-gradient(to right, " +
                    "#1daddd 0%%, " +
                    "#1daddd %1$.1f%%, " +
                    "#e5f9ff %1$.1f%%, " +
                    "#e5f9ff 100%%);";

    private static final String redStyle =
            "-track-color: linear-gradient(to right, " +
                    "#c70000 0%%, " +
                    "#c70000 %1$.1f%%, " +
                    "#ffe5e5 %1$.1f%%, " +
                    "#ffe5e5 100%%);";

    private static final String pinkStyle =
            "-track-color: linear-gradient(to right, " +
                    "#b43cb4 0%%, " +
                    "#b43cb4 %1$.1f%%, " +
                    "#ffe5f9 %1$.1f%%, " +
                    "#ffe5f9 100%%);";

    private static final String consoleStyle =
            "-track-color: linear-gradient(to right, " +
                    "#367d36 0%%, " +
                    "#367d36 %1$.1f%%, " +
                    "#363840 %1$.1f%%, " +
                    "#363840 100%%);";


    public static String getStyle(String currentTheme) {
        final String light = "style-light.css";
        final String dark = "style-dark.css";
        final String green = "style-green.css";
        final String blue = "style-blue.css";
        final String red = "style-red.css";
        final String pink = "style-pink.css";
        final String console = "style-console.css";

        switch (currentTheme) {
            case light -> { return lightStyle; }
            case dark  -> { return darkStyle;  }
            case green -> { return greenStyle; }
            case blue  -> { return blueStyle;  }
            case red   -> { return redStyle; }
            case pink  -> { return pinkStyle;  }
            case console -> { return consoleStyle; }
        }

        // default return
        return lightStyle;
    }
}
