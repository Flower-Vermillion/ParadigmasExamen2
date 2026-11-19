package co.edu.poli.Examen2.aplicacion.servicio;

import java.util.List;
import java.util.Objects;

import co.edu.poli.Examen2.aplicacion.puerto.entrada.DeserializarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;
import co.edu.poli.Examen2.dominio.Examen;

/**
 * Caso de uso que carga los exámenes guardados previamente.
 */
public class DeserializarExamenServicio implements DeserializarExamenCasoDeUso {

    private final ExamenRepository repositorio;

    public DeserializarExamenServicio(ExamenRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio");
    }

    @Override
    public List<Examen> deserializar() {
        return repositorio.deserializar();
    }
}
