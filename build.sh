#!/bin/bash
java --module-path "$(dirname "$0")" \
     --add-modules javafx.controls,javafx.fxml \
     -Djava.library.path="$(dirname "$0")" \
     -jar "$(dirname "$0")/j2p-ui.jar"


jpackage \
 --type dmg \
 --name J2PUI_def \
 --input out/artifacts/j2p_ui_jar \
 --main-jar j2p-ui.jar \
 --main-class org.arjun.j2pui.Main \
 --add-modules javafx.controls,javafx.fxml\
 --module-path /Users/arjun/Desktop/Projects/javafx/javafx-sdk-17.0.15/lib \
 --dest dist

 jlink \
   --module-path "$JAVA_HOME/jmods:/Users/arjun/Desktop/Projects/javafx/javafx-sdk-17.0.15/lib" \
   --add-modules java.base,javafx.controls,javafx.fxml \
   --output custom-runtime


echo 'export JAVA_HOME="/Users/arjun/Library/Java/JavaVirtualMachines/corretto-17.0.13/Contents/Home"' >> ~/.zshrc
source ~/.zshrc


java -jar ./build/libs/j2p-ui.jar --module-path /Users/arjun/Desktop/Projects/javafx/javafx-sdk-17.0.15/lib --add-modules=javafx.controls,javafx.fxml


jpackage \
 --type dmg \
 --name J2PUIII \
 --input out/artifacts/j2p_ui_jar \
 --main-jar j2p-ui.jar \
 --main-class org.arjun.j2pui.Main \
 --runtime-image custom-runtime \
 --dest dist


  ./build/image/bin/j2papp
