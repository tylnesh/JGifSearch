module com.lordsoftech {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires com.jfoenix;

    opens com.lordsoftech to javafx.fxml;
    exports com.lordsoftech;
}
