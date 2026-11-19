package co.edu.poli.Examen2.vista;

import java.io.IOException;

import co.edu.poli.Examen2.aplicacion.puerto.entrada.DeserializarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.entrada.ListarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.entrada.RegistrarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.entrada.SerializarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;
import co.edu.poli.Examen2.aplicacion.servicio.DeserializarExamenServicio;
import co.edu.poli.Examen2.aplicacion.servicio.ListarExamenServicio;
import co.edu.poli.Examen2.aplicacion.servicio.RegistrarExamenServicio;
import co.edu.poli.Examen2.aplicacion.servicio.SerializarExamenServicio;
import co.edu.poli.Examen2.infraestructura.persistencia.ArchivoExamenRepository;
import co.edu.poli.Examen2.infraestructura.persistencia.ConexionBD;
import co.edu.poli.Examen2.infraestructura.persistencia.decorator.ExamenRepositoryConCache;
import co.edu.poli.Examen2.infraestructura.persistencia.decorator.ExamenRepositoryConLog;
import co.edu.poli.Examen2.infraestructura.ui.ControlFormExamen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación JavaFX. Actúa como raíz de composición: es el único lugar que
 * conoce las implementaciones concretas y las conecta entre sí.
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Adaptador de salida base, envuelto por los decoradores.
        // Para usar MySQL: new MySqlExamenRepository() en lugar de ArchivoExamenRepository.
        ExamenRepository repositorio = new ExamenRepositoryConLog(
                new ExamenRepositoryConCache(new ArchivoExamenRepository()));

        // Servicios de aplicación (implementan los puertos de entrada).
        RegistrarExamenCasoDeUso registrar = new RegistrarExamenServicio(repositorio);
        ListarExamenCasoDeUso listar = new ListarExamenServicio(repositorio);
        SerializarExamenCasoDeUso serializar = new SerializarExamenServicio(repositorio);
        DeserializarExamenCasoDeUso deserializar = new DeserializarExamenServicio(repositorio);

        FXMLLoader loader = new FXMLLoader(App.class.getResource("/co/edu/poli/Examen2/primary.fxml"));
        loader.setControllerFactory(clase ->
                new ControlFormExamen(registrar, listar, serializar, deserializar));
        Parent raiz = loader.load();

        scene = new Scene(raiz, 640, 580);
        stage.setTitle("Exámenes de Sangre");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /** Se ejecuta al cerrar la ventana: libera la conexión compartida a MySQL. */
    @Override
    public void stop() {
        ConexionBD.getInstancia().cerrarConexion();
    }

    public static void main(String[] args) {
        launch();
    }
}