module com.example.tx1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;
    requires org.jsoup;

    opens com.example.tx1 to javafx.fxml;
    exports com.example.tx1;
    exports com.example.tx1.Connection;
    opens com.example.tx1.Connection to javafx.fxml;
    exports com.example.tx1.Entity;
    opens com.example.tx1.Entity to javafx.fxml;
    exports com.example.tx1.Controller;
    opens com.example.tx1.Controller to javafx.fxml;
    exports com.example.tx1.DAO;
    opens com.example.tx1.DAO to javafx.fxml;
}