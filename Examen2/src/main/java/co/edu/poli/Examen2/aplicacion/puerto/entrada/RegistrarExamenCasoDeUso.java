package co.edu.poli.Examen2.aplicacion.puerto.entrada;

import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.TipoSangre;

/**
 * Puerto de entrada: registrar un nuevo examen.
 */
public interface RegistrarExamenCasoDeUso {

    /**
     * Valida y registra un examen.
     *
     * @param codigo         código del examen
     * @param costo          costo del examen
     * @param nombrePaciente nombre del paciente
     * @param tipoSangre     grupo sanguíneo del paciente
     * @param factorRh       factor Rh del paciente
     * @throws IllegalArgumentException si los datos no cumplen las reglas de
     *                                  negocio; el mensaje explica el motivo
     */
    void registrar(String codigo, double costo, String nombrePaciente,
                   TipoSangre tipoSangre, FactorRh factorRh);
}
