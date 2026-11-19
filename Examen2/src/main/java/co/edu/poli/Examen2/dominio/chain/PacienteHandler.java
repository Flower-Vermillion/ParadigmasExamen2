package co.edu.poli.Examen2.dominio.chain;

import java.util.regex.Pattern;

import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.Paciente;

/**
 * Regla: el paciente debe tener un nombre válido (mínimo 3 caracteres, con
 * letras, espacios, puntos, apóstrofes o guiones), tipo de sangre y factor Rh.
 */
public class PacienteHandler extends ExamenHandler {

    private static final Pattern NOMBRE = Pattern.compile("^\\p{L}[\\p{L} .'-]{2,}$");

    @Override
    protected boolean verificar(Examen examen) {
        Paciente paciente = examen.getPaciente();
        return paciente != null
                && paciente.getNombre() != null
                && NOMBRE.matcher(paciente.getNombre()).matches()
                && paciente.getTipoSangre() != null
                && paciente.getFactorRh() != null;
    }

    @Override
    protected String motivo() {
        return "El paciente necesita un nombre válido (mínimo 3 letras), tipo de sangre y factor Rh.";
    }
}
