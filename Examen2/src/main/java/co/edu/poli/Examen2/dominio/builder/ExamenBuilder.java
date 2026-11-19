package co.edu.poli.Examen2.dominio.builder;

import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.Paciente;

/**
 * Patrón Builder: arma un {@link Examen} paso a paso y se asegura de que estén
 * todos los datos obligatorios antes de crearlo. Las reglas de negocio sobre
 * los valores (formato del código, costo, datos del paciente) las revisa la
 * cadena de validadores.
 */
public class ExamenBuilder {

    private String codigo;
    private double costo;
    private Paciente paciente;

    public ExamenBuilder conCodigo(String codigo) {
        this.codigo = codigo;
        return this;
    }

    public ExamenBuilder conCosto(double costo) {
        this.costo = costo;
        return this;
    }

    public ExamenBuilder conPaciente(Paciente paciente) {
        this.paciente = paciente;
        return this;
    }

    /**
     * Crea el examen.
     *
     * @return el examen construido
     * @throws IllegalArgumentException si falta el código o el paciente
     */
    public Examen build() {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del examen es obligatorio.");
        }
        if (paciente == null) {
            throw new IllegalArgumentException("El examen necesita un paciente.");
        }
        return new Examen(codigo.trim(), costo, paciente);
    }
}
