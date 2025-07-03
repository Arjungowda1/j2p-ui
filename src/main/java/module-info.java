module org.arjun.j2pui {
    requires reactfx;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.fxmisc.richtext;
    requires j2pbridge.main.SNAPSHOT;

    opens org.arjun.j2pui to javafx.fxml;
    exports org.arjun.j2pui;
}