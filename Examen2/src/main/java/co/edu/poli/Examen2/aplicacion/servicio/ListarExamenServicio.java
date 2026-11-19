package co.edu.poli.Examen2.aplicacion.servicio;

import java.util.List;
import java.util.Objects;

import co.edu.poli.Examen2.aplicacion.puerto.entrada.ListarExamenCasoDeUso;
import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;
import co.edu.poli.Examen2.dominio.Examen;

/**
 * Caso de uso de consulta de exámenes.
 */
public class ListarExamenServicio implements ListarExamenCasoDeUso {

    private final ExamenRepository repositorio;

    public ListarExamenServicio(ExamenRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio es obligatorio");
    }

    @Override
    public List<Examen> listar() {
        return repositorio.listar();
    }
}
