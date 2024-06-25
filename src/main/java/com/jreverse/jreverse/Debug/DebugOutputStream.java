package com.jreverse.jreverse.Debug;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.util.Arrays;

public class DebugOutputStream  extends OutputStream {
    private TextArea textArea;
    private String KeepText;

    public DebugOutputStream(TextArea textArea0) {
        if(textArea0 == null) {
            textArea = null;
            return;
        }
        textArea = textArea0;
        textArea.setWrapText(false);
    }

    public void SetTextArea(TextArea textArea1){
        textArea = textArea1;
        textArea.setWrapText(false);
    }

    @Override
    public void write(byte[] b) {
        if(textArea == null) {
            KeepText = KeepText + Arrays.toString(b) + "\n";
            return;
        }
        KeepText = KeepText + Arrays.toString(b) + "\n";

        Platform.runLater(() -> {
            textArea.setText(textArea.getText() + Arrays.toString(b) + "\n");
        });
    }

    @Override
    public void write(byte[] b, int off, int len) {
        if(textArea == null) {
            KeepText = KeepText + new String(b, off, len) + "\n";
            return;
        }
        KeepText = KeepText + new String(b, off, len) + "\n";

        Platform.runLater(() -> {
            textArea.setText(textArea.getText() + new String(b, off, len) + "\n");
        });
    }

    public void write(int b) {
        if(textArea == null) {
            KeepText = KeepText + b + "\n";
            return;
        }
        KeepText = KeepText + b + "\n";

        Platform.runLater(() -> {
            textArea.setText(textArea.getText() + b + "\n");
        });
    }
}
