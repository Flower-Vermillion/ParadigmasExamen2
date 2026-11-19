package co.edu.poli.Examen2.infraestructura.ui;

import java.util.List;

import co.edu.poli.Examen2.aplicacion.puerto.entrada.DeserializarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.entrada.ListarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.entrada.RegistrarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.entrada.SerializarExamenCasoDeUso;
import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.Paciente;
import co.edu.poli.Examen2.dominio.TipoSangre;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Button;

/**
 * Adaptador de entrada (JavaFX) del formulario de registro de exámenes.
 * <p>
 * Solo traduce las acciones de la pantalla a llamadas sobre los puertos de
 * entrada (casos de uso) y muestra los resultados. No contiene reglas de
 * negocio ni conoce la persistencia.
 */
public class ControlFormExamen {
	
	@FXML
	private Button btnGuardar;
	@FXML
	private Button btnListar;
	@FXML
	private Button btnSerializar;
	@FXML
	private Button btnDeserializar;
    @FXML
    private TextField txtCodigoExamen;
    @FXML
    private TextField txtNombrePaciente;
    @FXML
    private TextField txtCosto;
    @FXML
    private ComboBox<TipoSangre> cmbTipoSangre;
    @FXML
    private RadioButton rbPositivo;
    @FXML
    private RadioButton rbNegativo;
    @FXML
    private ToggleGroup tgFactorRH;
    @FXML
    private TextArea txtAreaMensajes;

    private final RegistrarExamenCasoDeUso registrarExamen;
    private final ListarExamenCasoDeUso listarExamenes;
    private final SerializarExamenCasoDeUso serializarExamenes;
    private final DeserializarExamenCasoDeUso deserializarExamenes;

    /**
     * Las dependencias llegan por constructor; las entrega
     * {@code App} mediante {@code controllerFactory}.
     */
    public ControlFormExamen(RegistrarExamenCasoDeUso registrarExamen,
                             ListarExamenCasoDeUso listarExamenes,
                             SerializarExamenCasoDeUso serializarExamenes,
                             DeserializarExamenCasoDeUso deserializarExamenes) {
        this.registrarExamen = registrarExamen;
        this.listarExamenes = listarExamenes;
        this.serializarExamenes = serializarExamenes;
        this.deserializarExamenes = deserializarExamenes;
    }

    @FXML
    public void initialize() {
        cmbTipoSangre.getItems().setAll(TipoSangre.values());
    }

    @FXML
    private void handleGuardar() {
        double costo;
        try {
            costo = Double.parseDouble(txtCosto.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            mostrar("El costo debe ser un número. Ej: 150.00");
            return;
        }

        String codigo = txtCodigoExamen.getText();
        String nombre = txtNombrePaciente.getText();
        TipoSangre tipoSangre = cmbTipoSangre.getValue();
        FactorRh factorRh = rbPositivo.isSelected() ? FactorRh.POSITIVO : FactorRh.NEGATIVO;

        try {
            registrarExamen.registrar(codigo, costo, nombre, tipoSangre, factorRh);
            mostrar("Examen " + codigo + " registrado correctamente.");
            limpiarCampos();
        } catch (IllegalArgumentException e) {
            mostrar("No se pudo registrar: " + e.getMessage());
        } catch (RuntimeException e) {
            mostrar("Error inesperado al registrar: " + e.getMessage());
        }
    }

    @FXML
    private void handleListar() {
        try {
            List<Examen> examenes = listarExamenes.listar();
            if (examenes.isEmpty()) {
                mostrar("No hay exámenes registrados.");
                return;
            }
            mostrar("--- Exámenes registrados (" + examenes.size() + ") ---");
            for (Examen examen : examenes) {
                mostrar(formatear(examen));
            }
        } catch (RuntimeException e) {
            mostrar("Error al listar: " + e.getMessage());
        }
    }

    @FXML
    private void handleSerializar() {
        try {
            serializarExamenes.serializar();
            mostrar("Exámenes serializados correctamente.");
        } catch (RuntimeException e) {
            mostrar("Error al serializar: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeserializar() {
        try {
            List<Examen> cargados = deserializarExamenes.deserializar();
            mostrar("Se deserializaron " + cargados.size() + " exámenes.");
        } catch (RuntimeException e) {
            mostrar("Error al deserializar: " + e.getMessage());
        }
    }

    private String formatear(Examen examen) {
        Paciente paciente = examen.getPaciente();
        String rh = paciente.getFactorRh() == FactorRh.POSITIVO ? "+" : "-";
        return String.format("%s | %s | %s%s | $%.2f",
                examen.getCodigo(), paciente.getNombre(),
                paciente.getTipoSangre(), rh, examen.getCosto());
    }

    private void limpiarCampos() {
        txtCodigoExamen.clear();
        txtNombrePaciente.clear();
        txtCosto.clear();
        cmbTipoSangre.getSelectionModel().clearSelection();
        rbPositivo.setSelected(true);
    }

    private void mostrar(String mensaje) {
        txtAreaMensajes.appendText(mensaje + System.lineSeparator());
    }
}
