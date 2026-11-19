package co.edu.poli.Examen2.infraestructura.persistencia.decorator;

import java.util.List;

import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;
import co.edu.poli.Examen2.dominio.Examen;

/**
 * Decorador que guarda en memoria el último listado y lo reutiliza hasta que
 * cambie el contenido del repositorio (al guardar o deserializar).
 */
public class ExamenRepositoryConCache extends ExamenRepositoryDecorator {

    private List<Examen> cache;

    public ExamenRepositoryConCache(ExamenRepository interno) {
        super(interno);
    }

    @Override
    public void guardar(Examen examen) {
        super.guardar(examen);
        cache = null;
    }

    @Override
    public List<Examen> listar() {
        if (cache == null) {
            cache = List.copyOf(super.listar());
        }
        return cache;
    }

    @Override
    public List<Examen> deserializar() {
        List<Examen> cargados = super.deserializar();
        cache = null;
        return cargados;
    }
}
