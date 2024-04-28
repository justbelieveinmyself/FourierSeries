module com.justbelieveinmyself.fourierseries {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.justbelieveinmyself.fourierseries to javafx.fxml;
    exports com.justbelieveinmyself.fourierseries;
}