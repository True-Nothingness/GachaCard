module com.light.gachacard {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.smartcardio;
    requires org.controlsfx.controls;
    opens com.light.gachacard to javafx.fxml;
    exports com.light.gachacard;
}
