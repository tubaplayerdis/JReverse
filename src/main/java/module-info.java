module com.jreverse.jreverse {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;
    requires java.desktop;
    requires org.javassist;
    requires static cfr;
    requires java.scripting;//This worked. only took 12 hours


    opens com.jreverse.jreverse to javafx.fxml;
    exports com.jreverse.jreverse;
    exports com.jreverse.jreverse.Bridge;
    opens com.jreverse.jreverse.Bridge to javafx.fxml;
}