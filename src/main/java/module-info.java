module org.example.bibliotecaterror {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    requires static lombok;
    requires javafx.graphics;




    exports org.example.bibliotecaterror;

    opens org.example.bibliotecaterror to  javafx.fxml;
    opens org.example.bibliotecaterror.controller to javafx.fxml;
    opens org.example.bibliotecaterror.model to com.google.gson, javafx.fxml, javafx.base;

}