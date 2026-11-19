package co.edu.poli.Examen2.dominio;

import java.util.Locale;

/**
 * Persona a la que se le realiza un examen. Se identifica únicamente por su
 * nombre (sin distinguir mayúsculas), y su tipo de sangre y factor Rh no
 * cambian entre exámenes.
 */
public class Paciente {

    private final String nombre;
    private final TipoSangre tipoSangre;
    private final FactorRh factorRh;

    /**
     * Crea un paciente. No valida los datos: las reglas de negocio las aplica
     * la cadena de validadores antes de registrar el examen.
     *
     * @param nombre     nombre completo del paciente
     * @param tipoSangre grupo sanguíneo
     * @param factorRh   factor Rh
     */
    public Paciente(String nombre, TipoSangre tipoSangre, FactorRh factorRh) {
        this.nombre = nombre == null ? null : nombre.trim();
        this.tipoSangre = tipoSangre;
        this.factorRh = factorRh;
    }

    /**
     * Devuelve el nombre del paciente.
     *
     * @return el nombre del paciente, sin espacios al inicio ni al final
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve el grupo sanguíneo (A, B, AB u O) del paciente.
     *
     * @return el grupo sanguíneo del paciente (A, B, AB u O)
     */
    public TipoSangre getTipoSangre() {
        return tipoSangre;
    }

    /**
     * Devuelve el factor Rh del paciente.
     *
     * @return el factor Rh del paciente
     */
    public FactorRh getFactorRh() {
        return factorRh;
    }

    /**
     * Devuelve el grupo sanguíneo junto con el factor Rh.
     *
     * @return el grupo sanguíneo completo, por ejemplo "O+" o "AB-"
     */
    public String getGrupoSanguineo() {
        if (tipoSangre == null || factorRh == null) {
            return "?";
        }
        return tipoSangre + factorRh.getSimbolo();
    }

    /**
     * Calcula la clave usada para comparar pacientes sin distinguir mayúsculas.
     *
     * @return el nombre en minúsculas, usado para comparar sin distinguir
     *         mayúsculas; {@code null} si el paciente no tiene nombre
     */
    private String clave() {
        return nombre == null ? null : nombre.toLowerCase(Locale.ROOT);
    }

    /**
     * Dos pacientes son iguales si tienen el mismo nombre, sin distinguir
     * mayúsculas de minúsculas.
     *
     * @param otro objeto a comparar
     * @return {@code true} si ambos pacientes tienen el mismo nombre
     */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Paciente paciente)) {
            return false;
        }
        return clave() != null && clave().equals(paciente.clave());
    }

    /**
     * Calcula el código hash del paciente a partir de su nombre.
     *
     * @return un valor calculado a partir del nombre, coherente con
     *         {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return clave() == null ? 0 : clave().hashCode();
    }

    /**
     * Describe al paciente en una línea.
     *
     * @return una descripción con el nombre y el grupo sanguíneo
     */
    @Override
    public String toString() {
        return nombre + " (" + getGrupoSanguineo() + ")";
    }
}