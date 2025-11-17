module com.example.ql_khachsan {
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
    requires com.zaxxer.hikari;
    requires java.sql;

    opens com.example.ql_khachsan to javafx.fxml;
    opens com.example.ql_khachsan.controllers to javafx.fxml;

    exports com.example.ql_khachsan;
    exports com.example.ql_khachsan.controllers;
}