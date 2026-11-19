package co.edu.poli.Examen2.dominio.chain;

import java.util.regex.Pattern;

import co.edu.poli.Examen2.dominio.Examen;

/**
 * Regla: el código debe tener el formato LETRAS-NÚMEROS, por ejemplo EXM-001.
 */
public class CodigoHandler extends ExamenHandler {

    private static final Pattern FORMATO = Pattern.compile("^[A-Za-z]+-\\d+$");

    @Override
    protected boolean verificar(Examen examen) {
        return examen.getCodigo() != null && FORMATO.matcher(examen.getCodigo()).matches();
    }

    @Override
    protected String motivo() {
        return "El código debe tener el formato LETRAS-NÚMEROS. Ej: EXM-001";
    }
}
