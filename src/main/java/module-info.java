module com.example.graphwiki {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jsoup;


    opens com.example.graphwiki to javafx.fxml;
    exports com.example.graphwiki;
}