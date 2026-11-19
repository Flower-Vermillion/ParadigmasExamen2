package co.edu.poli.Examen2.dominio;

import java.util.Locale;

/**
 * Examen de sangre registrado para un paciente. Su código lo identifica y un
 * mismo paciente puede tener muchos exámenes.
 * <p>
 * Se recomienda crearlo con {@code ExamenBuilder}.
 */
public class Examen {

    private final String codigo;
    private final double costo;
    private final Paciente paciente;

    /**
     * Crea un examen.
     *
     * @param codigo   código único del examen
     * @param costo    costo del examen
     * @param paciente paciente al que pertenece
     */
    public Examen(String codigo, double costo, Paciente paciente) {
        this.codigo = codigo;
        this.costo = costo;
        this.paciente = paciente;
    }

    /**
     * Devuelve el código del examen.
     *
     * @return el código que identifica al examen
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Devuelve el costo del examen.
     *
     * @return el costo del examen
     */
    public double getCosto() {
        return costo;
    }

    /**
     * Devuelve el paciente del examen.
     *
     * @return el paciente al que pertenece el examen
     */
    public Paciente getPaciente() {
        return paciente;
    }

    /**
     * Calcula la clave usada para comparar exámenes sin distinguir mayúsculas.
     *
     * @return el código en minúsculas, usado para comparar sin distinguir
     *         mayúsculas; {@code null} si el examen no tiene código
     */
    private String clave() {
        return codigo == null ? null : codigo.toLowerCase(Locale.ROOT);
    }

    /**
     * Dos exámenes son iguales si tienen el mismo código, sin distinguir
     * mayúsculas de minúsculas.
     *
     * @param otro objeto a comparar
     * @return {@code true} si ambos exámenes tienen el mismo código
     */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Examen examen)) {
            return false;
        }
        return clave() != null && clave().equals(examen.clave());
    }

    /**
     * Calcula el código hash del examen a partir de su código.
     *
     * @return un valor calculado a partir del código, coherente con
     *         {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return clave() == null ? 0 : clave().hashCode();
    }

    /**
     * Describe el examen en una línea.
     *
     * @return una descripción con el código, el paciente y el costo
     */
    @Override
    public String toString() {
        return codigo + " | " + paciente + " | $" + costo;
    }
}