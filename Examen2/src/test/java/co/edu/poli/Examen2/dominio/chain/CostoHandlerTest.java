package co.edu.poli.Examen2.dominio.chain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.Paciente;
import co.edu.poli.Examen2.dominio.TipoSangre;

@DisplayName("Chain of Responsibility: CostoHandler")
class CostoHandlerTest {

    private final CostoHandler handler = new CostoHandler();

    private static Examen examenConCosto(double costo) {
        Paciente paciente = new Paciente("Ana Gómez", TipoSangre.values()[0], FactorRh.POSITIVO);
        return new Examen("EXM-001", costo, paciente);
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0.01, 1, 150.50, 1_000_000 })
    @DisplayName("aprueba costos mayores que cero")
    void apruebaCostosPositivos(double costo) {
        assertTrue(handler.validar(examenConCosto(costo)).estaValido());
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0, -0.01, -150, Double.NaN,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY })
    @DisplayName("rechaza cero, negativos, NaN e infinitos")
    void rechazaCostosInvalidos(double costo) {
        assertFalse(handler.validar(examenConCosto(costo)).estaValido());
    }

    @Test
    @DisplayName("el rechazo explica que el costo debe ser mayor que cero")
    void elRechazoExplicaLaRegla() {
        Resultado resultado = handler.validar(examenConCosto(0));

        assertEquals("El costo debe ser mayor que cero.", resultado.getMotivo());
    }
}