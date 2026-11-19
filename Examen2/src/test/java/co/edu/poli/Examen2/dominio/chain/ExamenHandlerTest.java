package co.edu.poli.Examen2.dominio.chain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.Paciente;
import co.edu.poli.Examen2.dominio.TipoSangre;

@DisplayName("Chain of Responsibility: recorrido de la cadena")
class ExamenHandlerTest {

    /** Eslabón de prueba: cuenta cuántas veces lo consultan. */
    private static class Espia extends ExamenHandler {
        private final boolean aprueba;
        private final String motivo;
        int llamadas = 0;

        Espia(boolean aprueba, String motivo) {
            this.aprueba = aprueba;
            this.motivo = motivo;
        }

        @Override
        protected boolean verificar(Examen examen) {
            llamadas++;
            return aprueba;
        }

        @Override
        protected String motivo() {
            return motivo;
        }
    }

    private static Paciente pacienteValido() {
        return new Paciente("Ana Gómez", TipoSangre.values()[0], FactorRh.POSITIVO);
    }

    private static Examen examenValido() {
        return new Examen("EXM-001", 150, pacienteValido());
    }

    // ---------- Cadena estándar ----------

    @Nested
    @DisplayName("cadena estándar (crearCadena)")
    class CadenaEstandar {

        private final ExamenHandler cadena = ExamenHandler.crearCadena();

        @Test
        @DisplayName("aprueba un examen que cumple todas las reglas")
        void apruebaExamenValido() {
            Resultado resultado = cadena.validar(examenValido());

            assertTrue(resultado.estaValido());
            assertEquals("", resultado.getMotivo());
        }

        @Test
        @DisplayName("rechaza por código inválido")
        void rechazaPorCodigo() {
            Resultado resultado = cadena.validar(new Examen("EXM001", 150, pacienteValido()));

            assertFalse(resultado.estaValido());
            assertEquals("El código debe tener el formato LETRAS-NÚMEROS. Ej: EXM-001",
                    resultado.getMotivo());
        }

        @Test
        @DisplayName("rechaza por costo inválido")
        void rechazaPorCosto() {
            Resultado resultado = cadena.validar(new Examen("EXM-001", 0, pacienteValido()));

            assertFalse(resultado.estaValido());
            assertEquals("El costo debe ser mayor que cero.", resultado.getMotivo());
        }

        @Test
        @DisplayName("rechaza por paciente inválido")
        void rechazaPorPaciente() {
            Paciente sinRh = new Paciente("Ana Gómez", TipoSangre.values()[0], null);

            Resultado resultado = cadena.validar(new Examen("EXM-001", 150, sinRh));

            assertFalse(resultado.estaValido());
            assertEquals("El paciente necesita un nombre válido (mínimo 3 letras), tipo de sangre y factor Rh.",
                    resultado.getMotivo());
        }

        @Test
        @DisplayName("con varios errores, informa el primero de la cadena (código)")
        void informaElPrimerError() {
            Paciente invalido = new Paciente("A", null, null);

            Resultado resultado = cadena.validar(new Examen("???", -1, invalido));

            assertFalse(resultado.estaValido());
            assertEquals("El código debe tener el formato LETRAS-NÚMEROS. Ej: EXM-001",
                    resultado.getMotivo());
        }

        @Test
        @DisplayName("con código válido y varios errores más, informa el costo antes que el paciente")
        void respetaElOrdenCostoLuegoPaciente() {
            Paciente invalido = new Paciente("A", null, null);

            Resultado resultado = cadena.validar(new Examen("EXM-001", -1, invalido));

            assertEquals("El costo debe ser mayor que cero.", resultado.getMotivo());
        }
    }

    // ---------- Mecánica del patrón ----------

    @Nested
    @DisplayName("mecánica del patrón")
    class MecanicaDelPatron {

        @Test
        @DisplayName("setSiguiente devuelve el eslabón recibido para encadenar")
        void setSiguienteDevuelveElRecibido() {
            ExamenHandler primero = new Espia(true, "a");
            ExamenHandler segundo = new Espia(true, "b");

            assertSame(segundo, primero.setSiguiente(segundo));
        }

        @Test
        @DisplayName("un eslabón sin siguiente aprueba si cumple su regla")
        void eslabonSolitarioAprueba() {
            Espia solo = new Espia(true, "regla");

            assertTrue(solo.validar(examenValido()).estaValido());
            assertEquals(1, solo.llamadas);
        }

        @Test
        @DisplayName("si un eslabón aprueba, pasa al siguiente")
        void siApruebaPasaAlSiguiente() {
            Espia primero = new Espia(true, "primero");
            Espia segundo = new Espia(true, "segundo");
            primero.setSiguiente(segundo);

            Resultado resultado = primero.validar(examenValido());

            assertTrue(resultado.estaValido());
            assertEquals(1, primero.llamadas);
            assertEquals(1, segundo.llamadas);
        }

        @Test
        @DisplayName("si un eslabón rechaza, la cadena se detiene y no consulta al siguiente")
        void siRechazaLaCadenaSeDetiene() {
            Espia primero = new Espia(false, "falló el primero");
            Espia segundo = new Espia(true, "segundo");
            primero.setSiguiente(segundo);

            Resultado resultado = primero.validar(examenValido());

            assertFalse(resultado.estaValido());
            assertEquals("falló el primero", resultado.getMotivo());
            assertEquals(0, segundo.llamadas);
        }

        @Test
        @DisplayName("el rechazo de un eslabón intermedio devuelve su propio motivo")
        void rechazoIntermedio() {
            Espia primero = new Espia(true, "primero");
            Espia segundo = new Espia(false, "falló el segundo");
            Espia tercero = new Espia(true, "tercero");
            primero.setSiguiente(segundo).setSiguiente(tercero);

            Resultado resultado = primero.validar(examenValido());

            assertEquals("falló el segundo", resultado.getMotivo());
            assertEquals(1, primero.llamadas);
            assertEquals(1, segundo.llamadas);
            assertEquals(0, tercero.llamadas);
        }
    }

    // ---------- Resultado ----------

    @Nested
    @DisplayName("Resultado")
    class PruebasDeResultado {

        @Test
        @DisplayName("aprobado es válido y no tiene motivo")
        void aprobado() {
            Resultado resultado = Resultado.aprobado();

            assertTrue(resultado.estaValido());
            assertEquals("", resultado.getMotivo());
        }

        @Test
        @DisplayName("rechazado no es válido y conserva el motivo")
        void rechazado() {
            Resultado resultado = Resultado.rechazado("Motivo de prueba");

            assertFalse(resultado.estaValido());
            assertEquals("Motivo de prueba", resultado.getMotivo());
        }
    }
}