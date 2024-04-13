module com.jreverse.jreverse {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.management;
    requires java.desktop;
    requires org.javassist;
    requires static cfr;//This worked. only took 12 hours
    requires java.scripting;
    requires static jython.standalone;


    opens com.jreverse.jreverse to javafx.fxml;
    exports com.jreverse.jreverse;
    exports com.jreverse.jreverse.Bridge;
    opens com.jreverse.jreverse.Bridge to javafx.fxml;
}