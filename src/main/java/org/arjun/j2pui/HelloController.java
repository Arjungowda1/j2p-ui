package org.arjun.j2pui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.*;
import java.time.Duration;
import org.antlr.v4.runtime.*;
import org.arjun.interpreter.JavaLexer;
import org.arjun.interpreter.JavaParser;
import org.arjun.interpreter._interpreter_;

public class HelloController implements Initializable {
    @FXML
    private CodeArea javaCodeInput;

    @FXML
    private CodeArea pythonCodeOutput;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        javaCodeInput.setParagraphGraphicFactory(LineNumberFactory.get(javaCodeInput));
        pythonCodeOutput.setParagraphGraphicFactory(LineNumberFactory.get(pythonCodeOutput));

        Label javaPlaceholder = new Label("Enter your Java code here...");
        javaPlaceholder.getStyleClass().add("placeholder-label");
        
        Label pythonPlaceholder = new Label("Python code will appear here...");
        pythonPlaceholder.getStyleClass().add("placeholder-label");
        
        javaCodeInput.setPlaceholder(javaPlaceholder);
        pythonCodeOutput.setPlaceholder(pythonPlaceholder);
        javaCodeInput.multiPlainChanges()
                .successionEnds(Duration.ofMillis(300))
                .subscribe(ignore -> updateSyntaxHighlighting());
    }

    private void updateSyntaxHighlighting() {
        String text = javaCodeInput.getText();
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        if (text.isEmpty()) {
            spansBuilder.add(Collections.emptyList(), 0);
            javaCodeInput.setStyleSpans(0, spansBuilder.create());
            return;
        }
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(text));
        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = lexer.nextToken()).getType() != Token.EOF) {
            tokens.add(token);
        }

        int lastIndex = 0;

        for (Token currentToken : tokens) {
            int tokenStart = currentToken.getStartIndex();
            int tokenEnd = currentToken.getStopIndex() + 1;
            
            if (tokenStart > lastIndex) {
                spansBuilder.add(Collections.emptyList(), tokenStart - lastIndex);
            }
            
            String styleClass = getStyleClass(currentToken.getType());
            spansBuilder.add(Collections.singleton(styleClass), tokenEnd - tokenStart);
            
            lastIndex = tokenEnd;
        }

        if (lastIndex < text.length()) {
            spansBuilder.add(Collections.emptyList(), text.length() - lastIndex);
        }

        javaCodeInput.setStyleSpans(0, spansBuilder.create());
    }

    private String getStyleClass(int tokenType) {
        return switch (tokenType) {
            case JavaLexer.ABSTRACT, JavaLexer.ASSERT, JavaLexer.BOOLEAN, JavaLexer.BREAK, JavaLexer.BYTE,
                 JavaLexer.CASE, JavaLexer.CATCH, JavaLexer.CLASS, JavaLexer.CONST, JavaLexer.CONTINUE,
                 JavaLexer.DEFAULT, JavaLexer.DO, JavaLexer.DOUBLE, JavaLexer.ELSE, JavaLexer.ENUM, JavaLexer.EXTENDS,
                 JavaLexer.FINAL, JavaLexer.FINALLY, JavaLexer.FLOAT, JavaLexer.FOR, JavaLexer.IF, JavaLexer.IMPLEMENTS,
                 JavaLexer.IMPORT, JavaLexer.INSTANCEOF, JavaLexer.INT, JavaLexer.INTERFACE, JavaLexer.LONG,
                 JavaLexer.NATIVE, JavaLexer.NEW, JavaLexer.PACKAGE, JavaLexer.PRIVATE, JavaLexer.PROTECTED,
                 JavaLexer.PUBLIC, JavaLexer.RETURN, JavaLexer.SHORT, JavaLexer.STATIC, JavaLexer.STRICTFP,
                 JavaLexer.SUPER, JavaLexer.SWITCH, JavaLexer.SYNCHRONIZED, JavaLexer.THIS, JavaLexer.THROW,
                 JavaLexer.THROWS, JavaLexer.TRANSIENT, JavaLexer.TRY, JavaLexer.VOID, JavaLexer.VOLATILE,
                 JavaLexer.WHILE -> "keyword";
            case JavaLexer.STRING_LITERAL -> "string";
            case JavaLexer.COMMENT, JavaLexer.LINE_COMMENT -> "comment";
            case JavaLexer.DECIMAL_LITERAL, JavaLexer.HEX_LITERAL, JavaLexer.OCT_LITERAL, JavaLexer.BINARY_LITERAL,
                 JavaLexer.FLOAT_LITERAL, JavaLexer.HEX_FLOAT_LITERAL -> "number";
            default -> "";
        };
    }

    @FXML
    protected void onConvertButtonClick() {
        String javaCode = javaCodeInput.getText().trim();
        if (javaCode.isEmpty()) {
            showAlert("Empty Input", "Please enter Java code before converting.");
            return;
        }
        String pythonCode = convertJavaToPython(javaCode);
        pythonCodeOutput.replaceText(pythonCode == null || pythonCode.trim().isEmpty() ? "Unable to convert" : pythonCode);
    }

    @FXML
    protected void onDownloadButtonClick() {
        String pythonCode = pythonCodeOutput.getText().trim();
        if (pythonCode.isEmpty()) {
            showAlert("Empty Output", "No Python code to download. Please convert some Java code first.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Python File");
        fileChooser.setInitialFileName("converted.py");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Python Files", "*.py")
        );

        Stage stage = (Stage) pythonCodeOutput.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(pythonCode);
                showAlert("Success", "Python file saved successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to save the file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initOwner(pythonCodeOutput.getScene().getWindow());
        alert.showAndWait();
    }

    private String convertJavaToPython(String javaCode) {
        try {
            CharStream input = CharStreams.fromString(javaCode);
            JavaLexer lexer = new JavaLexer(input);
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            JavaParser parser = new JavaParser(tokenStream);
            _interpreter_ visitor = new _interpreter_();
            return visitor.visit(parser.compilationUnit());
        } catch (Exception exception) {
            return "Fatal Error: Could not parse the syntax";
        }
    }

    @FXML
    private void clearJavaInput() {
        javaCodeInput.clear();
        pythonCodeOutput.clear();
    }
}

