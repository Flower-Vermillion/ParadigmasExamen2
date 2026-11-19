package co.edu.poli.Examen2.dominio.chain;

/**
 * Resultado de validar un examen con la cadena de responsabilidad.
 */
public final class Resultado {

    private final boolean valido;
    private final String motivo;

    private Resultado(boolean valido, String motivo) {
        this.valido = valido;
        this.motivo = motivo;
    }

    /**
     * @return un resultado que indica que el examen cumple todas las reglas
     */
    public static Resultado aprobado() {
        return new Resultado(true, "");
    }

    /**
     * @param motivo explicación de la regla incumplida
     * @return un resultado que indica que el examen fue rechazado
     */
    public static Resultado rechazado(String motivo) {
        return new Resultado(false, motivo);
    }

    public boolean estaValido() {
        return valido;
    }

    public String getMotivo() {
        return motivo;
    }
}
