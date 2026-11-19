module co.edu.poli.Examen2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;

    opens co.edu.poli.Examen2.infraestructura.ui to javafx.fxml;

    exports co.edu.poli.Examen2.vista;
}