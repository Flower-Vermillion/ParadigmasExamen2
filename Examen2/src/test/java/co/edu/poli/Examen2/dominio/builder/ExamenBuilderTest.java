package co.edu.poli.Examen2.dominio.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import co.edu.poli.Examen2.dominio.Examen;
import co.edu.poli.Examen2.dominio.FactorRh;
import co.edu.poli.Examen2.dominio.Paciente;
import co.edu.poli.Examen2.dominio.TipoSangre;

@DisplayName("Patrón Builder: ExamenBuilder")
class ExamenBuilderTest {

    private static Paciente pacienteValido() {
        return new Paciente("Ana Gómez", TipoSangre.values()[0], FactorRh.POSITIVO);
    }

    @Test
    @DisplayName("construye el examen con todos los datos")
    void construyeConDatosCompletos() {
        Paciente paciente = pacienteValido();

        Examen examen = new ExamenBuilder()
                .conCodigo("EXM-001")
                .conCosto(150.50)
                .conPaciente(paciente)
                .build();

        assertEquals("EXM-001", examen.getCodigo());
        assertEquals(150.50, examen.getCosto(), 0.0001);
        assertSame(paciente, examen.getPaciente());
    }

    @Test
    @DisplayName("los métodos 'con...' devuelven el mismo builder para encadenar")
    void permiteEncadenarLlamadas() {
        ExamenBuilder builder = new ExamenBuilder();

        assertSame(builder, builder.conCodigo("EXM-001"));
        assertSame(builder, builder.conCosto(10));
        assertSame(builder, builder.conPaciente(pacienteValido()));
    }

    @Test
    @DisplayName("recorta los espacios del código")
    void recortaEspaciosDelCodigo() {
        Examen examen = new ExamenBuilder()
                .conCodigo("  EXM-002  ")
                .conPaciente(pacienteValido())
                .build();

        assertEquals("EXM-002", examen.getCodigo());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   " })
    @DisplayName("rechaza un código nulo, vacío o en blanco")
    void rechazaCodigoFaltante(String codigo) {
        ExamenBuilder builder = new ExamenBuilder()
                .conCodigo(codigo)
                .conCosto(100)
                .conPaciente(pacienteValido());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("El código del examen es obligatorio.", error.getMessage());
    }

    @Test
    @DisplayName("rechaza un examen sin código definido")
    void rechazaSinCodigoDefinido() {
        ExamenBuilder builder = new ExamenBuilder().conPaciente(pacienteValido());

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    @DisplayName("rechaza un examen sin paciente")
    void rechazaSinPaciente() {
        ExamenBuilder builder = new ExamenBuilder()
                .conCodigo("EXM-003")
                .conCosto(100);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("El examen necesita un paciente.", error.getMessage());
    }

    @Test
    @DisplayName("si faltan código y paciente, informa primero el código")
    void informaPrimeroElCodigo() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new ExamenBuilder().build());

        assertEquals("El código del examen es obligatorio.", error.getMessage());
    }

    @Test
    @DisplayName("no valida el costo: eso es responsabilidad de la cadena")
    void noValidaElCosto() {
        Examen examen = new ExamenBuilder()
                .conCodigo("EXM-004")
                .conCosto(-5)
                .conPaciente(pacienteValido())
                .build();

        assertEquals(-5, examen.getCosto(), 0.0001);
    }

    @Test
    @DisplayName("cada build() crea una instancia nueva")
    void cadaBuildCreaInstanciaNueva() {
        ExamenBuilder builder = new ExamenBuilder()
                .conCodigo("EXM-005")
                .conCosto(20)
                .conPaciente(pacienteValido());

        Examen primero = builder.build();
        Examen segundo = builder.build();

        assertNotSame(primero, segundo);
        assertEquals(primero, segundo);
    }
}