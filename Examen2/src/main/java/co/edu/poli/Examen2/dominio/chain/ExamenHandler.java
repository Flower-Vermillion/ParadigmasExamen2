package co.edu.poli.Examen2.dominio.chain;

import co.edu.poli.Examen2.dominio.Examen;

/**
 * Patrón Chain of Responsibility: cada eslabón verifica una regla de negocio
 * sobre el examen. Si la cumple, pasa al siguiente; si no, la cadena se
 * detiene y devuelve el motivo del rechazo.
 */
public abstract class ExamenHandler {

    private ExamenHandler siguiente;

    /**
     * Arma la cadena estándar de validación: código, costo y paciente.
     *
     * @return el primer eslabón de la cadena
     */
    public static ExamenHandler crearCadena() {
        ExamenHandler primero = new CodigoHandler();
        primero.setSiguiente(new CostoHandler()).setSiguiente(new PacienteHandler());
        return primero;
    }

    /**
     * @param siguiente eslabón que se ejecuta si este aprueba
     * @return el eslabón recibido, para poder encadenar llamadas
     */
    public ExamenHandler setSiguiente(ExamenHandler siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    /**
     * Valida el examen recorriendo la cadena.
     *
     * @param examen examen a validar
     * @return el resultado de la validación
     */
    public Resultado validar(Examen examen) {
        if (!verificar(examen)) {
            return Resultado.rechazado(motivo());
        }
        return siguiente == null ? Resultado.aprobado() : siguiente.validar(examen);
    }

    /**
     * @param examen examen a revisar
     * @return {@code true} si el examen cumple la regla de este eslabón
     */
    protected abstract boolean verificar(Examen examen);

    /**
     * @return mensaje que explica la regla de este eslabón cuando se incumple
     */
    protected abstract String motivo();
}
