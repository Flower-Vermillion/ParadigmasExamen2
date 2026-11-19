package co.edu.poli.Examen2.aplicacion.servicio;

import java.util.Objects;

import co.edu.poli.Examen2.aplicacion.puerto.entrada.SerializarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;

/**
 * Caso de uso que guarda el estado actual de los exámenes en el almacenamiento.
 */
public class SerializarExamenServicio implements SerializarExamenCasoDeUso {

    private final ExamenRepository repositorio;

    public SerializarExamenServicio(ExamenRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio");
    }

    @Override
    public void serializar() {
        repositorio.serializar();
    }
}
