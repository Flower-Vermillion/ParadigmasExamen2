package co.edu.poli.Examen2.dominio.chain;

import co.edu.poli.Examen2.dominio.Examen;

/**
 * Regla: el costo debe ser un número mayor que cero.
 */
public class CostoHandler extends ExamenHandler {

    @Override
    protected boolean verificar(Examen examen) {
        return Double.isFinite(examen.getCosto()) && examen.getCosto() > 0;
    }

    @Override
    protected String motivo() {
        return "El costo debe ser mayor que cero.";
    }
}
