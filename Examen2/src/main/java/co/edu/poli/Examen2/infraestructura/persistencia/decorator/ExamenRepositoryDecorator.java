package co.edu.poli.Examen2.infraestructura.persistencia.decorator;

import java.util.List;
import java.util.Objects;

import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;
import co.edu.poli.Examen2.dominio.Examen;

/**
 * Patrón Decorator: base de los decoradores del repositorio. Implementa el
 * mismo puerto, envuelve a otro repositorio y delega en él todas las
 * operaciones; cada decorador concreto sobrescribe las que necesita.
 */
public abstract class ExamenRepositoryDecorator implements ExamenRepository {

    protected final ExamenRepository interno;

    protected ExamenRepositoryDecorator(ExamenRepository interno) {
        this.interno = Objects.requireNonNull(interno, "El repositorio interno es obligatorio");
    }

    @Override
    public void guardar(Examen examen) {
        interno.guardar(examen);
    }

    @Override
    public List<Examen> listar() {
        return interno.listar();
    }

    @Override
    public void serializar() {
        interno.serializar();
    }

    @Override
    public List<Examen> deserializar() {
        return interno.deserializar();
    }
}
