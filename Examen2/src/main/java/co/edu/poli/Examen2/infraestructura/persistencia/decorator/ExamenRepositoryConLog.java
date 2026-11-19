package co.edu.poli.Examen2.infraestructura.persistencia.decorator;

import java.util.List;

import co.edu.poli.Examen2.aplicacion.puerto.salida.ExamenRepository;
import co.edu.poli.Examen2.dominio.Examen;

/**
 * Decorador que registra en el log cada operación del repositorio.
 */
public class ExamenRepositoryConLog extends ExamenRepositoryDecorator {

    private static final System.Logger LOG =
            System.getLogger(ExamenRepositoryConLog.class.getName());

    public ExamenRepositoryConLog(ExamenRepository interno) {
        super(interno);
    }

    @Override
    public void guardar(Examen examen) {
        LOG.log(System.Logger.Level.INFO, "Guardando examen {0}", examen.getCodigo());
        super.guardar(examen);
    }

    @Override
    public List<Examen> listar() {
        List<Examen> resultado = super.listar();
        LOG.log(System.Logger.Level.INFO, "Listando exámenes: {0} encontrados", resultado.size());
        return resultado;
    }

    @Override
    public void serializar() {
        LOG.log(System.Logger.Level.INFO, "Serializando exámenes");
        super.serializar();
    }

    @Override
    public List<Examen> deserializar() {
        LOG.log(System.Logger.Level.INFO, "Deserializando exámenes");
        return super.deserializar();
    }
}
