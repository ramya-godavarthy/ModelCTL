module modelCTL.java {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

   // opens modelCTL_java;
    opens modelCTL_java.Model;
    opens modelCTL_java.Controller;
    opens modelCTL_java.View;

}