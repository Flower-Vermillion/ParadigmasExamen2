package co.edu.poli.Examen2.aplicacion.puerto.salida;

import java.util.List;

import co.edu.poli.Examen2.dominio.Examen;

/**
 * Puerto de salida: abstrae el almacenamiento de los exámenes. Cada adaptador
 * (archivo, base de datos) lo implementa; los servicios solo conocen esta
 * interfaz.
 */
public interface ExamenRepository {

    /**
     * Guarda un examen.
     *
     * @param examen examen ya validado
     */
    void guardar(Examen examen);

    /**
     * @return los exámenes almacenados
     */
    List<Examen> listar();

    /**
     * Escribe el estado actual en el medio de almacenamiento (por ejemplo, un
     * archivo). Un adaptador puede lanzar {@link UnsupportedOperationException}
     * si su medio ya persiste cada examen al guardarlo.
     */
    void serializar();

    /**
     * Carga el estado almacenado y lo deja como estado actual.
     *
     * @return los exámenes cargados
     */
    List<Examen> deserializar();
}
