module com.example.uts_project {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.uts_project to javafx.fxml;
    exports com.example.uts_project;
}