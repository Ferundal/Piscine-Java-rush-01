module school21.cjettie.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens school21.cjettie.client to javafx.fxml;
    exports school21.cjettie.client;
}