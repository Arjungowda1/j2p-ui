module org.arjun.j2pui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.kordamp.bootstrapfx.core;
    requires org.fxmisc.richtext;
    requires j2pbridge.main.SNAPSHOT;

    opens org.arjun.j2pui to javafx.fxml;
    exports org.arjun.j2pui;
}