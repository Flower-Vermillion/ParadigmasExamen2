package co.edu.poli.Examen2.aplicacion.puerto.entrada;

import java.util.List;

import co.edu.poli.Examen2.dominio.Examen;

/**
 * Puerto de entrada: cargar los exámenes guardados previamente.
 */
public interface DeserializarExamenCasoDeUso {

    /**
     * Recupera los exámenes del almacenamiento.
     *
     * @return los exámenes cargados
     */
    List<Examen> deserializar();
}
