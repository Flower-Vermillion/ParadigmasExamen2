package co.edu.poli.Examen2.dominio;

/**
 * Factor Rh de una persona: positivo o negativo.
 */
public enum FactorRh {
    POSITIVO("+"),
    NEGATIVO("-");

    private final String simbolo;

    FactorRh(String simbolo) {
        this.simbolo = simbolo;
    }

    /**
     * @return el símbolo usado en la notación habitual del grupo sanguíneo ("+" o "-")
     */
    public String getSimbolo() {
        return simbolo;
    }
}
