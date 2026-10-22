module com.empresa {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    exports com.empresa;
    opens com.empresa to javafx.fxml;
    opens com.empresa.controller to javafx.fxml;
}