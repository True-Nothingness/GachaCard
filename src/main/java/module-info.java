module com.light.gachacard {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;
    requires org.controlsfx.controls;
    requires java.logging;
    requires java.base;
    opens com.light.gachacard to javafx.fxml;
    exports com.light.gachacard;
}
