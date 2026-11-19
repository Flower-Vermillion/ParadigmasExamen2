package co.edu.poli.Examen2.aplicacion.puerto.entrada;

import java.util.List;

import co.edu.poli.Examen2.dominio.Examen;

/**
 * Puerto de entrada: consultar los exámenes registrados.
 */
public interface ListarExamenCasoDeUso {

    /**
     * @return los exámenes registrados
     */
    List<Examen> listar();
}
