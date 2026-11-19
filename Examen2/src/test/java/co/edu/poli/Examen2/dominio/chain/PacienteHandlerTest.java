package co.edu.poli.Examen2.dominio.chain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.Paciente;
import co.edu.poli.Examen2.dominio.TipoSangre;

@DisplayName("Chain of Responsibility: PacienteHandler")
class PacienteHandlerTest {

    private static final String NOMBRE_VALIDO = "Ana Gómez";

    private final PacienteHandler handler = new PacienteHandler();

    private static Examen examenCon(Paciente paciente) {
        return new Examen("EXM-001", 100, paciente);
    }

    private static Examen examenConNombre(String nombre) {
        return examenCon(new Paciente(nombre, TipoSangre.A, FactorRh.POSITIVO));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Ana", "Ana Gómez", "María José", "O'Connor", "Jean-Luc",
            "Dr. Pérez", "Ñandú", "  Ana  " })
    @DisplayName("aprueba nombres válidos (letras, espacios, puntos, apóstrofes y guiones)")
    void apruebaNombresValidos(String nombre) {
        assertTrue(handler.validar(examenConNombre(nombre)).estaValido());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   ", "A", "Al", "12", "Ana2", "-Ana", "Ana@", "123456" })
    @DisplayName("rechaza nombres vacíos, cortos o con caracteres no permitidos")
    void rechazaNombresInvalidos(String nombre) {
        assertFalse(handler.validar(examenConNombre(nombre)).estaValido());
    }

    @Test
    @DisplayName("rechaza un examen sin paciente")
    void rechazaPacienteNulo() {
        assertFalse(handler.validar(examenCon(null)).estaValido());
    }

    @Test
    @DisplayName("rechaza un paciente sin tipo de sangre")
    void rechazaSinTipoSangre() {
        Paciente paciente = new Paciente(NOMBRE_VALIDO, null, FactorRh.POSITIVO);

        assertFalse(handler.validar(examenCon(paciente)).estaValido());
    }

    @Test
    @DisplayName("rechaza un paciente sin factor Rh")
    void rechazaSinFactorRh() {
        Paciente paciente = new Paciente(NOMBRE_VALIDO, TipoSangre.O, null);

        assertFalse(handler.validar(examenCon(paciente)).estaValido());
    }

    @ParameterizedTest
    @EnumSource(TipoSangre.class)
    @DisplayName("acepta cada tipo de sangre (A, B, AB y O)")
    void aceptaCadaTipoDeSangre(TipoSangre tipo) {
        Paciente paciente = new Paciente(NOMBRE_VALIDO, tipo, FactorRh.POSITIVO);

        assertTrue(handler.validar(examenCon(paciente)).estaValido());
    }

    @ParameterizedTest
    @EnumSource(FactorRh.class)
    @DisplayName("acepta cada factor Rh (positivo y negativo)")
    void aceptaCadaFactorRh(FactorRh factor) {
        Paciente paciente = new Paciente(NOMBRE_VALIDO, TipoSangre.O, factor);

        assertTrue(handler.validar(examenCon(paciente)).estaValido());
    }

    @Test
    @DisplayName("acepta las ocho combinaciones de tipo de sangre y factor Rh")
    void aceptaTodasLasCombinaciones() {
        int combinaciones = 0;
        for (TipoSangre tipo : TipoSangre.values()) {
            for (FactorRh factor : FactorRh.values()) {
                Paciente paciente = new Paciente(NOMBRE_VALIDO, tipo, factor);

                assertTrue(handler.validar(examenCon(paciente)).estaValido(), tipo + " " + factor);
                combinaciones++;
            }
        }
        assertEquals(8, combinaciones);
    }

    @Test
    @DisplayName("el rechazo explica qué necesita el paciente")
    void elRechazoExplicaLaRegla() {
        Resultado resultado = handler.validar(examenConNombre("Al"));

        assertEquals("El paciente necesita un nombre válido (mínimo 3 letras), tipo de sangre y factor Rh.",
                resultado.getMotivo());
    }
}