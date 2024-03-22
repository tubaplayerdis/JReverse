module com.jreverse.jreverse {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jreverse.jreverse to javafx.fxml;
    exports com.jreverse.jreverse;
    exports com.jreverse.jreverse.Bridge;
    opens com.jreverse.jreverse.Bridge to javafx.fxml;
}