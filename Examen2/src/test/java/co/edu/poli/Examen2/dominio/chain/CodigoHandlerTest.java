package co.edu.poli.Examen2.dominio.chain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.Paciente;
import co.edu.poli.Examen2.dominio.TipoSangre;

@DisplayName("Chain of Responsibility: CodigoHandler")
class CodigoHandlerTest {

    private final CodigoHandler handler = new CodigoHandler();

    private static Examen examenConCodigo(String codigo) {
        Paciente paciente = new Paciente("Ana Gómez", TipoSangre.values()[0], FactorRh.POSITIVO);
        return new Examen(codigo, 100, paciente);
    }

    @ParameterizedTest
    @ValueSource(strings = { "EXM-001", "exm-1", "A-0", "Lab-123456" })
    @DisplayName("aprueba códigos con formato LETRAS-NÚMEROS")
    void apruebaCodigosValidos(String codigo) {
        assertTrue(handler.validar(examenConCodigo(codigo)).estaValido());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "EXM001", "EXM-", "-001", "123-001", "EXM-00A", "EXM--001",
            "EXM 001", " EXM-001", "EXM-001 ", "EX1-001" })
    @DisplayName("rechaza códigos con formato inválido")
    void rechazaCodigosInvalidos(String codigo) {
        Resultado resultado = handler.validar(examenConCodigo(codigo));

        assertFalse(resultado.estaValido());
    }

    @Test
    @DisplayName("el rechazo explica el formato esperado")
    void elRechazoExplicaElFormato() {
        Resultado resultado = handler.validar(examenConCodigo("EXM001"));

        assertEquals("El código debe tener el formato LETRAS-NÚMEROS. Ej: EXM-001",
                resultado.getMotivo());
    }
}