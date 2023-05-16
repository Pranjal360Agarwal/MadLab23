module com.iandw.musicplayerjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.media;
    requires java.desktop;
    requires json.simple;
    requires jaudiotagger;
    requires javafx.web;
    requires org.jsoup;
    requires jakarta.mail;
    requires io.github.cdimascio.dotenv.java;
    requires org.eclipse.angus.activation;

    opens com.iandw.musicplayerjavafx to javafx.fxml;
    exports com.iandw.musicplayerjavafx;
    exports com.iandw.musicplayerjavafx.Utilities;
    opens com.iandw.musicplayerjavafx.Utilities to javafx.fxml;
    exports com.iandw.musicplayerjavafx.FileIO;
    opens com.iandw.musicplayerjavafx.FileIO to javafx.fxml;
    exports com.iandw.musicplayerjavafx.ContextMenus;
    opens com.iandw.musicplayerjavafx.ContextMenus to javafx.fxml;
    exports com.iandw.musicplayerjavafx.Libraries;
    opens com.iandw.musicplayerjavafx.Libraries to javafx.fxml;
}